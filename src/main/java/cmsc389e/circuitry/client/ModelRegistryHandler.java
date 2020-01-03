package cmsc389e.circuitry.client;

import cmsc389e.circuitry.common.items.ItemsCircuitry;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class ModelRegistryHandler {
    private static void registerModels(Item... items) {
	for (Item item : items)
	    ModelLoader.setCustomModelResourceLocation(item, 0,
		    new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
	registerModels(ItemsCircuitry.inNode, ItemsCircuitry.outNode);
    }
}