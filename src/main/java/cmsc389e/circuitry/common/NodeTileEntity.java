package cmsc389e.circuitry.common;

import java.util.stream.Stream;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.block.NodeBlock;
import cmsc389e.circuitry.common.network.PacketHandler;
import cmsc389e.circuitry.common.network.TagUpdatedMessage;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.PacketTarget;

public class NodeTileEntity extends TileEntity {
	public static NodeTileEntity get(World world, BlockPos pos) {
		TileEntity entity = world.getTileEntity(pos);
		return entity != null && entity.getType() == Circuitry.tileEntity.get() ? (NodeTileEntity) entity : null;
	}

	public static Stream<NodeTileEntity> stream(World world) {
		TileEntityType<?> type = Circuitry.tileEntity.get();
		return world.loadedTileEntityList.stream().filter(entity -> entity.getType() == type)
				.map(entity -> (NodeTileEntity) entity);
	}

	private int index;
	public String tag;

	public NodeTileEntity() {
		super(Circuitry.tileEntity.get());
	}

	public void changeIndex(int delta) {
		index += delta;
		markDirty();
		updateTag();
	}

	@Override
	public void read(CompoundNBT compound) {
		index = compound.getInt("");
		super.read(compound);
	}

	@Override
	public void setWorldAndPos(World world, BlockPos pos) {
		super.setWorldAndPos(world, pos);
		if (!world.isRemote)
			updateTag();
	}

	public void sync(PacketTarget target) {
		PacketHandler.CHANNEL.send(target, new TagUpdatedMessage(pos, tag));
	}

	public void updateTag() {
		if (Config.loaded) {
			String[] nodeTags = ((NodeBlock) world.getBlockState(pos).getBlock()).getNodeTags();
			tag = nodeTags[(index % nodeTags.length + nodeTags.length) % nodeTags.length];
		} else
			tag = String.valueOf(index);
		sync(PacketDistributor.ALL.noArg());
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("", index);
		return super.write(compound);
	}
}
