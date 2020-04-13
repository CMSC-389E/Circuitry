package cmsc389e.circuitry.common.block;

import cmsc389e.circuitry.common.ConfigCircuitry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

/**
 * TODO
 */
public class BlockInNode extends BlockNode {
    /**
     * TODO
     */
    public BlockInNode() {
	super("in_node");
    }

    /**
     * TODO
     */
    @Deprecated
    @Override
    public boolean canProvidePower(IBlockState state) {
	return true;
    }

    /**
     * TODO
     */
    @Override
    public String[] getTags() {
	return ConfigCircuitry.inTags;
    }

    /**
     * TODO
     */
    @Deprecated
    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
	return isPowered(blockState) ? 15 : 0;
    }
}