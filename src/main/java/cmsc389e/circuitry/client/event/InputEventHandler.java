package cmsc389e.circuitry.client.event;

import cmsc389e.circuitry.client.ClientProxy;
import cmsc389e.circuitry.networking.CircuitryPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

@EventBusSubscriber
public class InputEventHandler {
    @SubscribeEvent
    public static void onKeyInputEvent(@SuppressWarnings("unused") KeyInputEvent event) {
	ClientProxy.KEY_BINDINGS.forEach((key, keyBinding) -> {
	    if (key.test(Minecraft.getMinecraft().player, keyBinding.isPressed(), keyBinding.isKeyDown()))
		CircuitryPacketHandler.sendMessage(key);
	});
    }
}
