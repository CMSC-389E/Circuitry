package cmsc389e.circuitry.common.block;

import javax.annotation.Nullable;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.CircuitryWorldSavedData;
import cmsc389e.circuitry.common.network.CircuitryPacketHandler;
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

/**
 * TODO
 */
public abstract class BlockNode extends Block {
    private static PropertyBool POWERED = PropertyBool.create("powered");

    /**
     * TODO
     *
     * @param world TODO
     * @param pos   TODO
     * @param state TODO
     * @return TODO
     */
    public static String getTag(World world, BlockPos pos, IBlockState state) {
	Integer tag = CircuitryWorldSavedData.get(world).get(pos);
	if (tag != null) {
	    String[] tags = ((BlockNode) state.getBlock()).getTags();
	    return tags.length == 0 ? String.valueOf(tag) : tags[mod(tag, tags.length)];
	}
	return null;
    }

    /**
     * TODO
     *
     * @param state TODO
     * @return TODO
     */
    public static boolean isPowered(IBlockState state) {
	return state.getValue(POWERED);
    }

    /**
     * TODO
     *
     * @param tag TODO
     * @param mod TODO
     * @return TODO
     */
    private static int mod(int tag, int mod) {
	tag %= mod;
	if (tag < 0)
	    tag = mod + tag;
	return tag;
    }

    /**
     * TODO
     *
     * @param player TODO
     * @return TODO
     */
    @Nullable
    public static BlockPos rayTraceEyes(EntityPlayer player) {
	RayTraceResult result = ForgeHooks.rayTraceEyes(player,
		player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() + 1);
	if (result == null)
	    return null;
	BlockPos pos = result.getBlockPos();
	return player.getEntityWorld().getBlockState(pos).getBlock() instanceof BlockNode ? pos : null;
    }

    /**
     * TODO
     *
     * @param world     TODO
     * @param pos       TODO
     * @param state     TODO
     * @param isPowered TODO
     */
    public static void setPowered(World world, BlockPos pos, IBlockState state, boolean isPowered) {
	world.setBlockState(pos, state.withProperty(POWERED, isPowered));
    }

    /**
     * TODO
     *
     * @param registryName TODO
     */
    public BlockNode(String registryName) {
	super(Material.ROCK);
	setCreativeTab(CreativeTabs.REDSTONE).setRegistryName(registryName)
		.setTranslationKey(Circuitry.MODID + "." + getRegistryName().getPath());
    }

    /**
     * TODO
     */
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
	if (!world.isRemote)
	    CircuitryWorldSavedData.get(world).remove(pos);
    }

    /**
     * TODO
     */
    @Override
    public BlockStateContainer createBlockState() {
	return new BlockStateContainer(this, POWERED);
    }

    /**
     * TODO
     */
    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
	return isPowered(state) ? 15 : 0;
    }

    /**
     * TODO
     */
    @Override
    public int getMetaFromState(IBlockState state) {
	return isPowered(state) ? 1 : 0;
    }

    /**
     * TODO
     */
    @Deprecated
    @Override
    public IBlockState getStateFromMeta(int meta) {
	return getDefaultState().withProperty(POWERED, meta == 1);
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public abstract String[] getTags();

    /**
     * TODO
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
	    EnumFacing facing, float hitX, float hitY, float hitZ) {
	if (!world.isRemote) {
	    CircuitryWorldSavedData data = CircuitryWorldSavedData.get(world);
	    data.put(pos, data.get(pos) + (CircuitryPacketHandler.isPlayerHoldingModifier(player) ? -1 : 1));
	}
	return true;
    }

    /**
     * TODO
     */
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer,
	    ItemStack stack) {
	if (!world.isRemote)
	    CircuitryWorldSavedData.get(world).put(pos, 0);
    }
}