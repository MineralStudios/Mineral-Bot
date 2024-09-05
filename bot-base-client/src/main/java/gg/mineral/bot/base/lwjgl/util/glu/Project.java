package gg.mineral.bot.base.lwjgl.util.glu;

import gg.mineral.bot.impl.config.BotGlobalConfig;

public class Project {

    public static void gluPerspective(float fovy,
            float aspect,
            float zNear,
            float zFar) {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.util.glu.GLU.gluPerspective(fovy, aspect, zNear, zFar);
    }

}
