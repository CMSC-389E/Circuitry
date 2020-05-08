package cmsc389e.circuitry.common;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.client.ClientProxy;
import cmsc389e.circuitry.server.ServerProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

/**
 * Proxy interface implemented by the client {@link ClientProxy} and the server
 * {@link ServerProxy}.
 */
public interface IProxy {
    /**
     * Runs any initialization code required by the current physical side. Called
     * from {@link Circuitry#init(FMLInitializationEvent)}.
     *
     * @param event the {@link FMLInitializationEvent}
     */
    void init(FMLInitializationEvent event);

    /**
     * Runs any post-initialization code required by the current physical side.
     * Called from {@link Circuitry#postInit(FMLPostInitializationEvent)}.
     *
     * @param event the {@link FMLPostInitializationEvent event}
     */
    void postInit(FMLPostInitializationEvent event);
}