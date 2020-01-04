package cmsc389e.circuitry.client.gui;

import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class GuiOpenHandler {
    private static String getPreset() {
	FlatGeneratorInfo flatgeneratorinfo = new FlatGeneratorInfo();
	flatgeneratorinfo.getFlatLayers().add(new FlatLayerInfo(1, Blocks.BEDROCK));
	flatgeneratorinfo.getFlatLayers().add(new FlatLayerInfo(3, Blocks.STONE));
	flatgeneratorinfo.getFlatLayers().add(new FlatLayerInfo(52, Blocks.SANDSTONE));
	flatgeneratorinfo.setBiome(Biome.getIdForBiome(Biomes.DESERT));
	flatgeneratorinfo.updateLayers();
	return flatgeneratorinfo.toString();
    }

    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
	if (event.getGui() instanceof GuiCreateWorld) {
	    WorldType.WORLD_TYPES = new WorldType[] { WorldType.FLAT };
	    GuiCreateWorld screen = (GuiCreateWorld) event.getGui();
	    ObfuscationReflectionHelper.setPrivateValue(GuiCreateWorld.class, screen, "creative", "field_146342_r");
	    ObfuscationReflectionHelper.setPrivateValue(GuiCreateWorld.class, screen, false, "field_146341_s");
	    ObfuscationReflectionHelper.setPrivateValue(GuiCreateWorld.class, screen, true, "field_146340_t");
	    screen.chunkProviderSettingsJson = getPreset();
	}
    }
}
