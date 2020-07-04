package cmsc389e.circuitry.common.block;

import cmsc389e.circuitry.common.Config;
import cmsc389e.circuitry.common.Config.Key;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class InNodeBlock extends NodeBlock {
	/**
	 * Determines whether neighboring {@link Block}s can be powered by a
	 * {@link InNodeBlock}. This method always returns true, which causes
	 * {@link RedstoneBlock}s to be visually connected.
	 *
	 * @param state the {@link BlockState} of the {@link InNodeBlock}
	 */
	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}

	/**
	 * Called when a {@link InNodeBlock} is right-clicked and the
	 * {@link PlayerEntity} is not crouching. This always cycles the value of
	 * {@link #POWERED} for the {@link BlockState}. This has the effect of toggling
	 * the {@link InNodeBlock} at the {@link BlockPos} on and off.
	 *
	 * @param state  the {@link BlockState} of the {@link InNodeBlock} that was
	 *               clicked
	 * @param world  the {@link World} that the {@link InNodeBlock} was clicked in
	 * @param pos    the {@link BlockPos} of the {@link InNodeBlock} that was
	 *               clicked
	 * @param player the {@link PlayerEntity} who did the clicking
	 * @param hand   the {@link Hand} with which the {@link PlayerEntity} clicked
	 * @param result where on the {@link InNodeBlock}'s bounds it was hit
	 * @return {@link ActionResultType#SUCCESS}, which tells the game that the
	 *         action was consumed correctly
	 */
	@Override
	public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockRayTraceResult result) {
		world.setBlockState(pos, state.cycle(POWERED));
		return ActionResultType.SUCCESS;
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
	 */
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
		return blockState.get(POWERED) ? Config.get(Key.POWER) : 0;
	}
}
