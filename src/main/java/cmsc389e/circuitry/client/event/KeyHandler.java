package cmsc389e.circuitry.client.event;

import cmsc389e.circuitry.client.ClientProxy;
import cmsc389e.circuitry.common.network.CircuitryMessage.Key;
import cmsc389e.circuitry.common.network.CircuitryPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Handles key related {@link Event}s.
 */
@EventBusSubscriber(Side.CLIENT)
public class KeyHandler {
    /**
     * Fired whenever a {@link KeyBinding} is pressed or released. The
     * {@link ClientProxy#KEY_BINDINGS} are iterated through and tested using
     * {@link Key#test(EntityPlayer, boolean, boolean)}. If that returns true, the
     * {@link Key} is sent to {@link CircuitryPacketHandler} to be distributed to
     * the server.
     *
     * @param event the {@link KeyInputEvent}
     */
    @SubscribeEvent
    public static void onKeyInput(@SuppressWarnings("unused") KeyInputEvent event) {
	ClientProxy.KEY_BINDINGS.forEach((key, keyBinding) -> {
	    if (key.test(Minecraft.getMinecraft().player, keyBinding.isPressed(), keyBinding.isKeyDown()))
		CircuitryPacketHandler.sendMessage(key);
	});
    }
}
