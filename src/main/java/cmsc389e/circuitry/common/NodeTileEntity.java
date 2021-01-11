package cmsc389e.circuitry.common;

import java.util.stream.Stream;

import cmsc389e.circuitry.Circuitry;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;

public class NodeTileEntity extends TileEntity {
	public static Stream<NodeTileEntity> stream(World world) {
		TileEntityType<?> type = Circuitry.NODE.get();
		return world.loadedTileEntityList.parallelStream().filter(entity -> entity.getType() == type)
				.map(entity -> (NodeTileEntity) entity).sequential();
	}

	public int index;

	public NodeTileEntity() {
		super(Circuitry.NODE.get());
	}

	public NodeTileEntity(World world, Block block) {
		this();
		index = stream(world).filter(entity -> entity.getBlockState().getBlock() == block)
				.mapToInt(entity -> entity.index).sorted().reduce(-1, (left, right) -> left + 1 < right ? left : right)
				+ 1;
	}

	public String getTag() {
		if (Config.loaded) {
			String[] tags = getBlockState().getBlock() == Circuitry.IN_NODE.get() ? Config.inTags : Config.outTags;
			return tags[index % tags.length];
		}
		return String.valueOf(index);
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
		index = compound.getInt("index");
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("index", index);
		return super.write(compound);
	}
}