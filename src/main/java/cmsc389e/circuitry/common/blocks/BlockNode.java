package cmsc389e.circuitry.common.blocks;

import cmsc389e.circuitry.Circuitry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BlockNode extends Block {
    protected static PropertyBool powered = PropertyBool.create("powered");

    protected static boolean isPowered(IBlockState state) {
	return state.getValue(powered);
    }

    protected BlockNode(String registryName) {
	super(Material.ROCK);
	setCreativeTab(CreativeTabs.REDSTONE).setRegistryName(registryName)
		.setTranslationKey(Circuitry.MODID + "." + getRegistryName().getPath());
    }

    @Override
    public BlockStateContainer createBlockState() {
	return new BlockStateContainer(this, powered);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
	return isPowered(state) ? 15 : 0;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
	return isPowered(state) ? 1 : 0;
    }

    @Deprecated
    @Override
    public IBlockState getStateFromMeta(int meta) {
	return getDefaultState().withProperty(powered, meta == 1);
    }
}