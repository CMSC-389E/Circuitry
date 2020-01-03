package cmsc389e.circuitry.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockInNode extends BlockNode {
    private static void update(World worldIn, BlockPos pos, IBlockState state) {
	if (!worldIn.isRemote && isPowered(state) != worldIn.isBlockPowered(pos))
	    cyclePowered(worldIn, pos, state);
    }

    protected BlockInNode() {
	super("in_node");
    }

    @Deprecated
    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
	update(worldIn, pos, state);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
	update(worldIn, pos, state);
    }
}