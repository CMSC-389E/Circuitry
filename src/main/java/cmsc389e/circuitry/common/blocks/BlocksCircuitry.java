package cmsc389e.circuitry.common.blocks;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class BlocksCircuitry {
    public static Block inNode = new BlockInNode(), outNode = new BlockOutNode();

    @SubscribeEvent
    public static void registerBlocks(Register<Block> event) {
	event.getRegistry().registerAll(inNode, outNode, new BlockCircuitryRedstoneRepeater(false),
		new BlockCircuitryRedstoneRepeater(true), new BlockCircuitryRedstoneTorch(false),
		new BlockCircuitryRedstoneTorch(true), new BlockCircuitryRedstoneWire());
    }
}