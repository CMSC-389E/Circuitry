package cmsc389e.circuitry.client.event;

import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiFlatPresets;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Handles GUI related {@link Event}s.
 */
@EventBusSubscriber(Side.CLIENT)
public class GuiHandler {
    /**
     * Creates a {@link String} preset that is a copy of the Redstone Ready preset
     * for Superflat worlds. The code is pretty much a copy and paste of line 187 of
     * {@link GuiFlatPresets}:<br>
     * <br>
     * {@code registerPreset(I18n.format("createWorld.customize.preset.redstone_ready"), Items.REDSTONE, Biomes.DESERT, Collections.emptyList(), new FlatLayerInfo(52, Blocks.SANDSTONE), new FlatLayerInfo(3, Blocks.STONE), new FlatLayerInfo(1, Blocks.BEDROCK));}
     *
     * @return a {@link String} representation of the Redstone Ready preset
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
     * Modifies the world creation to have default settings more conducive to
     * pain-free Redstone programming.
     * <hr>
     * <b>More specifically, it sets the following values:</b>
     * <ul>
     * <li><i>Game Mode</i> to <i>Creative</i></li>
     * <li><i>Generate Structures</i> to <i>OFF</i></li>
     * <li><i>World Type</i> to <i>Superflat</i></li>
     * <li><i>Superflat Customization Preset</i> to <i>Redstone Ready</i></li>
     * </ul>
     * <hr>
     * <b>Mappings</b>
     * <ul>
     * <li>{@code field_146342_r} = {@link GuiCreateWorld#gameMode}</li>
     * <li>{@code field_146341_s} =
     * {@link GuiCreateWorld#generateStructuresEnabled}</li>
     * <li>{@code field_146340_t} = {@link GuiCreateWorld#allowCheats}</li>
     * </ul>
     *
     * @param event the {@link GuiOpenEvent}
     */
    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
	GuiScreen screen = event.getGui();
	if (screen instanceof GuiCreateWorld) {
	    WorldType.WORLD_TYPES = new WorldType[] { WorldType.FLAT };
	    GuiCreateWorld createWorld = (GuiCreateWorld) screen;
	    ObfuscationReflectionHelper.setPrivateValue(GuiCreateWorld.class, createWorld, GameType.CREATIVE.getName(),
		    "field_146342_r");
	    ObfuscationReflectionHelper.setPrivateValue(GuiCreateWorld.class, createWorld, false, "field_146341_s");
	    ObfuscationReflectionHelper.setPrivateValue(GuiCreateWorld.class, createWorld, true, "field_146340_t");
	    createWorld.chunkProviderSettingsJson = getPreset();
	}
    }
}
