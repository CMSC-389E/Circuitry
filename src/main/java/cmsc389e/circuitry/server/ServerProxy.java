package cmsc389e.circuitry.server;

import cmsc389e.circuitry.common.IProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

/**
 * Runs code specific to the physical server, but too generic to place into more
 * specialized classes. Also implements {@link IProxy} as the server-side proxy
 * instance.<br>
 * <br>
 * <b> Currently unused.</b>
 */
public class ServerProxy implements IProxy {
    /**
     * Called during Forge initialization, but is currently unused.
     */
    @Override
    public void init(FMLInitializationEvent event) {
	// Nothing needs to be done here.
    }

    /**
     * Called during Forge post-initialization, but is currently unused.
     */
    @Override
    public void postInit(FMLPostInitializationEvent event) {
	// Nothing needs to be done here.
    }
}