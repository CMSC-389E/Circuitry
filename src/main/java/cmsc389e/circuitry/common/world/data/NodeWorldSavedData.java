package cmsc389e.circuitry.common.world.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cmsc389e.circuitry.Circuitry;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class NodeWorldSavedData extends WorldSavedData {
    private static final String DATA_NAME = Circuitry.MODID + "_NodeData";

    public static NodeWorldSavedData get(World world) {
	MapStorage storage = world.getMapStorage();
	NodeWorldSavedData instance = (NodeWorldSavedData) storage.getOrLoadData(NodeWorldSavedData.class, DATA_NAME);
	if (instance == null) {
	    instance = new NodeWorldSavedData();
	    storage.setData(DATA_NAME, instance);
	}
	return instance;
    }

    private final Multimap<Block, BlockPos> poses;
    private final Map<BlockPos, String> tags;

    public NodeWorldSavedData() {
	super(DATA_NAME);
	poses = HashMultimap.create();
	tags = new HashMap<>();
    }

    public void breakNode(Block block, BlockPos pos) {
	remove(block, pos);
    }

    public Collection<BlockPos> getPos(Block block) {
	return poses.get(block);
    }

    public String getTag(BlockPos pos) {
	return tags.get(pos);
    }

    public void onNodePlaced(Block block, BlockPos pos, String tag) {
	put(block, pos, tag);
    }

    private void put(Block block, BlockPos pos, String tag) {
	poses.put(block, pos);
	setTag(pos, tag);
	markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
	poses.clear();
	tags.clear();
	nbt.getKeySet().forEach(name -> {
	    Block block = Block.getBlockFromName(name);
	    NBTTagCompound compound = nbt.getCompoundTag(name);
	    compound.getKeySet().forEach(key -> {
		BlockPos pos = BlockPos.fromLong(Long.valueOf(key));
		poses.put(block, pos);
		tags.put(pos, compound.getString(key));
	    });
	});
    }

    private void remove(Block block, BlockPos pos) {
	poses.remove(block, pos);
	tags.remove(pos);
	markDirty();
    }

    public void setTag(BlockPos pos, String tag) {
	tags.put(pos, tag);
	markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
	poses.asMap().forEach((block, poses) -> {
	    NBTTagCompound nbt = new NBTTagCompound();
	    poses.forEach(pos -> nbt.setString(String.valueOf(pos.toLong()), tags.get(pos)));
	    compound.setTag(block.getTranslationKey(), nbt);
	});
	return compound;
    }
}
