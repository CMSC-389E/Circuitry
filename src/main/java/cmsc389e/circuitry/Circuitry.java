package cmsc389e.circuitry;

import java.util.ArrayList;

import cmsc389e.circuitry.common.Config;
import cmsc389e.circuitry.common.NodeTileEntity;
import cmsc389e.circuitry.common.block.InNodeBlock;
import cmsc389e.circuitry.common.block.OutNodeBlock;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.tileentity.TileEntityType.Builder;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod(Circuitry.MODID)
@EventBusSubscriber(bus = Bus.MOD)
public class Circuitry {
	public static final String MODID = "circuitry";
	public static Block inNodeBlock, outNodeBlock;
	public static TileEntityType<?> nodeType;

	@SuppressWarnings("resource")
	@SubscribeEvent
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
				}, "drawnChatLines");
	}

	@SubscribeEvent
	public static void onRegisterBlocks(Register<Block> event) {
		event.getRegistry().registerAll(inNodeBlock = new InNodeBlock(), outNodeBlock = new OutNodeBlock());
	}

	@SubscribeEvent
	public static void onRegisterItems(Register<Item> event) {
		registerItems(event.getRegistry(), inNodeBlock, outNodeBlock);
	}

	@SubscribeEvent
	public static void onRegisterTETypes(Register<TileEntityType<?>> event) {
		event.getRegistry().register(nodeType = Builder.create(NodeTileEntity::new, inNodeBlock, outNodeBlock)
				.build(null).setRegistryName("node"));
	}

	private static void registerItems(IForgeRegistry<Item> registry, Block... blocks) {
		Properties properties = new Properties().group(ItemGroup.REDSTONE);
		for (Block block : blocks)
			registry.register(new BlockItem(block, properties).setRegistryName(block.getRegistryName()));
	}

	public Circuitry() {
		Config.register();
	}
}