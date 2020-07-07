package cmsc389e.circuitry.common;

import java.util.List;

import cmsc389e.circuitry.Circuitry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

public class NodeTileEntity extends TileEntity {
	private int tag;

	public NodeTileEntity() {
		super(Circuitry.NODE_TYPE.get());
	}

	public String getTag() {
		String prefix;
		List<String> tags;
		if (getBlockState().getBlock() == Circuitry.IN_NODE_BLOCK.get()) {
			prefix = "i";
			tags = Config.IN_TAGS.get();
		} else {
			prefix = "o";
			tags = Config.OUT_TAGS.get();
		}

		int size = tags.size();
		return tags.isEmpty() ? prefix + String.valueOf(tag) : tags.get((tag % size + size) % size);
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