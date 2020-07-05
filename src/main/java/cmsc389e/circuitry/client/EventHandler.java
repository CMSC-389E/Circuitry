package cmsc389e.circuitry.client;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.NodeTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent.HighlightBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(Dist.CLIENT)
public class EventHandler {
	@SubscribeEvent
	public static void onHighlightBlock(HighlightBlock event) {
		Minecraft minecraft = Minecraft.getInstance();
		TileEntity entity = minecraft.world.getTileEntity(event.getTarget().getPos());
		if (entity != null && entity.getType() == Circuitry.NODE_TYPE.get())
			minecraft.ingameGUI.setOverlayMessage(((NodeTileEntity) entity).getTag(), false);
	}
}
