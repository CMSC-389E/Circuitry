package cmsc389e.circuitry.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.block.BlockInNode;
import cmsc389e.circuitry.common.block.BlockNode;
import cmsc389e.circuitry.common.block.BlockOutNode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

/**
 * A {@link WorldSavedData} object that stores the {@link BlockPos} of every
 * {@link BlockInNode} and {@link BlockOutNode} as well as an {@link Integer}
 * for each one. The {@link Integer} is later converted to a {@link String} tag
 * within {@link BlockNode#getTag(World, BlockPos, IBlockState)}.
 */
public class CircuitryWorldSavedData extends WorldSavedData implements Iterable<BlockPos> {
    private static final String DATA_NAME = Circuitry.MODID + "_CircuitryData";

    /**
     * Gets the {@link CircuitryWorldSavedData} associated with the given
     * {@link World}, or creates a new instance if none exists.
     *
     * @param world the {@link World} object to retrieve the associated
     *              {@link CircuitryWorldSavedData} for
     * @return a {@link CircuitryWorldSavedData} associated with the given
     *         {@link World}
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
     * Creates an instance of {@link CircuitryWorldSavedData} using the default
     * name. This is a required constructor.
     */
    private CircuitryWorldSavedData() {
	this(DATA_NAME);
    }

    /**
     * Creates an instance of {@link CircuitryWorldSavedData} using the given
     * {@link String} as the name and initializes the tags. This is a required
     * constructor.
     *
     * @param name the name of this {@link CircuitryWorldSavedData}
     */
    public CircuitryWorldSavedData(String name) {
	super(name);
	tags = new HashMap<>();
    }

    /**
     * Gets the {@link Integer} tag associated with the given {@link BlockPos} in
     * the tags.
     *
     * @param pos the {@link BlockPos} to retrieve the {@link Integer} tag for
     * @return the {@link Integer} tag associated with the given {@link BlockPos}
     */
    public Integer get(BlockPos pos) {
	return tags.get(pos);
    }

    /**
     * Returns an {@link Iterator} that contains every {@link BlockPos} in the tags.
     */
    @Override
    public Iterator<BlockPos> iterator() {
	return tags.keySet().iterator();
    }

    /**
     * Puts the given {@link BlockPos} and {@link Integer} into the tags and calls
     * {@link #markDirty()}.
     *
     * @param pos the {@link BlockPos} to put as the key with the given
     *            {@link Integer}
     * @param tag the {@link Integer} to put as the value with the given
     *            {@link BlockPos}
     */
    public void put(BlockPos pos, int tag) {
	tags.put(pos, tag);
	markDirty();
    }

    /**
     * Reads the stored data from the given {@link NBTTagCompound} and fills the
     * tags with everything available. This involves the following steps:
     * <ol>
     * <li>Keys are retrieved with {@link NBTTagCompound#getKeySet()}.</li>
     * <li>Each key is converted to a {@link BlockPos} using
     * {@link BlockPos#fromLong(long)}.</li>
     * <li>Each key is used to retrieve an {@link Integer} from the given
     * {@link NBTTagCompound} using {@link NBTTagCompound#getInteger(String)}.</li>
     * <li>The {@link BlockPos} is used as the key and the {@link Integer} is used
     * as the value to call {@link Map#put(Object, Object)} with the tags.</li>
     * </ol>
     * If there is any {@link Exception} thrown during this process, it is assumed
     * that the tags are stored in an older format. In that case, the data is reread
     * using the old method. This involves the following steps:
     * <ol>
     * <li>Keys are retrieved with {@link NBTTagCompound#getKeySet()}.</li>
     * <li>Each key is used to retrieve a {@link NBTTagCompound} from the given
     * {@link NBTTagCompound} using
     * {@link NBTTagCompound#getCompoundTag(String)}.</li>
     * <li>The original steps are then followed using that
     * {@link NBTTagCompound}.</li>
     * </ol>
     */
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
	try {
	    tags.clear();
	    nbt.getKeySet().forEach(key -> tags.put(BlockPos.fromLong(Long.valueOf(key)), nbt.getInteger(key)));
	} catch (Exception e) {
	    System.err.println("Corrupt or old data file found. Attempting to update!");
	    nbt.getKeySet().forEach(name -> {
		NBTTagCompound compound = nbt.getCompoundTag(name);
		compound.getKeySet()
			.forEach(key -> tags.put(BlockPos.fromLong(Long.valueOf(key)), compound.getInteger(key)));
	    });
	    System.out.println("Success!");
	}
    }

    /**
     * Removes the given {@link BlockPos} from the tags and calls
     * {@link #markDirty()}.
     *
     * @param pos the {@link BlockPos} to remove
     */
    public void remove(BlockPos pos) {
	tags.remove(pos);
	markDirty();
    }

    /**
     * Writes the currently stored data in the tags to the given
     * {@link NBTTagCompound} which is turned into persistent data across sessions.
     */
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
	tags.forEach((pos, tag) -> compound.setInteger(String.valueOf(pos.toLong()), tag));
	return compound;
    }
}
