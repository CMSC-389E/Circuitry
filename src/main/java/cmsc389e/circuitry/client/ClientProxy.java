package cmsc389e.circuitry.client;

import java.util.HashMap;
import java.util.Map;

import cmsc389e.circuitry.common.IProxy;
import cmsc389e.circuitry.common.network.CommonKey;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;

/**
 * Runs code specific to the physical client, but too generic to place into more
 * specialized classes. Also implements {@link IProxy} as the client-side proxy
 * instance.
 */
public class ClientProxy implements IProxy {
    public static final Map<CommonKey, KeyBinding> KEY_BINDINGS = new HashMap<>();

    /**
     * Initializes {@link KeyBinding}s for the mod as specified by the
     * {@link CommonKey} enum.
     */
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