package cmsc389e.circuitry;

import cmsc389e.circuitry.common.command.CommandAbort;
import cmsc389e.circuitry.common.command.CommandAccount;
import cmsc389e.circuitry.common.command.CommandLoad;
import cmsc389e.circuitry.common.command.CommandSetInput;
import cmsc389e.circuitry.common.command.CommandSubmit;
import cmsc389e.circuitry.common.command.CommandTest;
import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * Mod class for CMSC 389E Circuitry.
 */
@Mod(modid = Circuitry.MODID, version = "1.12.2-0.0.1.0-beta3", useMetadata = true)
public class Circuitry {
    public static final String MODID = "circuitry";

    @EventHandler
    public static void serverStart(FMLServerStartingEvent event) {
	ServerCommandManager commandManager = (ServerCommandManager) event.getServer().getCommandManager();
	commandManager.registerCommand(new CommandAbort());
	commandManager.registerCommand(new CommandAccount());
	commandManager.registerCommand(new CommandLoad());
	commandManager.registerCommand(new CommandSetInput());
	commandManager.registerCommand(new CommandSubmit());
	commandManager.registerCommand(new CommandTest());
    }
}