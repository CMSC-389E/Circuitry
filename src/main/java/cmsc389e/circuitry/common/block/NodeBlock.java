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

	public static boolean isPowered(BlockState state) {
		return state.get(POWERED).booleanValue();
	}

	public static void setPowered(World world, BlockState state, BlockPos pos, boolean powered) {
		if (isPowered(state) != powered)
			world.setBlockState(pos, state.cycle(POWERED));
	}

	public NodeBlock() {
		super(Properties.create(Material.IRON));
		setDefaultState(getDefaultState().with(POWERED, Boolean.FALSE));
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new NodeTileEntity();
	}

	@Override
	public void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}

	@Override
	public int getLightValue(BlockState state) {
		return isPowered(state) ? 15 : 0;
	}

	public abstract String[] getNodeTags();

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if (!worldIn.isRemote && player.hasPermissionLevel(4))
			((NodeTileEntity) worldIn.getTileEntity(pos)).changeIndex(1);
		return ActionResultType.SUCCESS;
	}
}
