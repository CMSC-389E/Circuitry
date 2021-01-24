package cmsc389e.circuitry.common.block;

import cmsc389e.circuitry.common.Config;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class InNodeBlock extends NodeBlock {
	@Deprecated
	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}

	@Override
	public String[] getNodeTags() {
		return Config.inTags;
	}

	@Override
	public String getTranslationKey() {
		return "In Node";
	}

	@Deprecated
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return blockState.get(POWERED) ? 15 : 0;
	}
}
