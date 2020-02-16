package cmsc389e.circuitry.common.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public final class WorldHandler {
    public static final Map<World, Set<NextTickListEntry>> PENDING_TICKS = new HashMap<>();

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
	World world = event.getWorld();
	if (!world.isRemote) {
	    GameRules gameRules = world.getGameRules();
	    world.getWorldInfo().setDifficulty(EnumDifficulty.PEACEFUL);
	    world.getWorldInfo().setDifficultyLocked(true);
	    String string = String.valueOf(false);
	    gameRules.setOrCreateGameRule("doDaylightCycle", string);
	    gameRules.setOrCreateGameRule("doMobSpawning", string);
	    gameRules.setOrCreateGameRule("doTileDrops", string);
	    gameRules.setOrCreateGameRule("doWeatherCycle", string);

	    PENDING_TICKS.put(world, ObfuscationReflectionHelper.getPrivateValue(WorldServer.class, (WorldServer) world,
		    "field_73064_N")); // pendingTickListEntriesHashSet
	}
    }
}