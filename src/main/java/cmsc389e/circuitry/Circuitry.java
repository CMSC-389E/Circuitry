package cmsc389e.circuitry;

import cmsc389e.circuitry.common.command.CommandAbort;
import cmsc389e.circuitry.common.command.CommandLoad;
import cmsc389e.circuitry.common.command.CommandTest;
import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * Mod class for CMSC 389E Circuitry.
 */
@Mod(modid = Circuitry.MODID, version = "1.12.2-0.0.1.0-beta4", useMetadata = true)
public final class Circuitry {
    public static final String MODID = "circuitry";
    @SidedProxy(clientSide = "cmsc389e.circuitry.client.ClientProxy", serverSide = "cmsc389e.circuitry.server.ServerProxy")
    private static IProxy proxy;

    @EventHandler
    public static void init(@SuppressWarnings("unused") FMLInitializationEvent event) {
	proxy.init();
    }

    @EventHandler
    public static void serverStart(FMLServerStartingEvent event) {
	ServerCommandManager commandManager = (ServerCommandManager) event.getServer().getCommandManager();
	commandManager.registerCommand(new CommandAbort());
	commandManager.registerCommand(new CommandLoad());
	commandManager.registerCommand(new CommandTest());
    }
}