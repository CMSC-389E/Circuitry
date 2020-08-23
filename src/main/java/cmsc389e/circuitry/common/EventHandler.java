package cmsc389e.circuitry.common;

import java.io.IOException;

import com.mojang.brigadier.CommandDispatcher;

import cmsc389e.circuitry.common.command.SetCommand;
import cmsc389e.circuitry.common.command.TestCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
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
     * @throws IOException
     */
    @SuppressWarnings("resource")
    @SubscribeEvent
    public static void onServerStarting(FMLServerStartingEvent event) throws IOException {
	CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
	SetCommand.register(dispatcher);
	TestCommand.register(dispatcher);

	Config.load();

	MinecraftServer server = event.getServer();
	Tester.INSTANCE = new Tester(server);
	server.registerTickable(Tester.INSTANCE);
    }
}
