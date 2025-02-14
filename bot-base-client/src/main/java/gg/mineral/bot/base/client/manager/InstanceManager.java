package gg.mineral.bot.base.client.manager;

import gg.mineral.bot.base.client.instance.ClientInstance;
import lombok.Getter;
import org.eclipse.jdt.annotation.NonNull;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InstanceManager {
    @Getter
    private static final ConcurrentHashMap<UUID, ClientInstance> instances = new ConcurrentHashMap<>() {

        @Override
        public ClientInstance put(@NonNull UUID key, @NonNull ClientInstance value) {
            if (containsKey(key))
                throw new IllegalArgumentException("Instance with UUID " + key + " already exists");
            return super.put(key, value);
        }
    };

    @Getter
    private static final ConcurrentHashMap<UUID, ClientInstance> pendingInstances = new ConcurrentHashMap<>() {

        @Override
        public ClientInstance put(@NonNull UUID key, @NonNull ClientInstance value) {
            if (containsKey(key))
                throw new IllegalArgumentException("Instance with UUID " + key + " already exists");
            return super.put(key, value);
        }
    };


    public static boolean isRunning() {
        return !instances.isEmpty();
    }
}
