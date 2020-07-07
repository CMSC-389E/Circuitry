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

	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
	public static final DeferredRegister<TileEntityType<?>> TYPES = new DeferredRegister<>(
			ForgeRegistries.TILE_ENTITIES, MODID);

	public static final RegistryObject<NodeBlock> IN_NODE_BLOCK = BLOCKS.register("in_node", InNodeBlock::new),
			OUT_NODE_BLOCK = BLOCKS.register("out_node", OutNodeBlock::new);
	public static final RegistryObject<TileEntityType<?>> NODE_TYPE = TYPES.register("node",
			() -> Builder.create(NodeTileEntity::new, IN_NODE_BLOCK.get(), OUT_NODE_BLOCK.get()).build(null));

	public Circuitry() {
		Config.register();

		ITEMS.register("in_node", () -> IN_NODE_BLOCK.get().createItem());
		ITEMS.register("out_node", () -> OUT_NODE_BLOCK.get().createItem());

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		BLOCKS.register(bus);
		ITEMS.register(bus);
		TYPES.register(bus);
	}
}