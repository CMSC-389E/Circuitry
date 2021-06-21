package cmsc389e.circuitry.common.block;

import cmsc389e.circuitry.common.Config;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public final class InNodeBlock extends NodeBlock {
	@Deprecated
	@Override
	public boolean canProvidePower(final BlockState state) {
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
	public int getWeakPower(final BlockState blockState, final IBlockReader blockAccess, final BlockPos pos,
			final Direction side) {
		return isPowered(blockState) ? 15 : 0;
	}
}
