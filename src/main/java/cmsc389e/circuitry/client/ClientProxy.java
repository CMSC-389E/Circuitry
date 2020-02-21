package cmsc389e.circuitry.client;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import cmsc389e.circuitry.IProxy;
import cmsc389e.circuitry.networking.CircuitryMessage;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ClientProxy implements IProxy {
    public static final Map<KeyBinding, IMessage> KEYS = new HashMap<>();

    private static void addKey(String description, int keyCode, IMessage message) {
	KeyBinding key = new KeyBinding(description, keyCode, "CMSC 389E Circuitry");
	KEYS.put(key, message);
	ClientRegistry.registerKeyBinding(key);
    }

    @Override
    public void init() {
	addKey("Decrease Tag", Keyboard.KEY_LMENU, new CircuitryMessage(CircuitryMessage.Key.DECREASE_TAG));
	addKey("Toggle Input", Keyboard.KEY_R, new CircuitryMessage(CircuitryMessage.Key.TOGGLE_INPUT));
    }
}