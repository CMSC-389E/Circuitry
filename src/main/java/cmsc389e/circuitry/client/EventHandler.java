package cmsc389e.circuitry.client;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.types.JsonOps;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.network.PacketHandler;
import cmsc389e.circuitry.common.network.TagMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.AlertScreen;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OS;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.CheckResult;
import net.minecraftforge.fml.VersionChecker.Status;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.versions.forge.ForgeVersion;

@EventBusSubscriber(Dist.CLIENT)
public class EventHandler {
	@SuppressWarnings("resource")
	private static void alert(String msg1, String msg2, String msg3, String button, Consumer<OS> consumer) {
		Minecraft.getInstance()
				.displayGuiScreen(new AlertScreen(() -> consumer.accept(Util.getOSType()),
						new StringTextComponent(msg1).setStyle(new Style().setColor(TextFormatting.RED)),
						new StringTextComponent(msg2 + "\n\n").appendSibling(
								new StringTextComponent(msg3).setStyle(new Style().setColor(TextFormatting.AQUA))),
						button));
	}

	@SubscribeEvent
	@SuppressWarnings("resource")
	public static void onDrawHighlightBlock(DrawHighlightEvent.HighlightBlock event) {
		Minecraft minecraft = Minecraft.getInstance();
		TileEntity entity = minecraft.world.getTileEntity(event.getTarget().getPos());
		if (entity != null && entity.getType() == Circuitry.NODE.get())
			PacketHandler.CHANNEL.sendToServer(new TagMessage(entity.getPos()));
		else
			minecraft.ingameGUI.setOverlayMessage("", false);
	}

	@SubscribeEvent
	public static void onGuiScreenInitPost(GuiScreenEvent.InitGuiEvent.Post event) throws IllegalAccessException {
		Screen screen = event.getGui();
		if (screen instanceof CreateWorldScreen && ObfuscationReflectionHelper
				.findField(CreateWorldScreen.class, "field_146329_I").get(screen).equals("")) // worldSeed
			((CreateWorldScreen) screen).recreateFromExistingWorld(
					new WorldInfo(new WorldSettings(0, GameType.CREATIVE, false, false, WorldType.FLAT).enableCommands()
							.setGeneratorOptions(FlatGenerationSettings.createFlatGeneratorFromString(
									"minecraft:bedrock,3*minecraft:stone,52*minecraft:sandstone;minecraft:desert")
									.func_210834_a(JsonOps.INSTANCE).getValue()),
							I18n.format("selectWorld.newWorld")));
		else if (event.getGui() instanceof MainMenuScreen) {
			ModList list = ModList.get();

			Set<String> allowed = ImmutableSet.of(Circuitry.MODID, ForgeVersion.MOD_ID, "minecraft");
			Object[] mods = list.applyForEachModContainer(ModContainer::getModInfo).parallel()
					.filter(info -> !allowed.contains(info.getModId())).map(IModInfo::getDisplayName).toArray();
			if (mods.length != 0)
				alert("Illegal mods are installed.", "You must delete the following mods before proceeding:",
						Arrays.toString(mods), "fml.button.open.mods.folder",
						os -> os.openFile(FMLPaths.MODSDIR.get().toFile()));

			IModInfo mod = list.getModContainerById(Circuitry.MODID).get().getModInfo();
			if (!mod.getVersion().getQualifier().equals("NONE")) {
				CheckResult result = VersionChecker.getResult(mod);
				if (result.status == Status.OUTDATED)
					alert(mod.getDisplayName() + " is out of date.",
							"You must update to version " + result.target
									+ " before proceeding.\nOpen Link will bring you to the below page:",
							result.url, "Open Link", os -> os.openURI(result.url));
			}
		}
	}
}
