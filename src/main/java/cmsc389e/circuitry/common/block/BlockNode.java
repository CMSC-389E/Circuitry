package cmsc389e.circuitry.common.block;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.world.NodeWorldSavedData;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockNode extends Block {
    private static PropertyBool powered = PropertyBool.create("powered");

    public static void cyclePowered(World world, BlockPos pos, IBlockState state) {
	world.setBlockState(pos, state.cycleProperty(powered));
    }

    public static boolean isPowered(IBlockState state) {
	return state.getValue(powered);
    }

    public static void setPowered(World world, BlockPos pos, IBlockState state, boolean isPowered) {
	if (isPowered(state) != isPowered)
	    cyclePowered(world, pos, state);
    }

    protected BlockNode(String registryName) {
	super(Material.ROCK);
	setCreativeTab(CreativeTabs.REDSTONE).setRegistryName(registryName)
		.setTranslationKey(Circuitry.MODID + "." + getRegistryName().getPath());
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
	if (!world.isRemote)
	    NodeWorldSavedData.breakNode(world, pos, state);
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

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
	    ItemStack stack) {
	if (!world.isRemote)
	    NodeWorldSavedData.onNodePlacedBy(world, pos, state, placer);
    }
}