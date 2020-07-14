package cmsc389e.circuitry.common.block;

import java.util.List;

import cmsc389e.circuitry.common.Config;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OutNodeBlock extends NodeBlock {
	public OutNodeBlock() {
		super("out_node");
	}

	@Override
	public List<String> getNodeTags() {
		return Config.OUT_TAGS.get();
	}

	@Override
	public String getPrefix() {
		return "o";
	}

	@SuppressWarnings("resource")
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(POWERED, context.getWorld().isBlockPowered(context.getPos()));
	}

	@Deprecated
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
			boolean isMoving) {
		if (!worldIn.isRemote())
			setPowered(worldIn, state, pos, worldIn.isBlockPowered(pos));
	}
}
