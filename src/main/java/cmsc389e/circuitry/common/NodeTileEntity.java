package cmsc389e.circuitry.common;

import java.util.stream.Stream;

import cmsc389e.circuitry.Circuitry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;

public class NodeTileEntity extends TileEntity {
	public static Stream<NodeTileEntity> stream(World world) {
		TileEntityType<?> type = Circuitry.nodeTileEntity.get();
		return world.loadedTileEntityList.parallelStream().filter(entity -> entity.getType() == type)
				.map(entity -> (NodeTileEntity) entity);
	}

	public int index;

	public NodeTileEntity() {
		super(Circuitry.nodeTileEntity.get());
	}

	public String getTag() {
		if (Config.loaded) {
			String[] tags = getBlockState().getBlock() == Circuitry.inNodeBlock.get() ? Config.inTags : Config.outTags;
			return tags[index % tags.length];
		}
		return String.valueOf(index);

	}

	@Override
	public void read(CompoundNBT compound) {
		index = compound.getInt("index");
		super.read(compound);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("index", index);
		return super.write(compound);
	}
}
