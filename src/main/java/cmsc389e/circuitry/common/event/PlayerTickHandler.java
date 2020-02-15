package cmsc389e.circuitry.common.event;

import cmsc389e.circuitry.common.block.BlockNode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber
public final class PlayerTickHandler {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
	if (event.side == Side.SERVER && event.phase == Phase.END) {
	    EntityPlayer player = event.player;
	    String tag = null;
	    RayTraceResult result = ForgeHooks.rayTraceEyes(player,
		    player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() + 1);
	    if (result != null)
		tag = BlockNode.getTag(player.getEntityWorld(), result.getBlockPos());
	    player.sendStatusMessage(new TextComponentString(tag == null ? "" : tag), true);
	}
    }
}