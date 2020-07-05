package cmsc389e.circuitry.common.block;

import cmsc389e.circuitry.common.Config;
import cmsc389e.circuitry.common.Config.Key;
import cmsc389e.circuitry.common.NodeTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class NodeBlock extends Block {
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");

	public NodeBlock() {
		super(Block.Properties.create(Material.IRON));
		setDefaultState(getDefaultState().with(POWERED, false));
	}

	public BlockItem createItem() {
		return new BlockItem(this, new Item.Properties().group(ItemGroup.REDSTONE));
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new NodeTileEntity();
	}

	@Override
	public void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}

	/**
	 * Called when a {@link InNodeBlock} is right-clicked and the
	 * {@link PlayerEntity} is not crouching. The {@link World} is not checked
	 * whether it is remote since all this method does is modify
	 * {@link NodeTileEntity#tag} at the {@link BlockPos}. On {@link Chunk} load,
	 * the client-side {@link NodeTileEntity} is synced to the server-side one. That
	 * means {@link NodeTileEntity#tag} will always be synchronized.
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
		NodeTileEntity entity = (NodeTileEntity) world.getTileEntity(pos);
		entity.setTag(entity.getTag() + 1);
		return ActionResultType.SUCCESS;
	}

	@Override
	public int getLightValue(BlockState state) {
		return state.get(POWERED) ? Config.get(Key.LIGHT) : 0;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
}