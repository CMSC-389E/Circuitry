package cmsc389e.circuitry;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
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

    @Comment("A list of all possible names for input node blocks for the currently loaded test framework. Can be changed manually.")
    @Name("Input Names")
    public static String[] inputs = new String[0];
    @Comment("A list of all possible names for output node blocks for the currently loaded test framework. Can be changed manually.")
    @Name("Output Names")
    public static String[] outputs = new String[0];

    public static void sync() {
	ConfigManager.sync(Circuitry.MODID, Type.INSTANCE);
    }
}