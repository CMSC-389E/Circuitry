package cmsc389e.circuitry;

import cmsc389e.circuitry.common.Config;
import cmsc389e.circuitry.common.NodeTileEntity;
import cmsc389e.circuitry.common.block.InNodeBlock;
import cmsc389e.circuitry.common.block.NodeBlock;
import cmsc389e.circuitry.common.block.OutNodeBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.tileentity.TileEntityType.Builder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(Circuitry.MODID)
public class Circuitry {
	public static final String MODID = "circuitry";

	private static final DeferredRegister<Block> blocks = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
	private static final DeferredRegister<Item> items = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
	private static final DeferredRegister<TileEntityType<?>> types = new DeferredRegister<>(
			ForgeRegistries.TILE_ENTITIES, MODID);

	public static final RegistryObject<NodeBlock> IN_NODE_BLOCK = blocks.register("in_node", InNodeBlock::new),
			OUT_NODE_BLOCK = blocks.register("out_node", OutNodeBlock::new);
	public static final RegistryObject<Item> IN_NODE_ITEM = items.register("in_node",
			() -> IN_NODE_BLOCK.get().createItem()),
			OUT_NODE_ITEM = items.register("out_node", () -> OUT_NODE_BLOCK.get().createItem());
	public static final RegistryObject<TileEntityType<?>> NODE_TYPE = types.register("node",
			() -> Builder.create(NodeTileEntity::new, IN_NODE_BLOCK.get(), OUT_NODE_BLOCK.get()).build(null));

	public Circuitry() {
		Config.register();

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		blocks.register(bus);
		items.register(bus);
		types.register(bus);
	}
}