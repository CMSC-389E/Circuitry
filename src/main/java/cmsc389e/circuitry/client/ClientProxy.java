package cmsc389e.circuitry.client;

import java.util.HashMap;
import java.util.Map;

import cmsc389e.circuitry.IProxy;
import cmsc389e.circuitry.networking.CommonKey;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;

public class ClientProxy implements IProxy {
    public static final Map<CommonKey, KeyBinding> KEY_BINDINGS = new HashMap<>();

    @Override
    public void init() {
	String name = Loader.instance().activeModContainer().getName();
	for (CommonKey key : CommonKey.values()) {
	    KeyBinding keyBinding = new KeyBinding(key.toString(), key.getKeyCode(), name);
	    KEY_BINDINGS.put(key, keyBinding);
	    ClientRegistry.registerKeyBinding(keyBinding);
	}
    }
}