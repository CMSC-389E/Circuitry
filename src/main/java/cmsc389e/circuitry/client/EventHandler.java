package cmsc389e.circuitry.client;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.types.JsonOps;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.network.KeyMessage;
import cmsc389e.circuitry.common.network.KeyMessage.Key;
import cmsc389e.circuitry.common.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.AlertScreen;
import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OS;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
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
	private static AlertScreen alert(String msg1, String msg2, String msg3, String button, Consumer<OS> consumer) {
		return new AlertScreen(() -> consumer.accept(Util.getOSType()),
				new StringTextComponent(msg1).setStyle(new Style().setColor(TextFormatting.RED)),
				new StringTextComponent(msg2 + "\n\n").appendSibling(
						new StringTextComponent(msg3).setStyle(new Style().setColor(TextFormatting.AQUA))),
				button);
	}

	@SubscribeEvent
	public static void onGuiOpenEvent(GuiOpenEvent event) {
		Screen screen = event.getGui();
		if (screen instanceof CreateWorldScreen)
			((CreateWorldScreen) screen).recreateFromExistingWorld(
					new WorldInfo(new WorldSettings(0, GameType.CREATIVE, false, false, WorldType.FLAT).enableCommands()
							.setGeneratorOptions(FlatGenerationSettings.createFlatGeneratorFromString(
									"minecraft:bedrock,3*minecraft:stone,52*minecraft:sandstone;minecraft:desert")
									.func_210834_a(JsonOps.INSTANCE).getValue()),
							I18n.format("selectWorld.newWorld")));
		else {
			ModList list = ModList.get();

			Set<String> allowed = ImmutableSet.of(Circuitry.MODID, ForgeVersion.MOD_ID, "minecraft");
			String mods = list.applyForEachModContainer(ModContainer::getModInfo).parallel()
					.filter(info -> !allowed.contains(info.getModId())).map(IModInfo::getDisplayName)
					.collect(Collectors.joining(", "));
			if (!mods.isEmpty())
				event.setGui(alert("Illegal mods are installed.",
						"You must delete the following mods before proceeding:", mods, "fml.button.open.mods.folder",
						os -> os.openFile(FMLPaths.MODSDIR.get().toFile())));

			IModInfo mod = list.getModContainerById(Circuitry.MODID).get().getModInfo();
			if (!mod.getVersion().getQualifier().equals("NONE")) {
				CheckResult result = VersionChecker.getResult(mod);
				if (result.status == Status.OUTDATED)
					event.setGui(alert(mod.getDisplayName() + " is out of date.",
							"You must update to version " + result.target
									+ " before proceeding.\nOpen Link will bring you to the below page:",
							result.url, "Open Link", os -> os.openURI(result.url)));
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("resource")
	public static void onTickClient(TickEvent.ClientTickEvent event) {
		if (event.phase == Phase.END) {
			Minecraft minecraft = Minecraft.getInstance();
			RayTraceResult result = minecraft.objectMouseOver;
			if (result != null && result.getType() == Type.BLOCK) {
				BlockPos pos = ((BlockRayTraceResult) result).getPos();
				for (Key key : Key.values())
					if (key.binding.isPressed())
						PacketHandler.CHANNEL.sendToServer(new KeyMessage(key, pos));
			}
		}
	}
}
