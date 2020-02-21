package cmsc389e.circuitry.common.event;

import cmsc389e.circuitry.common.command.CommandTest;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

@EventBusSubscriber
public final class WorldHandler {
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
	}
    }

    @SubscribeEvent
    public static void onWorldTick(WorldTickEvent event) {
	if (event.phase == Phase.START)
	    if (CommandTest.isRunning(event.world))
		CommandTest.tick(event.world);
    }
}