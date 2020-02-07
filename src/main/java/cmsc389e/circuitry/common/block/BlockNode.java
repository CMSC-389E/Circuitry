package cmsc389e.circuitry.common.block;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.ConfigCircuitry;
import cmsc389e.circuitry.common.world.data.CircuitryWorldSavedData;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockNode extends Block {
    private static PropertyBool powered = PropertyBool.create("powered");

    protected static void cyclePowered(World world, BlockPos pos, IBlockState state) {
	world.setBlockState(pos, state.cycleProperty(powered));
    }

    public static String getTag(World world, BlockPos pos) {
	return CircuitryWorldSavedData.get(world).get(pos);
    }

    public static boolean isPowered(IBlockState state) {
	return state.getValue(powered);
    }

    protected static int nextTagInteger(World world, BlockPos pos) {
	String tag = getTag(world, pos);
	return tag == null ? 0 : (Integer.valueOf(tag.substring(1)) + 1) % ConfigCircuitry.tagLimit;
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
	    CircuitryWorldSavedData.get(world).remove(this, pos);
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

    protected abstract String nextTag(World world, BlockPos pos);

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
	    EnumFacing facing, float hitX, float hitY, float hitZ) {
	if (!world.isRemote && player.isSneaking()) {
	    CircuitryWorldSavedData.get(world).put(pos, nextTag(world, pos));
	    return false;
	}
	return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
	    ItemStack stack) {
	if (!world.isRemote)
	    CircuitryWorldSavedData.get(world).put(this, pos, nextTag(world, pos));
    }
}