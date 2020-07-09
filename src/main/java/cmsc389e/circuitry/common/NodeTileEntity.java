package cmsc389e.circuitry.common;

import java.util.List;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.block.NodeBlock;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

public class NodeTileEntity extends TileEntity {
	private int tag;

	public NodeTileEntity() {
		super(Circuitry.nodeType);
	}

	public String getTag() {
		NodeBlock block = (NodeBlock) getBlockState().getBlock();
		List<String> tags = block.getNodeTags();
		int size = tags.size();
		return size == 0 ? block.getPrefix() + tag : tags.get((tag % size + size) % size);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return write(new CompoundNBT());
	}

	@Override
	public void handleUpdateTag(CompoundNBT tag) {
		read(tag);
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		tag = compound.getInt("tag");
	}

	public void setTag(boolean increment) {
		tag += increment ? 1 : -1;
		markDirty();
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("tag", tag);
		return super.write(compound);
	}
}