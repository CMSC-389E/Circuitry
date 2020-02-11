package cmsc389e.circuitry.common.block;

import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCircuitryRedstoneRepeater extends BlockRedstoneRepeater {
    protected BlockCircuitryRedstoneRepeater(boolean powered) {
	super(powered);
	setRegistryName((powered ? Blocks.POWERED_REPEATER : Blocks.UNPOWERED_REPEATER).getRegistryName());
    }

    @Override
    public void updateState(World world, BlockPos pos, IBlockState state) {
	if (!world.isRemote && !isLocked(world, pos, state)) {
	    boolean flag = shouldBePowered(world, pos, state);
	    if (isRepeaterPowered != flag && !world.isBlockTickPending(pos, this))
		if (getDelay(state) == 2)
		    world.setBlockState(pos, flag ? getPoweredState(state) : getUnpoweredState(state));
		else {
		    int i = -1;
		    if (isFacingTowardsRepeater(world, pos, state))
			i = -3;
		    else if (isRepeaterPowered)
			i = -2;
		    world.updateBlockTick(pos, this, getDelay(state) - 1, i);
		}
	}
    }
}