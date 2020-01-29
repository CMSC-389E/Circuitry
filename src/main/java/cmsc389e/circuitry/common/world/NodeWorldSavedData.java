package cmsc389e.circuitry.common.world;

import java.util.Collection;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cmsc389e.circuitry.Circuitry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class NodeWorldSavedData extends WorldSavedData {
    private static final String DATA_NAME = Circuitry.MODID + "_NodeData";

    public static void breakNode(World world, BlockPos pos, IBlockState state) {
	get(world).remove(state.getBlock(), pos);
    }

    public static NodeWorldSavedData get(World world) {
	MapStorage storage = world.getMapStorage();
	NodeWorldSavedData instance = (NodeWorldSavedData) storage.getOrLoadData(NodeWorldSavedData.class, DATA_NAME);
	if (instance == null) {
	    instance = new NodeWorldSavedData();
	    storage.setData(DATA_NAME, instance);
	}
	return instance;
    }

    public static void onNodePlacedBy(World world, BlockPos pos, IBlockState state,
	    @SuppressWarnings("unused") EntityLivingBase placer) {
	get(world).put(state.getBlock(), pos);
    }

    private final Multimap<Block, BlockPos> posMap;

    public NodeWorldSavedData() {
	super(DATA_NAME);
	posMap = HashMultimap.create();
    }

    public Collection<BlockPos> getBlockData(Block block) {
	return posMap.get(block);
    }

    private void put(Block block, BlockPos pos) {
	posMap.put(block, pos);
	markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
	posMap.clear();
	nbt.getKeySet().forEach(name -> posMap.put(Block.getBlockFromName(name), BlockPos.fromLong(nbt.getLong(name))));
    }

    private void remove(Block block, BlockPos pos) {
	posMap.remove(block, pos);
	markDirty();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
	posMap.forEach((block, pos) -> compound.setLong(block.getLocalizedName(), pos.toLong()));
	return compound;
    }
}
