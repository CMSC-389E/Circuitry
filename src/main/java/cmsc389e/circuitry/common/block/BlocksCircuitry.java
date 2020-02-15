package cmsc389e.circuitry.common.block;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public final class BlocksCircuitry {
    public static final Block IN_NODE = new BlockInNode(), OUT_NODE = new BlockOutNode();

    @SubscribeEvent
    public static void registerBlocks(Register<Block> event) {
	event.getRegistry().registerAll(IN_NODE, OUT_NODE, new BlockCircuitryRedstoneWire());
    }
}