package cmsc389e.circuitry.common.block;

import cmsc389e.circuitry.common.block.override.BlockCircuitryRedstoneRepeater;
import cmsc389e.circuitry.common.block.override.BlockCircuitryRedstoneTorch;
import cmsc389e.circuitry.common.block.override.BlockCircuitryRedstoneWire;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class BlocksCircuitry {
    public static final Block IN_NODE = new BlockOutNode(), OUT_NODE = new BlockInNode();

    @SubscribeEvent
    public static void registerBlocks(Register<Block> event) {
	event.getRegistry().registerAll(IN_NODE, OUT_NODE, new BlockCircuitryRedstoneRepeater(false),
		new BlockCircuitryRedstoneRepeater(true), new BlockCircuitryRedstoneTorch(false),
		new BlockCircuitryRedstoneTorch(true), new BlockCircuitryRedstoneWire());
    }
}