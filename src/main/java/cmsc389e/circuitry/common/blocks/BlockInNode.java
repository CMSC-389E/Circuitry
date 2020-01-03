package cmsc389e.circuitry.common.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockInNode extends BlockNode {
    private static void update(World worldIn, BlockPos pos, IBlockState state) {
	if (!worldIn.isRemote) {
	    boolean isBlockPowered = worldIn.isBlockPowered(pos);
	    if (isPowered(state) != isBlockPowered)
		worldIn.setBlockState(pos, state.cycleProperty(powered));
	}
    }

    protected BlockInNode() {
	super("in_node");
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
	update(worldIn, pos, state);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
	if (world instanceof World)
	    update((World) world, pos, world.getBlockState(pos));
    }
}