package cmsc389e.circuitry.common.blocks;

import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.init.Blocks;

public class BlockCircuitryRedstoneWire extends BlockRedstoneWire {
    public BlockCircuitryRedstoneWire() {
	super();
	setRegistryName(Blocks.REDSTONE_WIRE.getRegistryName());
    }
}