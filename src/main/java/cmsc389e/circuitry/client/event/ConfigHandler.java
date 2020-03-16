package cmsc389e.circuitry.client.event;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.ConfigCircuitry;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class ConfigHandler {
    @SubscribeEvent
    public static void onConfigChanged(OnConfigChangedEvent event) {
	if (event.getModID().equals(Circuitry.MODID))
	    ConfigCircuitry.sync();
    }
}