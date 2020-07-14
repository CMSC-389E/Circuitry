package cmsc389e.circuitry.common;

import com.mojang.brigadier.CommandDispatcher;

import cmsc389e.circuitry.common.command.SetCommand;
import cmsc389e.circuitry.common.command.TestCommand;
import net.minecraft.command.CommandSource;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
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
		SetCommand.register(dispatcher);
		TestCommand.register(dispatcher);
	}

	@SubscribeEvent
	public static void onWorldTick(WorldTickEvent event) {
		if (event.phase == Phase.START) {
			Tester tester = Tester.INSTANCES.get(event.world);
			if (tester != null)
				tester.tick();
		}
	}
}
