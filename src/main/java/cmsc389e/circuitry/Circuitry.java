package cmsc389e.circuitry;

import cmsc389e.circuitry.common.Config;
import cmsc389e.circuitry.common.NodeTileEntity;
import cmsc389e.circuitry.common.block.InNodeBlock;
import cmsc389e.circuitry.common.block.OutNodeBlock;
import net.minecraft.block.Block;
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
import net.minecraftforge.registries.IForgeRegistry;

@Mod(Circuitry.MODID)
@EventBusSubscriber(bus = Bus.MOD)
public class Circuitry {
	public static final String MODID = "circuitry";
	public static Block inNodeBlock, outNodeBlock;
	public static TileEntityType<?> nodeType;

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

	public Circuitry() throws IllegalAccessException {
		Config.register();
	}
}