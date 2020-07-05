package cmsc389e.circuitry.common;

import cmsc389e.circuitry.Circuitry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

public class NodeTileEntity extends TileEntity {
	private int tag;

	public NodeTileEntity() {
		super(Circuitry.NODE_TYPE.get());
	}

	public int getTag() {
		return tag;
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

	public void setTag(int tag) {
		this.tag = tag;
		markDirty();
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("tag", tag);
		return super.write(compound);
	}
}