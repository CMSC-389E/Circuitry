package cmsc389e.circuitry.common.block.override;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class BlockCircuitryRedstoneTorch extends BlockRedstoneTorch {
    private static boolean shouldBeOff(World worldIn, BlockPos pos, IBlockState state) {
	EnumFacing enumfacing = state.getValue(FACING).getOpposite();
	return worldIn.isSidePowered(pos.offset(enumfacing), enumfacing);
    }

    public BlockCircuitryRedstoneTorch(boolean isOn) {
	super(isOn);
	setCreativeTab(CreativeTabs.REDSTONE)
		.setRegistryName((isOn ? Blocks.REDSTONE_TORCH : Blocks.UNLIT_REDSTONE_TORCH).getRegistryName())
		.setTranslationKey("notGate");
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
	if (!onNeighborChangeInternal(worldIn, pos, state)) {
	    boolean flag = shouldBeOff(worldIn, pos, state);
	    if ((boolean) ObfuscationReflectionHelper.getPrivateValue(BlockRedstoneTorch.class, this,
		    "field_150113_a") == flag)
		worldIn.setBlockState(pos, (flag ? Blocks.UNLIT_REDSTONE_TORCH : Blocks.REDSTONE_TORCH)
			.getDefaultState().withProperty(FACING, state.getValue(FACING)));
	}
    }
}