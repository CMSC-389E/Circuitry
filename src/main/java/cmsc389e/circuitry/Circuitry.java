package cmsc389e.circuitry;

import cmsc389e.circuitry.common.Config;
import cmsc389e.circuitry.common.block.InNodeBlock;
import cmsc389e.circuitry.common.block.NodeBlock;
import cmsc389e.circuitry.common.block.OutNodeBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(Circuitry.MODID)
public class Circuitry {
	public static final String MODID = "circuitry";

	private static final DeferredRegister<Block> blocks = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
	private static final DeferredRegister<Item> items = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);

	private static final RegistryObject<Block> inNodeBlock = blocks.register("in_node", InNodeBlock::new),
			outNodeBlock = blocks.register("out_node", OutNodeBlock::new);
	private static final RegistryObject<Item> inNodeItem = items.register("in_node",
			() -> ((NodeBlock) inNodeBlock.get()).createItem()),
			outNodeItem = items.register("out_node", () -> ((NodeBlock) outNodeBlock.get()).createItem());

	public Circuitry() {
		ModLoadingContext.get().registerConfig(Type.COMMON, Config.SPEC_PAIR.getRight());

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		blocks.register(bus);
		items.register(bus);
	}
}