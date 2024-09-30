package net.minecraft.util;

import org.apache.commons.lang3.Validate;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class ResourceLocation {
    private final String resourceDomain, resourcePath;

    public ResourceLocation(String domain, String path) {
        Validate.notNull(path, "Path must not be null");
        this.resourceDomain = domain != null && domain.length() != 0 ? domain : "minecraft";
        this.resourcePath = path;
    }

    public ResourceLocation(String resourceLocationString) {
        String resourceDomain = "minecraft";
        String resourcePath = resourceLocationString;
        int dividerIndex = resourceLocationString.indexOf(58);

        if (dividerIndex >= 0) {
            resourcePath = resourceLocationString.substring(dividerIndex + 1, resourceLocationString.length());

            if (dividerIndex > 1)
                resourceDomain = resourceLocationString.substring(0, dividerIndex);
        }

        this.resourceDomain = resourceDomain.toLowerCase();
        this.resourcePath = resourcePath;
    }

    public String toString() {
        return this.resourceDomain + ":" + this.resourcePath;
    }
}
