package cmsc389e.circuitry.client.event;

import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiFlatPresets;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Handles GUI related {@link Event}s.
 */
@EventBusSubscriber(Side.CLIENT)
public class GuiHandler {
    /**
     * In a slightly over-complicated process, the settings for the Redstone Ready
     * preset is obtained and returned. What makes it so complicated is that
     * {@link GuiFlatPresets#FLAT_WORLD_PRESETS} and
     * {@code GuiFlatPresets.LayerItem} are both private so nothing can be easily
     * accessed.
     * <hr>
     * <b>The following steps are performed:</b>
     * <ol>
     * <li>The translation key for the preset is translated to the current language
     * with {@link I18n#format(String, Object...)}.</li>
     * <li>The {@link Class} object for {@code GuiFlatPresets.LayerItem} is
     * acquired. It is saved to a {@code Class<Object>} so that
     * {@link ObfuscationReflectionHelper#getPrivateValue(Class, Object, String...)}
     * doesn't need to cast each element.</li>
     * <li>{@link GuiFlatPresets#FLAT_WORLD_PRESETS} is acquired and iterated
     * through.</li>
     * <li>{@code GuiFlatPresets.LayerItem.name} is acquired and
     * {@link String#equals(Object)} is called to see if it is the same as the
     * Redstone Ready preset's name.</li>
     * <li>Finally, if the previous item is true,
     * {@code GuiFlatPresets.LayerItem.generatorInfo} is acquired and returned.</li>
     * </ol>
     * <hr>
     * <b>Mappings</b>
     * <ul>
     * <li>{@code field_146431_f} = {@link GuiFlatPresets#FLAT_WORLD_PRESETS}</li>
     * <li>{@code field_148232_b} = {@code GuiFlatPresets.LayerItem.name}</li>
     * <li>{@code field_148233_c} =
     * {@code GuiFlatPresets.LayerItem.generatorInfo}</li>
     * </ul>
     *
     * @return a String representation of the Redstone Ready preset
     */
    private static String getPreset() {
	String name = I18n.format("createWorld.customize.preset.redstone_ready");
	Class<Object> cl = ReflectionHelper.getClass(null, GuiFlatPresets.class.getName() + "$LayerItem");
	for (Object obj : (Iterable<?>) ObfuscationReflectionHelper.getPrivateValue(GuiFlatPresets.class, null,
		"field_146431_f"))
	    if (ObfuscationReflectionHelper.getPrivateValue(cl, obj, "field_148232_b").equals(name))
		return ObfuscationReflectionHelper.getPrivateValue(cl, obj, "field_148233_c");
	return null;
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
