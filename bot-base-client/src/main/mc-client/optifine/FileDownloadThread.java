package optifine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;

@Getter
@RequiredArgsConstructor
public class FileDownloadThread extends Thread {
    private final String urlString;
    private final IFileDownloadListener listener;
    private final Minecraft mc;

    public void run() {
        try {
            byte[] e = HttpPipeline.get(this.urlString, mc.getProxy());
            this.listener.fileDownloadFinished(this.urlString, e, (Throwable) null);
        } catch (Exception var2) {
            this.listener.fileDownloadFinished(this.urlString, (byte[]) null, var2);
        }
    }
}
