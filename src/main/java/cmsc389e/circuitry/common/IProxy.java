package cmsc389e.circuitry.common;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.client.ClientProxy;
import cmsc389e.circuitry.server.ServerProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * Proxy interface implemented by the client {@link ClientProxy} and the server
 * {@link ServerProxy}. Currently only contains the {@link #init()} method.
 */
public interface IProxy {
    /**
     * Runs any initialization code required by the current physical side. Called
     * from {@link Circuitry#init(FMLInitializationEvent)}.
     */
    void init();

    /**
     * TODO
     */
    void postInit();
}