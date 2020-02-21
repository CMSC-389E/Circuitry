package cmsc389e.circuitry.common.block;

import javax.annotation.Nullable;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.world.CircuitryWorldSavedData;
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
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public abstract class BlockNode extends Block {
    private static PropertyBool POWERED = PropertyBool.create("powered");

    public static String getTag(World world, BlockPos pos, IBlockState state) {
	Integer tag = CircuitryWorldSavedData.get(world).get(pos);
	if (tag != null) {
	    String[] tags = ((BlockNode) state.getBlock()).getTags();
	    return tags.length == 0 ? String.valueOf(tag) : tags[mod(tag, tags.length)];
	}
	return null;
    }

    public static boolean isPowered(IBlockState state) {
	return state.getValue(POWERED);
    }

    private static int mod(int tag, int mod) {
	tag %= mod;
	if (tag < 0)
	    tag = mod + tag;
	return tag;
    }

    @Nullable
    public static BlockPos rayTraceEyes(EntityPlayer player) {
	RayTraceResult result = ForgeHooks.rayTraceEyes(player,
		player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() + 1);
	if (result == null)
	    return null;
	BlockPos pos = result.getBlockPos();
	return player.getEntityWorld().getBlockState(pos).getBlock() instanceof BlockNode ? pos : null;
    }

    public static void setPowered(World world, BlockPos pos, IBlockState state, boolean isPowered) {
	world.setBlockState(pos, state.withProperty(POWERED, isPowered));
    }

    private static boolean shouldDecreaseTag(EntityPlayer player) {
	// temporarily using sneaking
	return player.isSneaking();
    }

    public BlockNode(String registryName) {
	super(Material.ROCK);
	setCreativeTab(CreativeTabs.REDSTONE).setRegistryName(registryName)
		.setTranslationKey(Circuitry.MODID + "." + getRegistryName().getPath());
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
	if (!world.isRemote)
	    CircuitryWorldSavedData.get(world).remove(pos);
    }

    @Override
    public BlockStateContainer createBlockState() {
	return new BlockStateContainer(this, POWERED);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
	return isPowered(state) ? 15 : 0;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
	return isPowered(state) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
	return getDefaultState().withProperty(POWERED, meta == 1);
    }

    public abstract String[] getTags();

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
	    EnumFacing facing, float hitX, float hitY, float hitZ) {
	if (!world.isRemote) {
	    CircuitryWorldSavedData data = CircuitryWorldSavedData.get(world);
	    data.put(pos, data.get(pos) + (shouldDecreaseTag(player) ? -1 : 1));
	}
	return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
	    ItemStack stack) {
	if (!world.isRemote)
	    CircuitryWorldSavedData.get(world).put(pos, 0);
    }
}