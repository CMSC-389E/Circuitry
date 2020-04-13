package cmsc389e.circuitry.client;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.IProxy;
import cmsc389e.circuitry.common.network.CircuitryMessage.Key;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;

/**
 * Runs code specific to the physical client, but too generic to place into more
 * specialized classes. Also implements {@link IProxy} as the client-side proxy
 * instance.
 */
public class ClientProxy implements IProxy {
    public static final Map<Key, KeyBinding> KEY_BINDINGS = new HashMap<>();

    /**
     * Initializes {@link KeyBinding}s for the mod as specified by the {@link Key}
     * enum.
     */
    @Override
    public void init() {
	String name = Loader.instance().activeModContainer().getName();
	for (Key key : Key.values()) {
	    KeyBinding keyBinding = new KeyBinding(key.toString(), key.getKeyCode(), name);
	    KEY_BINDINGS.put(key, keyBinding);
	    ClientRegistry.registerKeyBinding(keyBinding);
	}
    }

    /**
     * TODO
     */
    @Override
    public void postInit() {
	List<String> mods = new ArrayList<>();
	Loader.instance().getActiveModList().forEach(mod -> {
	    if (!mod.getModId().equals(Circuitry.MODID) && mod.getSharedModDescriptor() != null)
		mods.add(mod.getName());
	});
	if (!mods.isEmpty()) {
	    String message = "Extra mod(s) found. Please remove before proceeding:";
	    throw new CustomModLoadingErrorDisplayException(message + " " + mods, null) {
		/**
		 * TODO
		 */
		@Override
		public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX,
			int mouseRelY, float tickTime) {
		    int increment = fontRenderer.FONT_HEIGHT;
		    int offset = errorScreen.height / 4;

		    errorScreen.drawCenteredString(fontRenderer, message, errorScreen.width / 2, offset,
			    Color.WHITE.getRGB());
		    offset += increment / 2;
		    for (String mod : mods) {
			offset += 1.5 * increment;
			errorScreen.drawCenteredString(fontRenderer, mod, errorScreen.width / 2, offset,
				Color.WHITE.getRGB());
		    }
		}

		/**
		 * TODO
		 */
		@Override
		public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) {
		    // TODO
		}
	    };
	}
    }
}