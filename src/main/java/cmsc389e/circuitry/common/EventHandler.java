package cmsc389e.circuitry.common;

import java.io.IOException;

import com.mojang.brigadier.CommandDispatcher;

import cmsc389e.circuitry.common.command.SetCommand;
import cmsc389e.circuitry.common.command.TestCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.PacketTarget;

@EventBusSubscriber
public class EventHandler {
	@SubscribeEvent
	public static void onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
		event.setCanceled(true);
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
		PacketTarget target = PacketDistributor.PLAYER.with(() -> player);
		NodeTileEntity.stream(player.world).forEach(entity -> entity.sync(target));
	}

	@SubscribeEvent
	@SuppressWarnings("resource")
	public static void onServerStarting(FMLServerStartingEvent event) {
		try {
			CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
			SetCommand.register(dispatcher);
			TestCommand.register(dispatcher);

			MinecraftServer server = event.getServer();
			ServerWorld world = server.getWorld(DimensionType.OVERWORLD);
			server.registerTickable(new Tester(world));

			Config.load(world);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	@SuppressWarnings("resource")
	public static void onWorldCreateSpawnPosition(WorldEvent.CreateSpawnPosition event) {
		IWorld world = event.getWorld();

		WorldInfo info = world.getWorldInfo();
		info.setDayTime(6000);

		GameRules rules = info.getGameRulesInstance();
		rules.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
		rules.get(GameRules.DO_WEATHER_CYCLE).set(false, null);

		if (world.getWorld().getServer().isSinglePlayer())
			rules.get(GameRules.DO_MOB_SPAWNING).set(false, null);
	}
}
