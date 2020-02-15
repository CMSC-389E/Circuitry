package cmsc389e.circuitry.common.block;

import cmsc389e.circuitry.ConfigCircuitry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public final class BlockInNode extends BlockNode {
    protected BlockInNode() {
	super("in_node");
    }

    @Override
    public boolean canProvidePower(IBlockState state) {
	return true;
    }

    @Override
    protected String[] getTags() {
	return ConfigCircuitry.inputs;
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
	return isPowered(blockState) ? 15 : 0;
    }
}