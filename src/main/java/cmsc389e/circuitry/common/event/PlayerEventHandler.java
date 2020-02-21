package cmsc389e.circuitry.common.event;

import cmsc389e.circuitry.common.block.BlockNode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber
public final class PlayerEventHandler {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
	if (event.side == Side.SERVER && event.phase == Phase.START) {
	    EntityPlayer player = event.player;
	    BlockPos pos = BlockNode.rayTraceEyes(player);
	    if (pos != null) {
		World world = player.getEntityWorld();
		player.sendStatusMessage(
			new TextComponentString(BlockNode.getTag(world, pos, world.getBlockState(pos))), true);
	    }
	}
    }
}