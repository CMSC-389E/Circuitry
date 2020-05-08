package cmsc389e.circuitry.common;

import cmsc389e.circuitry.common.block.BlocksCircuitry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Code related to registering {@link Item}s for the mod.
 */
@EventBusSubscriber
public class ItemsCircuitry {
    public static Item IN_NODE = newItemBlock(BlocksCircuitry.IN_NODE),
	    OUT_NODE = newItemBlock(BlocksCircuitry.OUT_NODE);

    /**
     * Creates a new {@link ItemBlock} for the given {@link Block}. This just
     * equates to passing the {@link Block} into the {@link ItemBlock} constructor
     * and setting its registry name to match the {@link Block}'s registry name.
     *
     * @param block a {@link Block} to be converted to an {@link ItemBlock}
     * @return an {@link ItemBlock} representing the given {@link Block}
     */
    private static Item newItemBlock(Block block) {
	return new ItemBlock(block).setRegistryName(block.getRegistryName());
    }

    /**
     * Registers the mod's {@link Item}s with the {@link IForgeRegistry} during the
     * {@link Item} {@link Register} event.
     *
     * @param event the {@link Item} {@link Register} event
     */
    @SubscribeEvent
    public static void registerItems(Register<Item> event) {
	event.getRegistry().registerAll(IN_NODE, OUT_NODE);
    }
}