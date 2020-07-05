package cmsc389e.circuitry.common;

import com.mojang.brigadier.CommandDispatcher;

import cmsc389e.circuitry.common.command.CircuitryCommand;
import cmsc389e.circuitry.common.command.test.TestCommand;
import net.minecraft.command.CommandSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@EventBusSubscriber
public class EventHandler {
	/**
	 * Called while the server is starting. Registers all commands for this mod for
	 * use in-game.
	 *
	 * @param event the {@link FMLServerStartingEvent}
	 */
	@SubscribeEvent
	public static void onServerStarting(FMLServerStartingEvent event) {
		CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
		dispatcher.register(CircuitryCommand.getCommand());
		dispatcher.register(TestCommand.getCommand());
	}
}
