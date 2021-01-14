package cmsc389e.circuitry.common.network;

import cmsc389e.circuitry.common.NodeTileEntity;
import cmsc389e.circuitry.common.network.PacketHandler.Message;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class TagMessage extends Message {
	private final BlockPos pos;

	public TagMessage(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeBlockPos(pos);
	}

	@Override
	public void handle(Context context) {
		context.getSender().sendStatusMessage(
				new StringTextComponent(((NodeTileEntity) context.getSender().world.getTileEntity(pos)).getTag()),
				true);
	}
}
