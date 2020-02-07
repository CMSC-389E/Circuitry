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

public class CircuitryWorldSavedData extends WorldSavedData {
    private static final String DATA_NAME = Circuitry.MODID + "_CircuitryData";

    public static CircuitryWorldSavedData get(World world) {
	MapStorage storage = world.getMapStorage();
	CircuitryWorldSavedData instance = (CircuitryWorldSavedData) storage
		.getOrLoadData(CircuitryWorldSavedData.class, DATA_NAME);
	if (instance == null) {
	    instance = new CircuitryWorldSavedData();
	    storage.setData(DATA_NAME, instance);
	}
	return instance;
    }

    private final Multimap<Block, BlockPos> poses;
    private final Map<BlockPos, String> tags;

    private CircuitryWorldSavedData() {
	this(DATA_NAME);
    }

    public CircuitryWorldSavedData(String name) {
	super(name);
	poses = HashMultimap.create();
	tags = new HashMap<>();
    }

    public Collection<BlockPos> get(Block block) {
	return poses.get(block);
    }

    public String get(BlockPos pos) {
	return tags.get(pos);
    }

    public void put(Block block, BlockPos pos, String tag) {
	poses.put(block, pos);
	put(pos, tag);
    }

    public void put(BlockPos pos, String tag) {
	tags.put(pos, tag);
	markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
	poses.clear();
	tags.clear();
	nbt.getKeySet().forEach(name -> {
	    Block block = Block.getBlockFromName(name);
	    NBTTagCompound compound = nbt.getCompoundTag(name);
	    compound.getKeySet().forEach(serialized -> {
		BlockPos pos = BlockPos.fromLong(Long.valueOf(serialized));
		poses.put(block, pos);
		tags.put(pos, compound.getString(serialized));
	    });
	});
    }

    public void remove(Block block, BlockPos pos) {
	poses.remove(block, pos);
	tags.remove(pos);
	markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
	poses.asMap().forEach((block, poses) -> {
	    NBTTagCompound value = new NBTTagCompound();
	    poses.forEach(pos -> value.setString(String.valueOf(pos.toLong()), tags.get(pos)));
	    compound.setTag(block.getRegistryName().toString(), value);
	});
	return compound;
    }
}
