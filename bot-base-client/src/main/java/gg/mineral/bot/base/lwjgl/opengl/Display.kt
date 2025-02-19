package gg.mineral.bot.base.lwjgl.opengl

import gg.mineral.bot.impl.config.BotGlobalConfig
import org.lwjgl.LWJGLException
import org.lwjgl.PointerBuffer
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.DisplayMode
import org.lwjgl.opengl.Drawable
import org.lwjgl.opengl.PixelFormat
import java.nio.ByteBuffer

object Display {
    private val DEFAULT_DISPLAY_MODE = DisplayMode(1280, 720)

    @JvmStatic
    val isActive: Boolean
        get() {
            if (BotGlobalConfig.headless) return true

            return Display.isActive()
        }

    @JvmStatic
    @Throws(LWJGLException::class)
    fun setFullscreen(fullscreen: Boolean) {
        if (BotGlobalConfig.headless) return

        Display.setFullscreen(fullscreen)
    }

    @JvmStatic
    @set:Throws(LWJGLException::class)
    var displayMode: DisplayMode
        get() {
            if (BotGlobalConfig.headless) return DEFAULT_DISPLAY_MODE

            return Display.getDisplayMode()
        }
        set(displayMode) {
            if (BotGlobalConfig.headless) return

            Display.setDisplayMode(displayMode)
        }

    @JvmStatic
    fun setResizable(resizable: Boolean) {
        if (BotGlobalConfig.headless) return

        Display.setResizable(resizable)
    }

    @JvmStatic
    fun setTitle(title: String?) {
        if (BotGlobalConfig.headless) return

        Display.setTitle(title)
    }

    @JvmStatic
    fun setIcon(byteBuffers: Array<ByteBuffer?>?) {
        if (BotGlobalConfig.headless) return

        Display.setIcon(byteBuffers)
    }

    @JvmStatic
    @Throws(LWJGLException::class)
    fun create(withDepthBits: PixelFormat) {
        if (BotGlobalConfig.headless) return

        Display.create(withDepthBits)
    }

    @JvmStatic
    @Throws(LWJGLException::class)
    fun create() {
        if (BotGlobalConfig.headless) return

        Display.create()
    }

    @JvmStatic
    fun setVSyncEnabled(enableVsync: Boolean) {
        if (BotGlobalConfig.headless) return

        Display.setVSyncEnabled(enableVsync)
    }

    @JvmStatic
    @get:Throws(LWJGLException::class)
    val availableDisplayModes: Array<DisplayMode>
        get() {
            if (BotGlobalConfig.headless) return arrayOf(
                DEFAULT_DISPLAY_MODE
            )

            return Display.getAvailableDisplayModes()
        }

    @JvmStatic
    val desktopDisplayMode: DisplayMode
        get() {
            if (BotGlobalConfig.headless) return DEFAULT_DISPLAY_MODE

            return Display.getDesktopDisplayMode()
        }

    @JvmStatic
    fun destroy() {
        if (BotGlobalConfig.headless) return

        Display.destroy()
    }

    @JvmStatic
    val isCreated: Boolean
        get() {
            if (BotGlobalConfig.headless) return true

            return Display.isCreated()
        }

    @JvmStatic
    val isCloseRequested: Boolean
        get() {
            if (BotGlobalConfig.headless) return false

            return Display.isCloseRequested()
        }

    @JvmStatic
    fun sync(limitFramerate: Int) {
        if (BotGlobalConfig.headless) return

        Display.sync(limitFramerate)
    }

    @JvmStatic
    fun update() {
        if (BotGlobalConfig.headless) return

        Display.update()
    }

    @JvmStatic
    fun wasResized(): Boolean {
        if (BotGlobalConfig.headless) return false

        return Display.wasResized()
    }

    @JvmStatic
    val width: Int
        get() {
            if (BotGlobalConfig.headless) return DEFAULT_DISPLAY_MODE.width

            return Display.getWidth()
        }

    @JvmStatic
    val height: Int
        get() {
            if (BotGlobalConfig.headless) return DEFAULT_DISPLAY_MODE.height

            return Display.getHeight()
        }

    @JvmStatic
    val drawable: Drawable
        get() {
            if (BotGlobalConfig.headless) return object : Drawable {
                @Throws(LWJGLException::class)
                override fun isCurrent(): Boolean {
                    return false
                }

                @Throws(LWJGLException::class)
                override fun makeCurrent() {
                }

                @Throws(LWJGLException::class)
                override fun releaseContext() {
                }

                override fun destroy() {
                }

                @Throws(LWJGLException::class)
                override fun setCLSharingProperties(properties: PointerBuffer) {
                }
            }

            return Display.getDrawable()
        }
}
