package cmsc389e.circuitry.common;

import java.util.function.Consumer;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.block.NodeBlock;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.BlockFlags;

public final class NodeTileEntity extends TileEntity {
	public static void forEach(final World world, final Consumer<NodeTileEntity> consumer) {
		final TileEntityType<?> type = Circuitry.tileEntity.get();
		world.loadedTileEntityList.forEach(entity -> {
			if (entity.getType() == type)
				consumer.accept((NodeTileEntity) entity);
		});
	}

	public static NodeTileEntity get(final World world, final BlockPos pos) {
		final TileEntity entity = world.getTileEntity(pos);
		return entity != null && entity.getType() == Circuitry.tileEntity.get() ? (NodeTileEntity) entity : null;
	}

	public static void notifyBlockUpdates(final World world) {
		forEach(world, NodeTileEntity::notifyBlockUpdate);
	}

	private int index;
	public String tag;

	public NodeTileEntity() {
		super(Circuitry.tileEntity.get());
	}

	public void changeIndex(final int delta) {
		index += delta;
		markDirty();
		notifyBlockUpdate();
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(pos, -1, getUpdateTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		if (tag == null)
			if (Config.loaded) {
				final String[] nodeTags = ((NodeBlock) world.getBlockState(pos).getBlock()).getNodeTags();
				tag = nodeTags[(index % nodeTags.length + nodeTags.length) % nodeTags.length];
			} else
				tag = String.valueOf(index);

		final CompoundNBT nbt = write(new CompoundNBT());
		nbt.putString("", tag);
		return nbt;
	}

	@Override
	public void handleUpdateTag(final CompoundNBT nbt) {
		tag = nbt.getString("");
	}

	public void notifyBlockUpdate() {
		tag = null;
		final BlockState state = getBlockState();
		world.notifyBlockUpdate(pos, state, state, BlockFlags.BLOCK_UPDATE);
	}

	@Override
	public void onDataPacket(final NetworkManager net, final SUpdateTileEntityPacket pkt) {
		handleUpdateTag(pkt.getNbtCompound());
	}

	@Override
	public void read(final CompoundNBT compound) {
		index = compound.getInt("");
		super.read(compound);
	}

	@Override
	public CompoundNBT write(final CompoundNBT compound) {
		compound.putInt("", index);
		return super.write(compound);
	}
}
