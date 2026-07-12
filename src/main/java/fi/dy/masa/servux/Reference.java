package fi.dy.masa.servux;

import fi.dy.masa.servux.util.StringUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;

import java.nio.file.Path;

public class Reference
{
    public static final String MOD_ID = "servux";
    public static final String MOD_NAME = "Servux";
    public static final String MOD_VERSION = StringUtils.getModVersionString(MOD_ID);
    public static final String MC_VERSION = SharedConstants.getGameVersion().id();
    public static final String MOD_TYPE = "fabric";
    public static final String MOD_STRING = MOD_ID + "-" + MOD_TYPE + "-" + MC_VERSION + "-" + MOD_VERSION;
    public static final boolean DEV_DEBUG = false;
	public static final boolean ANSI_MODE = DEV_DEBUG;

    //compat mods
    public static final String LITEMATICA_MODID = "litematica";
    public static final String MINIHUD_MODID = "minihud";
    public static final String TWEAKEROO_MODID = "tweakeroo";

    public static final Path DEFAULT_RUN_DIR = FabricLoader.getInstance().getGameDir();
    public static final Path DEFAULT_CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
}
