package cmsc389e.circuitry.common.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockOutNode extends BlockNode {
    protected BlockOutNode() {
	super("out_node");
    }

    @Deprecated
    @Override
    public boolean canProvidePower(IBlockState state) {
	return true;
    }

    @Deprecated
    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
	return isPowered(blockState) ? 15 : 0;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
	    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
	if (!worldIn.isRemote) {
	    worldIn.setBlockState(pos, state.cycleProperty(powered));
	    return false;
	}
	return true;
    }
}