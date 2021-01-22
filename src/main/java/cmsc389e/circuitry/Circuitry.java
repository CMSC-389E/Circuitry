package cmsc389e.circuitry;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

import cmsc389e.circuitry.common.Config;
import cmsc389e.circuitry.common.NodeTileEntity;
import cmsc389e.circuitry.common.block.InNodeBlock;
import cmsc389e.circuitry.common.block.OutNodeBlock;
import cmsc389e.circuitry.common.network.KeyPressedMessage.Key;
import cmsc389e.circuitry.common.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.server.dedicated.PropertyManager;
import net.minecraft.server.dedicated.ServerProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.tileentity.TileEntityType.Builder;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(bus = Bus.MOD)
@Mod(Circuitry.MODID)
public class Circuitry {
	public static final String MODID = "circuitry";
	public static RegistryObject<Block> inNode, outNode;
	public static RegistryObject<TileEntityType<?>> tileEntity;

	@SubscribeEvent
	@SuppressWarnings("resource")
	public static void onClientSetup(FMLClientSetupEvent event) {
		ObfuscationReflectionHelper.setPrivateValue(NewChatGui.class,
				event.getMinecraftSupplier().get().ingameGUI.getChatGUI(), new ArrayList<ChatLine>() {
					private boolean frozen;

					@Override
					public ChatLine remove(int index) {
						frozen = true;
						return get(index);
					}

					@Override
					public int size() {
						int size = frozen ? 0 : super.size();
						frozen = false;
						return size;
					}
				}, "field_146253_i"); // drawnChatLines

		Key.register();
	}

	@SubscribeEvent
	@SuppressWarnings("resource")
	public static void onDedicatedServerSetup(FMLDedicatedServerSetupEvent event) {
		ServerProperties properties = event.getServerSupplier().get().getServerProperties();
		Properties serverProperties = ObfuscationReflectionHelper.getPrivateValue(PropertyManager.class, properties,
				"field_73672_b"); // serverProperties
		String falseValue = Boolean.FALSE.toString();
		if (!falseValue.equals(serverProperties.put("reset-props", falseValue))) {
			serverProperties.put("difficulty", Difficulty.PEACEFUL.getTranslationKey());
			serverProperties.put("gamemode", GameType.CREATIVE.getName());
			serverProperties.put("generate-structures", falseValue);
			serverProperties.put("level-type", WorldType.FLAT.getName());
			serverProperties.put("spawn-animals", falseValue);
		}
		properties.save(Paths.get("server.properties"));
	}

	public Circuitry() {
		Item.Properties properties = new Item.Properties().group(ItemGroup.REDSTONE);

		DeferredRegister<Block> blocks = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
		DeferredRegister<Item> items = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
		DeferredRegister<TileEntityType<?>> types = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, MODID);

		inNode = blocks.register("in_node", InNodeBlock::new);
		outNode = blocks.register("out_node", OutNodeBlock::new);
		tileEntity = types.register("node",
				() -> Builder.create(NodeTileEntity::new, inNode.get(), outNode.get()).build(null));
		blocks.getEntries().forEach(
				block -> items.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties)));

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		blocks.register(bus);
		items.register(bus);
		types.register(bus);

		Config.register();
		PacketHandler.register();
	}
}
