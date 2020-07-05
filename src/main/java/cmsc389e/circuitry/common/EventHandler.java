package cmsc389e.circuitry.common;

import com.mojang.brigadier.CommandDispatcher;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.command.CircuitryCommand;
import cmsc389e.circuitry.common.command.LoadCommand;
import cmsc389e.circuitry.common.command.TestCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.DrawHighlightEvent.HighlightBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@EventBusSubscriber
public class EventHandler {
	@SubscribeEvent
	public static void onHighlightBlock(HighlightBlock event) {
		Minecraft minecraft = Minecraft.getInstance();
		TileEntity entity = minecraft.world.getTileEntity(event.getTarget().getPos());
		minecraft.ingameGUI.setOverlayMessage(entity != null && entity.getType() == Circuitry.NODE_TYPE.get()
				? String.valueOf(((NodeTileEntity) entity).getTag())
				: "", false);
	}

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
		dispatcher.register(LoadCommand.getCommand());
		dispatcher.register(TestCommand.getCommand());
	}
}
