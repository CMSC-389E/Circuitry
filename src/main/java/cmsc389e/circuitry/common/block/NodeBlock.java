package cmsc389e.circuitry.common.block;

import cmsc389e.circuitry.common.Config;
import cmsc389e.circuitry.common.Config.Key;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;

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
	public void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}

	@Override
	public int getLightValue(BlockState state) {
		return state.get(POWERED) ? Config.get(Key.LIGHT) : 0;
	}
}