package cmsc389e.circuitry.common.network;

import cmsc389e.circuitry.common.NodeTileEntity;
import cmsc389e.circuitry.common.network.PacketHandler.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class TagUpdatedMessage extends Message {
	private final BlockPos pos;
	private final String tag;

	public TagUpdatedMessage(BlockPos pos, String tag) {
		this.pos = pos;
		this.tag = tag;
	}

	public TagUpdatedMessage(PacketBuffer buffer) {
		this(buffer.readBlockPos(), buffer.readString());
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeBlockPos(pos).writeString(tag);
	}

	@Override
	@SuppressWarnings("resource")
	public void handle(Context context) {
		NodeTileEntity entity = NodeTileEntity.get(Minecraft.getInstance().world, pos);
		if (entity != null)
			entity.tag = tag;
	}
}
