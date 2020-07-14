package cmsc389e.circuitry.common.block;

import java.util.List;

import cmsc389e.circuitry.common.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class InNodeBlock extends NodeBlock {
	public InNodeBlock() {
		super("in_node");
	}

	/**
	 * Determines whether neighboring {@link Block}s can be powered by a
	 * {@link InNodeBlock}. This method always returns true, which causes
	 * {@link RedstoneBlock}s to be visually connected.
	 *
	 * @param state the {@link BlockState} of the {@link InNodeBlock}
	 * @deprecated
	 */
	@Deprecated
	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}

	@Override
	public List<String> getNodeTags() {
		return Config.IN_TAGS.get();
	}

	@Override
	public String getPrefix() {
		return "i";
	}

	/**
	 * Determines how much power a {@link InNodeBlock} creates. This is the distance
	 * {@link RedstoneBlock}s will be powered from where they are connected to the
	 * {@link InNodeBlock}. This power will not travel through solid {@link Block}s,
	 * unlike the power from {@link RepeaterBlock}s, for instance.
	 *
	 * @param blockState  the {@link BlockState} of the {@link InNodeBlock}
	 * @param blockAccess a {@link IBlockReader} which provides access to some
	 *                    information about the {@link InNodeBlock}
	 * @param pos         the {@link BlockPos} of the {@link InNodeBlock}
	 * @param side        the side of the {@link InNodeBlock} that the power is
	 *                    coming out of
	 * @return the power of the {@link InNodeBlock} between 0 and 15, inclusive
	 * @deprecated
	 */
	@Deprecated
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return blockState.get(POWERED) ? Config.POWER.get() : 0;
	}
}
