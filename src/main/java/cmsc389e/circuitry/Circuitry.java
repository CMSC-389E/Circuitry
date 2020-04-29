package cmsc389e.circuitry;

import cmsc389e.circuitry.common.IProxy;
import cmsc389e.circuitry.common.command.CommandAbort;
import cmsc389e.circuitry.common.command.CommandLoad;
import cmsc389e.circuitry.common.command.CommandSet;
import cmsc389e.circuitry.common.command.CommandTest;
import cmsc389e.circuitry.common.network.CircuitryPacketHandler;
import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * Mod class for CMSC 389E Circuitry.
 */
@Mod(modid = Circuitry.MODID, version = "1.12.2-1.0.1.0", useMetadata = true, updateJSON = "https://raw.githubusercontent.com/JamieBrassel/CMSC-389E-Circuitry/master/update.json")
public class Circuitry {
    public static final String MODID = "circuitry";
    @SidedProxy(clientSide = "cmsc389e.circuitry.client.ClientProxy", serverSide = "cmsc389e.circuitry.server.ServerProxy")
    private static IProxy proxy;

    /**
     * Called while Minecraft Forge is initializing during mod startup.<br>
     * Currently, this method calls {@link CircuitryPacketHandler#init()} and
     * {@link IProxy#init(FMLInitializationEvent)}.
     *
     * @param event the {@link FMLInitializationEvent}
     */
    @EventHandler
    public static void init(FMLInitializationEvent event) {
	CircuitryPacketHandler.init();
	proxy.init(event);
    }

    /**
     * Called while Minecraft Forge is post-initializing during mod startup.<br>
     * Currently, this method just calls
     * {@link IProxy#postInit(FMLPostInitializationEvent)}.
     *
     * @param event the {@link FMLPostInitializationEvent event}
     */
    @EventHandler
    public static void postInit(FMLPostInitializationEvent event) {
	proxy.postInit(event);
    }

    /**
     * Called while the logical server is starting up, but before it is started.<br>
     * Currently, this method registers {@link CommandAbort /abort},
     * {@link CommandLoad /load}, {@link CommandSet /set}, and {@link CommandTest
     * /test}.
     *
     * @param event the {@link FMLServerStartingEvent}
     */
    @EventHandler
    public static void serverStart(FMLServerStartingEvent event) {
	ServerCommandManager commandManager = (ServerCommandManager) event.getServer().getCommandManager();
	commandManager.registerCommand(new CommandAbort());
	commandManager.registerCommand(new CommandLoad());
	commandManager.registerCommand(new CommandSet());
	commandManager.registerCommand(new CommandTest());
    }
}