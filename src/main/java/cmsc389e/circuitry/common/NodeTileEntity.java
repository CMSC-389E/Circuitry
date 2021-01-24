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

public class NodeTileEntity extends TileEntity {
	public static void forEach(World world, Consumer<NodeTileEntity> consumer) {
		TileEntityType<?> type = Circuitry.tileEntity.get();
		world.loadedTileEntityList.forEach(entity -> {
			if (entity.getType() == type)
				consumer.accept((NodeTileEntity) entity);
		});
	}

	public static NodeTileEntity get(World world, BlockPos pos) {
		TileEntity entity = world.getTileEntity(pos);
		return entity != null && entity.getType() == Circuitry.tileEntity.get() ? (NodeTileEntity) entity : null;
	}

	public static void notifyBlockUpdates(World world) {
		forEach(world, NodeTileEntity::notifyBlockUpdate);
	}

	private int index;
	public String tag;

	public NodeTileEntity() {
		super(Circuitry.tileEntity.get());
	}

	public void changeIndex(int delta) {
		index += delta;
		markDirty();
		notifyBlockUpdate();
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		if (Config.loaded) {
			String[] nodeTags = ((NodeBlock) world.getBlockState(pos).getBlock()).getNodeTags();
			tag = nodeTags[(index % nodeTags.length + nodeTags.length) % nodeTags.length];
		} else
			tag = String.valueOf(index);

		CompoundNBT compoundIn = new CompoundNBT();
		compoundIn.putString("", tag);
		return new SUpdateTileEntityPacket(pos, -1, compoundIn);
	}

	public void notifyBlockUpdate() {
		BlockState state = getBlockState();
		world.notifyBlockUpdate(pos, state, state, BlockFlags.BLOCK_UPDATE);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		tag = pkt.getNbtCompound().getString("");
	}

	@Override
	public void read(CompoundNBT compound) {
		index = compound.getInt("");
		super.read(compound);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("", index);
		return super.write(compound);
	}
}
