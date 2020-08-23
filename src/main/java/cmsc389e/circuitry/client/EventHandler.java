package cmsc389e.circuitry.client;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableSet;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.NodeTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.AlertScreen;
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
    private static void alert(GuiOpenEvent event, String line1, String line2, String line3, String button,
	    Consumer<OS> consumer) {
	event.setGui(
		new AlertScreen(() -> consumer.accept(Util.getOSType()),
			new StringTextComponent(line1).setStyle(new Style().setColor(TextFormatting.RED)),
			new StringTextComponent(line2 + "\n\n").appendSibling(
				new StringTextComponent(line3).setStyle(new Style().setColor(TextFormatting.AQUA))),
			button));
    }

    @SubscribeEvent
    public static void onGuiOpen(GuiOpenEvent event) {
	if (event.getGui() instanceof MainMenuScreen) {
	    ModList list = ModList.get();

	    Set<String> allowed = ImmutableSet.of(Circuitry.MODID, ForgeVersion.MOD_ID, "minecraft");
	    Object[] mods = list.applyForEachModContainer(ModContainer::getModInfo)
		    .filter(modInfo -> !allowed.contains(modInfo.getModId())).map(IModInfo::getDisplayName).toArray();
	    if (mods.length != 0)
		alert(event, "Illegal mods are installed.", "You must delete the following mods before proceeding:",
			Arrays.toString(mods), "fml.button.open.mods.folder",
			os -> os.openFile(FMLPaths.MODSDIR.get().toFile()));

	    IModInfo info = list.getModContainerById(Circuitry.MODID).get().getModInfo();
	    if (!info.getVersion().getQualifier().equals("NONE")) {
		CheckResult result = VersionChecker.getResult(info);
		if (result.status == Status.OUTDATED)
		    alert(event, info.getDisplayName() + " is out of date.",
			    "You must update to version " + result.target
				    + " before proceeding.\nOpen Link will bring you to the page below:",
			    result.url, "Open Link", os -> os.openURI(result.url));
	    }
	}
    }

    @SubscribeEvent
    public static void onHighlightBlock(HighlightBlock event) {
	@SuppressWarnings("resource")
	Minecraft minecraft = Minecraft.getInstance();
	TileEntity te = minecraft.world.getTileEntity(event.getTarget().getPos());
	minecraft.ingameGUI.setOverlayMessage(
		te != null && te.getType() == Circuitry.TYPE.get() ? ((NodeTileEntity) te).getTag() : "", false);
    }
}
