package cmsc389e.circuitry.common.block;

import cmsc389e.circuitry.common.NodeTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class NodeBlock extends Block {
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public static final boolean isPowered(final BlockState state) {
		return state.get(POWERED).booleanValue();
	}

	public static final void setPowered(final World world, final BlockState state, final BlockPos pos,
			final boolean powered) {
		if (isPowered(state) != powered)
			world.setBlockState(pos, state.cycle(POWERED));
	}

	public NodeBlock() {
		super(Properties.create(Material.IRON));
		setDefaultState(getDefaultState().with(POWERED, Boolean.FALSE));
	}

	@Override
	public final TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return new NodeTileEntity();
	}

	@Override
	public final void fillStateContainer(final Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}

	@Deprecated
	@Override
	public final int getLightValue(final BlockState state) {
		return isPowered(state) ? 15 : 0;
	}

	public abstract String[] getNodeTags();

	@Override
	public final boolean hasTileEntity(final BlockState state) {
		return true;
	}

	@Deprecated
	@Override
	public final ActionResultType onBlockActivated(final BlockState state, final World worldIn, final BlockPos pos,
			final PlayerEntity player, final Hand handIn, final BlockRayTraceResult hit) {
		if (!worldIn.isRemote && player.hasPermissionLevel(4))
			((NodeTileEntity) worldIn.getTileEntity(pos)).changeIndex(1);
		return ActionResultType.SUCCESS;
	}
}
