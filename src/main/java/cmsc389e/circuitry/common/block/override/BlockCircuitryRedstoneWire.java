package cmsc389e.circuitry.common.block.override;

import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockCircuitryRedstoneWire extends BlockRedstoneWire {
    protected World world;

    public BlockCircuitryRedstoneWire() {
	super();
	setRegistryName(Blocks.REDSTONE_WIRE.getRegistryName());
    }
}