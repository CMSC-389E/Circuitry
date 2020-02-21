package cmsc389e.circuitry;

import java.io.File;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Configuration file for the mod.
 */
@Config(modid = Circuitry.MODID)
public class ConfigCircuitry {
    /**
     * Called when config values are changed.
     */
    @Mod.EventBusSubscriber
    public static class EventHandler {
	@SubscribeEvent
	public static void onConfigChangedEvent(OnConfigChangedEvent event) {
	    if (event.getModID().equals(Circuitry.MODID))
		sync();
	}
    }

    public static String[] inTags = new String[0];
    public static String[] outTags = new String[0];
    public static String server = "https://cs.umd.edu/~abrassel/";
    public static String submit = "submit" + File.separatorChar + "submit.jar";
    public static String testLogs = "test_logs.txt";
    public static String tests = "tests.txt";

    public static void sync() {
	ConfigManager.sync(Circuitry.MODID, Type.INSTANCE);
    }
}