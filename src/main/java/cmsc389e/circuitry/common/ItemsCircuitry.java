package cmsc389e.circuitry.common;

import cmsc389e.circuitry.common.block.BlocksCircuitry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * TODO
 */
@EventBusSubscriber
public class ItemsCircuitry {
    public static Item IN_NODE = newItemBlock(BlocksCircuitry.IN_NODE),
	    OUT_NODE = newItemBlock(BlocksCircuitry.OUT_NODE);

    /**
     * TODO
     *
     * @param block TODO
     * @return TODO
     */
    private static Item newItemBlock(Block block) {
	return new ItemBlock(block).setRegistryName(block.getRegistryName());
    }

    /**
     * TODO
     *
     * @param event TODO
     */
    @SubscribeEvent
    public static void registerItems(Register<Item> event) {
	event.getRegistry().registerAll(IN_NODE, OUT_NODE);
    }
}