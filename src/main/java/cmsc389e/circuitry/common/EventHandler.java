package cmsc389e.circuitry.common;

import java.io.IOException;

import com.mojang.brigadier.CommandDispatcher;

import cmsc389e.circuitry.common.command.SetCommand;
import cmsc389e.circuitry.common.command.TestCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@EventBusSubscriber()
public class EventHandler {
    /**
     * Called while the server is starting. Registers all commands for this mod for
     * use in-game. TODO
     *
     * @param event the {@link FMLServerStartingEvent}
     */
    @SuppressWarnings("resource")
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

	MinecraftServer server = event.getServer();
	Tester.INSTANCE = new Tester(server);
	server.registerTickable(Tester.INSTANCE);

	GameRules rules = server.getGameRules();
	rules.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, server);
	rules.get(GameRules.DO_TILE_DROPS).set(false, server);
	rules.get(GameRules.DO_WEATHER_CYCLE).set(false, server);
	server.setDifficultyForAllWorlds(Difficulty.PEACEFUL, true);
	server.getWorlds().forEach(world -> world.setDayTime(6000));
    }
}
