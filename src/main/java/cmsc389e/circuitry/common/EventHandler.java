package cmsc389e.circuitry.common;

import java.io.IOException;

import com.mojang.brigadier.CommandDispatcher;

import cmsc389e.circuitry.common.command.SetCommand;
import cmsc389e.circuitry.common.command.TestCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.world.WorldEvent.CreateSpawnPosition;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@EventBusSubscriber
public class EventHandler {
	@SubscribeEvent
	public static void onCreateSpawnPosition(CreateSpawnPosition event) {
		WorldInfo info = event.getWorld().getWorldInfo();
		info.setDayTime(6000);
		info.setDifficulty(Difficulty.PEACEFUL);

		GameRules rules = info.getGameRulesInstance();
		rules.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
		rules.get(GameRules.DO_MOB_SPAWNING).set(false, null);
		rules.get(GameRules.DO_TILE_DROPS).set(false, null);
		rules.get(GameRules.DO_WEATHER_CYCLE).set(false, null);
	}

	/**
	 * Called while the server is starting. Registers all commands for this mod for
	 * use in-game. TODO
	 *
	 * @param event the {@link FMLServerStartingEvent}
	 */
	@SubscribeEvent
	public static void onServerStarting(FMLServerStartingEvent event) {
		CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
		SetCommand.register(dispatcher);
		TestCommand.register(dispatcher);

		try {
			Config.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		@SuppressWarnings("resource")
		MinecraftServer server = event.getServer();
		server.registerTickable(Tester.INSTANCE = new Tester(server));
	}
}
