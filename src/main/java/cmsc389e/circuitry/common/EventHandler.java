package cmsc389e.circuitry.common;

import cmsc389e.circuitry.common.command.CircuitryCommand;
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
		event.getCommandDispatcher().register(CircuitryCommand.getCommand());
	}
}
