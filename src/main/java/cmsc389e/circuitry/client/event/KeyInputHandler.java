package cmsc389e.circuitry.client.event;

import cmsc389e.circuitry.client.ClientProxy;
import cmsc389e.circuitry.networking.CircuitryPacketHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

@EventBusSubscriber
public class KeyInputHandler {
    @SubscribeEvent
    public static void onKeyInputEvent(@SuppressWarnings("unused") KeyInputEvent event) {
	ClientProxy.KEYS.forEach((key, message) -> {
	    if (key.isPressed())
		CircuitryPacketHandler.INSTANCE.sendToServer(message);
	});
    }
}
