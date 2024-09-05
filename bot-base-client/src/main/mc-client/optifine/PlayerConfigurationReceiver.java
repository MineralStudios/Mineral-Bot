package optifine;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;

public class PlayerConfigurationReceiver implements IFileDownloadListener {
    private String player = null;
    private final Minecraft mc;

    public PlayerConfigurationReceiver(Minecraft mc, String player) {
        this.player = player;
        this.mc = mc;
    }

    public void fileDownloadFinished(String url, byte[] bytes, Throwable exception) {
        if (bytes != null) {
            try {
                String e = new String(bytes, "ASCII");
                JsonParser jp = new JsonParser();
                JsonElement je = jp.parse(e);
                PlayerConfigurationParser pcp = new PlayerConfigurationParser(this.mc, this.player);
                PlayerConfiguration pc = pcp.parsePlayerConfiguration(je);

                if (pc != null) {
                    pc.setInitialized(true);
                    PlayerConfigurations.setPlayerConfiguration(this.player, pc);
                }
            } catch (Exception var9) {
                Config.dbg("Error parsing configuration: " + url + ", " + var9.getClass().getName() + ": "
                        + var9.getMessage());
            }
        }
    }
}
