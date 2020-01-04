package cmsc389e.circuitry.common.world;

import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class WorldEventHandler {
    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
	World world = event.getWorld();
	if (!world.isRemote) {
	    GameRules gameRules = world.getGameRules();
	    world.getWorldInfo().setDifficulty(EnumDifficulty.PEACEFUL);
	    world.getWorldInfo().setDifficultyLocked(true);
	    gameRules.setOrCreateGameRule("doDaylightCycle", String.valueOf(false));
	    gameRules.setOrCreateGameRule("doMobSpawning", String.valueOf(false));
	    gameRules.setOrCreateGameRule("doTileDrops", String.valueOf(false));
	    gameRules.setOrCreateGameRule("doWeatherCycle", String.valueOf(false));
	}
    }
}
