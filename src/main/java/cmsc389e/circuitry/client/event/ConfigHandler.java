package cmsc389e.circuitry.client.event;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.ConfigCircuitry;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Handler for config related {@link Event}s.
 */
@EventBusSubscriber(Side.CLIENT)
public class ConfigHandler {
    /**
     * Fired whenever a config file is changed through the in game GUI. Checks first
     * that the {@link Event}'s mod ID matches {@link Circuitry#MODID} before
     * calling {@link ConfigCircuitry#sync()}.
     *
     * @param event the {@link OnConfigChangedEvent}
     */
    @SubscribeEvent
    public static void onConfigChanged(OnConfigChangedEvent event) {
	if (event.getModID().equals(Circuitry.MODID))
	    ConfigCircuitry.sync();
    }
}