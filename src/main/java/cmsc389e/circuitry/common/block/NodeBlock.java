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
import net.minecraft.world.chunk.Chunk;

public abstract class NodeBlock extends Block {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public static void setPowered(World world, BlockState state, BlockPos pos, boolean powered) {
	if (state.get(POWERED) != powered)
	    world.setBlockState(pos, state.cycle(POWERED));
    }

    public NodeBlock() {
	super(Properties.create(Material.IRON).lightValue(15));
	setDefaultState(getDefaultState().with(POWERED, false));
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
	return new NodeTileEntity();
    }

    @Override
    public void fillStateContainer(Builder<Block, BlockState> builder) {
	builder.add(POWERED);
    }

    @Deprecated
    @Override
    public int getLightValue(BlockState state) {
	return state.get(POWERED) ? super.getLightValue(state) : 0;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
	return true;
    }

    /**
     * Called when a {@link NodeBlock} is right-clicked and the {@link PlayerEntity}
     * is not crouching. The {@link World} is not checked whether it is remote since
     * all this method does is modify {@link NodeTileEntity#index} at the
     * {@link BlockPos}. On {@link Chunk} load, the client-side
     * {@link NodeTileEntity} is synced to the server-side one. That means
     * {@link NodeTileEntity#index} will always be synchronized.
     *
     * @param state   the {@link BlockState} of the {@link NodeBlock} that was
     *                clicked
     * @param worldIn the {@link World} that the {@link NodeBlock} was clicked in
     * @param pos     the {@link BlockPos} of the {@link NodeBlock} that was clicked
     * @param player  the {@link PlayerEntity} who did the clicking
     * @param handIn  the {@link Hand} with which the {@link PlayerEntity} clicked
     * @param hit     where on the {@link NodeBlock}'s bounds it was hit
     * @return {@link ActionResultType#SUCCESS}, which tells the game that the
     *         action was consumed correctly
     * @deprecated
     */
    @Deprecated
    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
	    Hand handIn, BlockRayTraceResult hit) {
	NodeTileEntity te = (NodeTileEntity) worldIn.getTileEntity(pos);
	te.index++;
	te.markDirty();
	return ActionResultType.SUCCESS;
    }
}