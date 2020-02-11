package cmsc389e.circuitry.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class BlockCircuitryRedstoneTorch extends BlockRedstoneTorch {
    private static final ITextComponent ERROR = new TextComponentString(
	    "An infinite redstone loop was caught and the redstone torch responsible sacked. Please check your circuit for loops.")
		    .setStyle(new Style().setColor(TextFormatting.RED));

    private static boolean shouldBeOff(World world, BlockPos pos, IBlockState state) {
	EnumFacing enumfacing = state.getValue(FACING).getOpposite();
	return world.isSidePowered(pos.offset(enumfacing), enumfacing);
    }

    protected BlockCircuitryRedstoneTorch(boolean isOn) {
	super(isOn);
	setCreativeTab(CreativeTabs.REDSTONE)
		.setRegistryName((isOn ? Blocks.REDSTONE_TORCH : Blocks.UNLIT_REDSTONE_TORCH).getRegistryName())
		.setTranslationKey("notGate");
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
	if (!world.isRemote)
	    try {
		if (!onNeighborChangeInternal(world, pos, state)) {
		    boolean flag = shouldBeOff(world, pos, state);
		    if ((boolean) ObfuscationReflectionHelper.getPrivateValue(BlockRedstoneTorch.class, this,
			    "field_150113_a") == flag)
			world.setBlockState(pos, (flag ? Blocks.UNLIT_REDSTONE_TORCH : Blocks.REDSTONE_TORCH)
				.getDefaultState().withProperty(FACING, state.getValue(FACING)));
		}
	    } catch (Exception e) {
		world.getMinecraftServer().getPlayerList().sendMessage(ERROR);
		world.addWeatherEffect(new EntityLightningBolt(world, pos.getX(), pos.getY(), pos.getZ(), true)); // No
														  // damage/Fire
		world.setBlockToAir(pos); // Easiest way to prevent recursion loop is to break the block
	    }
    }
}