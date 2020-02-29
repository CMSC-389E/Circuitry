package cmsc389e.circuitry;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import cmsc389e.circuitry.common.command.CommandAbort;
import cmsc389e.circuitry.common.command.CommandLoad;
import cmsc389e.circuitry.common.command.CommandSet;
import cmsc389e.circuitry.common.command.CommandTest;
import cmsc389e.circuitry.networking.CircuitryPacketHandler;
import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.ForgeVersion.CheckResult;
import net.minecraftforge.common.ForgeVersion.Status;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * Mod class for CMSC 389E Circuitry.
 */
@Mod(modid = Circuitry.MODID, version = "1.12.2-0.0.1.0-beta8", useMetadata = true, updateJSON = "https://raw.githubusercontent.com/JamieBrassel/CMSC-389E-Circuitry/master/update.json")
public class Circuitry {
    public static final ModContainer CONTAINER = Loader.instance().activeModContainer();
    public static final String MODID = "circuitry";
    @SidedProxy(clientSide = "cmsc389e.circuitry.client.ClientProxy", serverSide = "cmsc389e.circuitry.server.ServerProxy")
    private static IProxy proxy;

    @EventHandler
    public static void init(@SuppressWarnings("unused") FMLInitializationEvent event) {
	CircuitryPacketHandler.init();
	proxy.init();

	CheckResult result = ForgeVersion.getResult(CONTAINER);
	if (result.status == Status.OUTDATED)
	    Runtime.getRuntime().addShutdownHook(new Thread() {
		@Override
		public void run() {
		    File source = CONTAINER.getSource();
		    if (source.isDirectory())
			System.err.println("Cannot update " + CONTAINER.getName()
				+ " because this is a deobfuscated environment!");
		    else
			try {
			    FileUtils.copyURLToFile(new URL(
				    result.url + "/download/" + result.target + "/circuitry-" + result.target + ".jar"),
				    source);
			} catch (IOException e) {
			    e.printStackTrace();
			}
		}
	    });
    }

    @EventHandler
    public static void serverStart(FMLServerStartingEvent event) {
	ServerCommandManager commandManager = (ServerCommandManager) event.getServer().getCommandManager();
	commandManager.registerCommand(new CommandAbort());
	commandManager.registerCommand(new CommandLoad());
	commandManager.registerCommand(new CommandSet());
	commandManager.registerCommand(new CommandTest());
    }
}