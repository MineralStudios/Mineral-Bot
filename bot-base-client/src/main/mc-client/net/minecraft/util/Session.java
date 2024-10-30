package net.minecraft.util;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.Map;

public class Session implements gg.mineral.bot.api.instance.Session {
    @Getter
    private final String username, playerID, token;
    @Getter
    private final Session.Type sessionType;

    public Session(String username, String playerID, String token, String sessionType) {
        this.username = username;
        this.playerID = playerID;
        this.token = token;
        this.sessionType = Session.Type.fromString(sessionType);
    }

    public String getSessionID() {
        return "token:" + this.token + ":" + this.playerID;
    }

    public GameProfile getGameProfile() {
        try {
            val uuid = UUIDTypeAdapter.fromString(this.getPlayerID());
            return new GameProfile(uuid, this.getUsername());
        } catch (IllegalArgumentException var2) {
            return new GameProfile(null, this.getUsername());
        }
    }

    @RequiredArgsConstructor
    public static enum Type {
        LEGACY("legacy"), MOJANG("mojang");

        private static final Map<String, Session.Type> stringToType = new Object2ObjectOpenHashMap<>();
        private final String lowerCaseName;

        public static Session.Type fromString(String str) {
            return stringToType.get(str.toLowerCase());
        }

        static {
            val values = values();
            val length = values.length;

            for (int i = 0; i < length; ++i) {
                val type = values[i];
                stringToType.put(type.lowerCaseName, type);
            }
        }
    }

    @Override
    public String getAccessToken() {
        return this.token;
    }

    @Override
    public String getSessionId() {
        return this.getSessionID();
    }
}
