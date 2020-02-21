package cmsc389e.circuitry.common.world;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import cmsc389e.circuitry.Circuitry;
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

    private final Map<BlockPos, Integer> tags;

    private CircuitryWorldSavedData() {
	this(DATA_NAME);
    }

    public CircuitryWorldSavedData(String name) {
	super(name);
	tags = new HashMap<>();
    }

    public void forEach(Consumer<? super BlockPos> action) {
	tags.keySet().forEach(action);
    }

    public Integer get(BlockPos pos) {
	return tags.get(pos);
    }

    public void put(BlockPos pos, int tag) {
	tags.put(pos, tag);
	markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
	try {
	    nbt.getKeySet().forEach(key -> put(BlockPos.fromLong(Long.valueOf(key)), nbt.getInteger(key)));
	    setDirty(false);
	} catch (Exception e) {
	    System.err.println("Corrupt or old data file found. Attempting to update!");
	    nbt.getKeySet().forEach(name -> {
		NBTTagCompound compound = nbt.getCompoundTag(name);
		compound.getKeySet()
			.forEach(key -> put(BlockPos.fromLong(Long.valueOf(key)), compound.getInteger(key)));
	    });
	    System.out.println("Success!");
	}
    }

    public void remove(BlockPos pos) {
	tags.remove(pos);
	markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
	tags.forEach((pos, tag) -> compound.setInteger(String.valueOf(pos.toLong()), tag));
	return compound;
    }
}
