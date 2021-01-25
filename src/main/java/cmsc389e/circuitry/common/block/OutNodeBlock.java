package cmsc389e.circuitry.common.block;

import cmsc389e.circuitry.common.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OutNodeBlock extends NodeBlock {
	@Override
	public String[] getNodeTags() {
		return Config.outTags;
	}

	@Override
	@SuppressWarnings("resource")
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).with(POWERED,
				Boolean.valueOf(context.getWorld().isBlockPowered(context.getPos())));
	}

	@Override
	public String getTranslationKey() {
		return "Out Node";
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
			boolean isMoving) {
		if (!worldIn.isRemote())
			setPowered(worldIn, state, pos, worldIn.isBlockPowered(pos));
	}
}
