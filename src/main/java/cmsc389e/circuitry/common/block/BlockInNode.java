package cmsc389e.circuitry.common.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockInNode extends BlockNode {
    public BlockInNode() {
	super("in_node");
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
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
	    EnumFacing facing, float hitX, float hitY, float hitZ) {
	if (world.isRemote)
	    return true;
	cyclePowered(world, pos, state);
	return false;
    }
}