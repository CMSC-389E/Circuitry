package cmsc389e.circuitry.client.event;

import cmsc389e.circuitry.common.ItemsCircuitry;
import cmsc389e.circuitry.common.block.BlockInNode;
import cmsc389e.circuitry.common.block.BlockOutNode;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Registers {@link BlockOutNode} and {@link BlockInNode} item models so that
 * they display correctly in the inventory.
 */
@EventBusSubscriber(Side.CLIENT)
public class ModelHandler {
    /**
     * TODO
     *
     * @param event TODO
     */
    @SubscribeEvent
    public static void onModelRegistry(@SuppressWarnings("unused") ModelRegistryEvent event) {
	registerModels(ItemsCircuitry.IN_NODE, ItemsCircuitry.OUT_NODE);
    }

    /**
     * Registers all the {@link Item}s' models passed in.
     *
     * @param items {@link Item}s' models to register
     */
    private static void registerModels(Item... items) {
	for (Item item : items)
	    ModelLoader.setCustomModelResourceLocation(item, 0,
		    new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}