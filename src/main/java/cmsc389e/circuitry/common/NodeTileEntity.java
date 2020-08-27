package cmsc389e.circuitry.common;

import java.util.function.Consumer;

import cmsc389e.circuitry.Circuitry;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class NodeTileEntity extends TileEntity {
    public static void forEach(MinecraftServer server, Consumer<NodeTileEntity> action) {
	TileEntityType<?> type = Circuitry.TYPE.get();
	server.getWorlds().forEach(world -> world.loadedTileEntityList.forEach(te -> {
	    if (te.getType() == type)
		action.accept((NodeTileEntity) te);
	}));
    }

    public int index;

    public NodeTileEntity() {
	super(Circuitry.TYPE.get());
    }

    public String getTag() {
	if (Config.loaded) {
	    String[] tags = getBlockState().getBlock() == Circuitry.IN_NODE_BLOCK.get() ? Config.inTags
		    : Config.outTags;
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