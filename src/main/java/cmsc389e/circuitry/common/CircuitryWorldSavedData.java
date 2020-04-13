package cmsc389e.circuitry.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cmsc389e.circuitry.Circuitry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

/**
 * TODO
 */
public class CircuitryWorldSavedData extends WorldSavedData implements Iterable<BlockPos> {
    private static final String DATA_NAME = Circuitry.MODID + "_CircuitryData";

    /**
     * TODO
     *
     * @param world TODO
     * @return TODO
     */
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

    /**
     * TODO
     */
    private CircuitryWorldSavedData() {
	this(DATA_NAME);
    }

    /**
     * TODO
     *
     * @param name TODO
     */
    public CircuitryWorldSavedData(String name) {
	super(name);
	tags = new HashMap<>();
    }

    /**
     * TODO
     *
     * @param pos TODO
     * @return TODO
     */
    public Integer get(BlockPos pos) {
	return tags.get(pos);
    }

    /**
     * TODO
     */
    @Override
    public Iterator<BlockPos> iterator() {
	return tags.keySet().iterator();
    }

    /**
     * TODO
     *
     * @param pos TODO
     * @param tag TODO
     */
    public void put(BlockPos pos, int tag) {
	tags.put(pos, tag);
	markDirty();
    }

    /**
     * TODO
     */
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

    /**
     * TODO
     *
     * @param pos TODO
     */
    public void remove(BlockPos pos) {
	tags.remove(pos);
	markDirty();
    }

    /**
     * TODO
     */
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
	tags.forEach((pos, tag) -> compound.setInteger(String.valueOf(pos.toLong()), tag));
	return compound;
    }
}
