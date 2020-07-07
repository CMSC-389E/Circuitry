package cmsc389e.circuitry.client;

import java.util.Arrays;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.NodeTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.AlertScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OS;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.DrawHighlightEvent.HighlightBlock;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.CheckResult;
import net.minecraftforge.fml.VersionChecker.Status;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.versions.forge.ForgeVersion;

@EventBusSubscriber(Dist.CLIENT)
public class EventHandler {
	private static void checkMods(ModList list, GuiOpenEvent event, Runnable openMods) {
		Set<String> allowed = ImmutableSet.of(Circuitry.MODID, ForgeVersion.MOD_ID, "minecraft");
		Object[] mods = list.applyForEachModContainer(ModContainer::getModInfo)
				.filter(info -> !allowed.contains(info.getModId())).map(IModInfo::getDisplayName).toArray();
		if (mods.length != 0)
			event.setGui(new AlertScreen(openMods,
					new StringTextComponent("Illegal mods are installed.")
							.setStyle(new Style().setColor(TextFormatting.RED)),
					new StringTextComponent("You must delete the following mods before proceeding:\n\n")
							.appendSibling(new StringTextComponent(Arrays.toString(mods))
									.setStyle(new Style().setColor(TextFormatting.AQUA))),
					"fml.button.open.mods.folder"));
	}

	private static void checkVersion(ModList list, GuiOpenEvent event, OS os, Runnable openMods) {
		IModInfo info = list.getModContainerById(Circuitry.MODID).get().getModInfo();
		if (!info.getVersion().getQualifier().equals("NONE")) {
			CheckResult result = VersionChecker.getResult(info);
			if (result.status == Status.OUTDATED)
				event.setGui(new ConfirmScreen(t -> {
					if (t)
						os.openURI(result.url);
					else
						openMods.run();
				}, new StringTextComponent(info.getDisplayName() + " is out of date.")
						.setStyle(new Style().setColor(TextFormatting.RED)),
						new StringTextComponent("You must update to version " + result.target
								+ " before proceeding.\nOpen Link will bring you to the page below:\n\n")
										.appendSibling(new StringTextComponent(result.url)
												.setStyle(new Style().setColor(TextFormatting.AQUA))),
						"Open Link", "Open Mods Folder"));
		}
	}

	@SubscribeEvent
	public static void onGuiOpen(GuiOpenEvent event) {
		if (event.getGui() instanceof MainMenuScreen) {
			ModList list = ModList.get();
			OS os = Util.getOSType();
			Runnable openMods = () -> os.openFile(FMLPaths.MODSDIR.get().toFile());

			checkVersion(list, event, os, openMods);
			checkMods(list, event, openMods);
		}
	}

	@SubscribeEvent
	public static void onHighlightBlock(HighlightBlock event) {
		@SuppressWarnings("resource")
		Minecraft minecraft = Minecraft.getInstance();
		TileEntity te = minecraft.world.getTileEntity(event.getTarget().getPos());
		minecraft.ingameGUI.setOverlayMessage(
				te != null && te.getType() == Circuitry.NODE_TYPE.get() ? ((NodeTileEntity) te).getTag() : "", false);
	}
}
