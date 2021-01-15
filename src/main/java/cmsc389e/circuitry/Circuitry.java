package cmsc389e.circuitry;

import java.util.ArrayList;

import cmsc389e.circuitry.common.Config;
import cmsc389e.circuitry.common.NodeTileEntity;
import cmsc389e.circuitry.common.block.InNodeBlock;
import cmsc389e.circuitry.common.block.OutNodeBlock;
import cmsc389e.circuitry.common.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.tileentity.TileEntityType.Builder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(bus = Bus.MOD)
@Mod(Circuitry.MODID)
public class Circuitry {
	public static final String MODID = "circuitry";
	public static RegistryObject<Block> IN_NODE, OUT_NODE;
	public static RegistryObject<TileEntityType<?>> NODE;

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
	}

	public Circuitry() {
		Properties properties = new Properties().group(ItemGroup.REDSTONE);

		DeferredRegister<Block> blocks = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
		DeferredRegister<Item> items = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
		DeferredRegister<TileEntityType<?>> types = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, MODID);

		IN_NODE = blocks.register("in_node", InNodeBlock::new);
		OUT_NODE = blocks.register("out_node", OutNodeBlock::new);
		blocks.getEntries().parallelStream().forEach(
				block -> items.register(block.getId().getPath(), () -> new BlockItem(block.get(), properties)));
		NODE = types.register("node",
				() -> Builder.create(NodeTileEntity::new, IN_NODE.get(), OUT_NODE.get()).build(null));

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		blocks.register(bus);
		items.register(bus);
		types.register(bus);

		Config.register();
		PacketHandler.register();
	}
}