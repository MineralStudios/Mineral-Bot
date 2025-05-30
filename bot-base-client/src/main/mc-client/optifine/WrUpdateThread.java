package optifine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import org.lwjgl.opengl.Pbuffer;

import java.util.LinkedList;
import java.util.List;

public class WrUpdateThread extends Thread {
    private Pbuffer pbuffer = null;
    private final Object lock = new Object();
    private final List<WorldRenderer> updateList = new LinkedList<>();
    private final List<WorldRenderer> updatedList = new LinkedList<>();
    private int updateCount = 0;
    private final Tessellator mainTessellator;
    private final Tessellator threadTessellator;
    private boolean working;
    private WorldRendererThreaded currentRenderer;
    private boolean canWork;
    private boolean canWorkToEndOfUpdate;
    private boolean terminated;
    private static final int MAX_UPDATE_CAPACITY = 10;
    private final Minecraft mc;

    public WrUpdateThread(Minecraft mc, Pbuffer pbuffer) {
        super("WrUpdateThread");
        this.mc = mc;
        this.mainTessellator = mc.getTessellator();
        this.threadTessellator = new Tessellator(2097152);
        this.working = false;
        this.currentRenderer = null;
        this.canWork = false;
        this.canWorkToEndOfUpdate = false;
        this.terminated = false;
        this.pbuffer = pbuffer;
    }

