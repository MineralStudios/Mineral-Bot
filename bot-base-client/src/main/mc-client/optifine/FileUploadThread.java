package optifine;

import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;

@Getter
@RequiredArgsConstructor
public class FileUploadThread extends Thread {
    private final String urlString;
    private final Map<String, String> headers;
    private final byte[] content;
    private final IFileUploadListener listener;
    private final Minecraft mc;

    public void run() {
        try {
            HttpUtils.post(this.mc, this.urlString, this.headers, this.content);
            this.listener.fileUploadFinished(this.urlString, this.content, (Throwable) null);
        } catch (Exception var2) {
            this.listener.fileUploadFinished(this.urlString, this.content, var2);
        }
    }
}
