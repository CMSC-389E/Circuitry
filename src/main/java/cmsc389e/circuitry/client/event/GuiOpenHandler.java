package cmsc389e.circuitry.client.event;

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
import net.minecraftforge.fml.relauncher.Side;

/**
 * Modifies the world creation to have default settings more conducive to
 * pain-free Redstone programming.<br>
 * <br>
 * More specifically, it sets the following values:
 * <li><i>Game Mode</i> to <i>Creative</i>
 * <li><i>Generate Structures</i> to <i>OFF</i>
 * <li><i>World Type</i> to <i>Superflat</i>
 * <li><i>Superflat Customization Preset</i> to a clone of <i>Redstone Ready</i>
 */
@EventBusSubscriber(Side.CLIENT)
public class GuiOpenHandler {
    /**
     * Pretty much a copy and paste of the Readstone Ready preset settings.
     *
     * @return a String representation of the preset
     */
    private static String getPreset() {
	FlatGeneratorInfo flatgeneratorinfo = new FlatGeneratorInfo();
	flatgeneratorinfo.getFlatLayers().add(new FlatLayerInfo(1, Blocks.BEDROCK));
	flatgeneratorinfo.getFlatLayers().add(new FlatLayerInfo(3, Blocks.STONE));
	flatgeneratorinfo.getFlatLayers().add(new FlatLayerInfo(52, Blocks.SANDSTONE));
	flatgeneratorinfo.setBiome(Biome.getIdForBiome(Biomes.DESERT));
	flatgeneratorinfo.updateLayers();
	return flatgeneratorinfo.toString();
    }

    /**
     * Modifies default settings for world creation to be more Redstone-friendly.
     *
     * @param event a {@link GuiOpenEvent}
     */
    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
	if (event.getGui() instanceof GuiCreateWorld) {
	    WorldType.WORLD_TYPES = new WorldType[] { WorldType.FLAT };
	    GuiCreateWorld screen = (GuiCreateWorld) event.getGui();
	    ObfuscationReflectionHelper.setPrivateValue(GuiCreateWorld.class, screen, "creative", "field_146342_r"); // gameMode
	    ObfuscationReflectionHelper.setPrivateValue(GuiCreateWorld.class, screen, false, "field_146341_s"); // generateStructuresEnabled
	    ObfuscationReflectionHelper.setPrivateValue(GuiCreateWorld.class, screen, true, "field_146340_t"); // allowCheats
	    screen.chunkProviderSettingsJson = getPreset();
	}
    }
}