    public void run() {
        try {
            this.pbuffer.makeCurrent();
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        WrUpdateThread.ThreadUpdateListener updateListener = new WrUpdateThread.ThreadUpdateListener(
                null);

        while (!Thread.interrupted() && !this.terminated) {
            try {
                WorldRendererThreaded e = this.getRendererToUpdate();

                if (e == null) {
                    return;
                }

                this.checkCanWork(null);

                try {
                    this.currentRenderer = e;
                    this.mc.setTessellator(this.threadTessellator);
                    e.updateRenderer(updateListener);
                } finally {
                    this.mc.setTessellator(this.mainTessellator);
                }

                this.rendererUpdated(e);
            } catch (Exception var9) {
                var9.printStackTrace();

                if (this.currentRenderer != null) {
                    this.currentRenderer.isUpdating = false;
                    this.currentRenderer.needsUpdate = true;
                }

                this.currentRenderer = null;
                this.working = false;
            }
        }
    }

    public void addRendererToUpdate(WorldRenderer wr, boolean first) {
        Object var3 = this.lock;

        synchronized (this.lock) {
            if (wr.isUpdating) {
                throw new IllegalArgumentException("Renderer already updating");
            } else {
                if (first) {
                    this.updateList.add(0, wr);
                } else {
                    this.updateList.add(wr);
                }

                wr.isUpdating = true;
                this.lock.notifyAll();
            }
        }
    }

    private WorldRendererThreaded getRendererToUpdate() {
        Object var1 = this.lock;

        synchronized (this.lock) {
            while (this.updateList.size() <= 0) {
                try {
                    this.lock.wait(2000L);

                    if (this.terminated) {
                        Object var10000 = null;
                        return (WorldRendererThreaded) var10000;
                    }
                } catch (InterruptedException var4) {
                }
            }

            WorldRendererThreaded wrt = (WorldRendererThreaded) this.updateList.remove(0);
            this.lock.notifyAll();
            return wrt;
        }
    }

    public boolean hasWorkToDo() {
        Object var1 = this.lock;

        synchronized (this.lock) {
            return this.updateList.size() > 0 || (this.currentRenderer != null || this.working);
        }
    }

    public int getUpdateCapacity() {
        Object var1 = this.lock;

        synchronized (this.lock) {
            return this.updateList.size() > 10 ? 0 : 10 - this.updateList.size();
        }
    }

    private void rendererUpdated(WorldRenderer wr) {
        Object var2 = this.lock;

        synchronized (this.lock) {
            this.updatedList.add(wr);
            ++this.updateCount;
            this.currentRenderer = null;
            this.working = false;
            this.lock.notifyAll();
        }
    }

    private void finishUpdatedRenderers() {
        Object var1 = this.lock;

        synchronized (this.lock) {
            for (int i = 0; i < this.updatedList.size(); ++i) {
                WorldRendererThreaded wr = (WorldRendererThreaded) this.updatedList.get(i);
                wr.finishUpdate();
                wr.isUpdating = false;
            }

            this.updatedList.clear();
        }
    }

    public void pause() {
        Object var1 = this.lock;

        synchronized (this.lock) {
            this.canWork = false;
            this.canWorkToEndOfUpdate = false;
            this.lock.notifyAll();

            while (this.working) {
                try {
                    this.lock.wait();
                } catch (InterruptedException var4) {
                }
            }

            this.finishUpdatedRenderers();
        }
    }

    public void unpause() {
        Object var1 = this.lock;

        synchronized (this.lock) {
            if (this.working) {
                Config.warn("UpdateThread still working in unpause()!!!");
            }

            this.canWork = true;
            this.canWorkToEndOfUpdate = false;
            this.lock.notifyAll();
        }
    }

    public void unpauseToEndOfUpdate() {
        Object var1 = this.lock;

        synchronized (this.lock) {
            if (this.working) {
                Config.warn("UpdateThread still working in unpause()!!!");
            }

            if (this.currentRenderer != null) {
                while (this.currentRenderer != null) {
                    this.canWork = false;
                    this.canWorkToEndOfUpdate = true;
                    this.lock.notifyAll();

                    try {
                        this.lock.wait();
                    } catch (InterruptedException var4) {
                    }
                }

                this.pause();
            }
        }
    }

    private void checkCanWork(IWrUpdateControl uc) {
        Thread.yield();
        Object var2 = this.lock;

        synchronized (this.lock) {
            while (!this.canWork && (!this.canWorkToEndOfUpdate || this.currentRenderer == null)) {
                if (uc != null) {
                    uc.pause();
                }

                this.working = false;
                this.lock.notifyAll();

                try {
                    this.lock.wait();
                } catch (InterruptedException var5) {
                }
            }

            this.working = true;

            if (uc != null) {
                uc.resume();
            }

            this.lock.notifyAll();
        }
    }

    public void clearAllUpdates() {
        Object var1 = this.lock;

        synchronized (this.lock) {
            this.unpauseToEndOfUpdate();

            for (int i = 0; i < this.updateList.size(); ++i) {
                WorldRenderer wr = this.updateList.get(i);
                wr.needsUpdate = true;
                wr.isUpdating = false;
            }

            this.updateList.clear();
            this.lock.notifyAll();
        }
    }

    public int getPendingUpdatesCount() {
        Object var1 = this.lock;

        synchronized (this.lock) {
            int count = this.updateList.size();

            if (this.currentRenderer != null) {
                ++count;
            }

            return count;
        }
    }

    public int resetUpdateCount() {
        Object var1 = this.lock;

        synchronized (this.lock) {
            int count = this.updateCount;
            this.updateCount = 0;
            return count;
        }
    }

    public void terminate() {
        this.terminated = true;
    }

    static class NamelessClass750854124 {
    }

    private class ThreadUpdateControl implements IWrUpdateControl {
        private IWrUpdateControl updateControl;
        private boolean paused;

        private ThreadUpdateControl() {
            this.updateControl = null;
            this.paused = false;
        }

        public void pause() {
            if (!this.paused) {
                this.paused = true;
                this.updateControl.pause();
                WrUpdateThread.this.mc.setTessellator(WrUpdateThread.this.mainTessellator);
            }
        }

        public void resume() {
            if (this.paused) {
                this.paused = false;
                WrUpdateThread.this.mc.setTessellator(WrUpdateThread.this.threadTessellator);
                this.updateControl.resume();
            }
        }

        public void setUpdateControl(IWrUpdateControl updateControl) {
            this.updateControl = updateControl;
        }

        ThreadUpdateControl(WrUpdateThread.NamelessClass750854124 x1) {
            this();
        }
    }

    private class ThreadUpdateListener implements IWrUpdateListener {
        private final WrUpdateThread.ThreadUpdateControl tuc;

        private ThreadUpdateListener() {
            this.tuc = WrUpdateThread.this.new ThreadUpdateControl(null);
        }

        public void updating(IWrUpdateControl uc) {
            this.tuc.setUpdateControl(uc);
            WrUpdateThread.this.checkCanWork(this.tuc);
        }

        ThreadUpdateListener(WrUpdateThread.NamelessClass750854124 x1) {
            this();
        }
    }
}
