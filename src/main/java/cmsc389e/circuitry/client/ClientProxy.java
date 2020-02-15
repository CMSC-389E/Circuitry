package cmsc389e.circuitry.client;

import org.lwjgl.input.Keyboard;

import cmsc389e.circuitry.IProxy;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public final class ClientProxy implements IProxy {
    @Override
    public void init() {
	String category = "CMSC 389E Circuitry";
	ClientRegistry.registerKeyBinding(new KeyBinding("Decrease Tags", Keyboard.KEY_LMENU, category));
	ClientRegistry.registerKeyBinding(new KeyBinding("Toggle In Node", Keyboard.KEY_R, category));
    }
}