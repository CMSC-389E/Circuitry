package cmsc389e.circuitry.common.block;

import cmsc389e.circuitry.common.ConfigCircuitry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * TODO
 */
public class BlockOutNode extends BlockNode {
    /**
     * TODO
     *
     * @param world TODO
     * @param pos   TODO
     * @param state TODO
     */
    private static void update(World world, BlockPos pos, IBlockState state) {
	if (!world.isRemote)
	    setPowered(world, pos, state, world.isBlockPowered(pos));
    }

    /**
     * TODO
     */
    public BlockOutNode() {
	super("out_node");
    }

    /**
     * TODO
     */
    @Override
    public String[] getTags() {
	return ConfigCircuitry.outTags;
    }

    /**
     * TODO
     */
    @Deprecated
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
	update(world, pos, state);
    }

    /**
     * TODO
     */
    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
	update(world, pos, state);
    }
}