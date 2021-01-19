package cmsc389e.circuitry.common;

import java.io.IOException;

import com.mojang.brigadier.CommandDispatcher;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.command.SetCommand;
import cmsc389e.circuitry.common.command.TestCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@EventBusSubscriber
public class EventHandler {
	@SubscribeEvent
	public static void onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
		event.setCanceled(true);
	}

	@SubscribeEvent
	@SuppressWarnings("resource")
	public static void onServerStarting(FMLServerStartingEvent event) {
		try {
			CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
			SetCommand.register(dispatcher);
			TestCommand.register(dispatcher);

			MinecraftServer server = event.getServer();
			server.registerTickable(new Tester(server.getWorld(DimensionType.OVERWORLD)));

			Config.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public static void onTickPlayer(TickEvent.PlayerTickEvent event) {
		if (event.phase == Phase.END && event.side.isServer()) {
			TileEntity entity = event.player.world.getTileEntity(((BlockRayTraceResult) event.player
					.pick(event.player.getAttribute(PlayerEntity.REACH_DISTANCE).getValue(), 1, false)).getPos());
			event.player.sendStatusMessage(
					new StringTextComponent(entity != null && entity.getType() == Circuitry.nodeTileEntity.get()
							? ((NodeTileEntity) entity).getTag()
							: ""),
					true);
		}
	}
}
