package cmsc389e.circuitry.common.items;

import cmsc389e.circuitry.common.blocks.BlocksCircuitry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class ItemsCircuitry {
    public static Item inNode = newItemBlock(BlocksCircuitry.inNode), outNode = newItemBlock(BlocksCircuitry.outNode);

    private static Item newItemBlock(Block block) {
	return new ItemBlock(block).setRegistryName(block.getRegistryName());
    }

    @SubscribeEvent
    public static void registerItems(Register<Item> event) {
	event.getRegistry().registerAll(inNode, outNode);
    }
}