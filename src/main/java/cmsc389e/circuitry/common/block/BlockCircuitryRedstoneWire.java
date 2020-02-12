package cmsc389e.circuitry.common.block;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCircuitryRedstoneWire extends BlockRedstoneWire {
    private static final Multimap<World, BlockPos> UPDATING = HashMultimap.create();

    public synchronized static boolean isUpdating(World world) {
	return !UPDATING.get(world).isEmpty();
    }

    protected BlockCircuitryRedstoneWire() {
	super();
	setRegistryName(Blocks.REDSTONE_WIRE.getRegistryName());
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
	UPDATING.put(world, pos);
	super.neighborChanged(state, world, pos, block, fromPos);
	UPDATING.remove(world, pos);
    }
}