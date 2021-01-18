package cmsc389e.circuitry.common.network;

import org.codehaus.plexus.util.StringUtils;
import org.lwjgl.glfw.GLFW;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.NodeTileEntity;
import cmsc389e.circuitry.common.block.NodeBlock;
import cmsc389e.circuitry.common.network.PacketHandler.Message;
import net.minecraft.block.BlockState;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class KeyMessage extends Message {
	public enum Key {
		DECREASE_TAG("Decrease Tag", GLFW.GLFW_KEY_G), TOGGLE_NODE("Toggle Node", GLFW.GLFW_KEY_R);

		public static void register() {
			String category = StringUtils.capitalise(Circuitry.MODID);
			for (Key key : values()) {
				key.binding = new KeyBinding(key.description, key.keyCode, category);
				ClientRegistry.registerKeyBinding(key.binding);
			}
		}

		private final String description;
		private final int keyCode;
		public KeyBinding binding;

		Key(String description, int keyCode) {
			this.description = description;
			this.keyCode = keyCode;
		}
	}

	private final Key key;
	private final BlockPos pos;

	public KeyMessage(Key key, BlockPos pos) {
		this.key = key;
		this.pos = pos;
	}

	public KeyMessage(PacketBuffer buffer) {
		this(buffer.readEnumValue(Key.class), buffer.readBlockPos());
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeEnumValue(key).writeBlockPos(pos);
	}

	@Override
	public void handle(Context context) {
		World world = context.getSender().world;
		switch (key) {
		case DECREASE_TAG:
			BlockState state = world.getBlockState(pos);
			if (state.getBlock() == Circuitry.inNodeBlock.get())
				NodeBlock.setPowered(world, state, pos, !state.get(NodeBlock.POWERED));
			break;
		case TOGGLE_NODE:
			TileEntity entity = world.getTileEntity(pos);
			if (entity != null && entity.getType() == Circuitry.nodeTileEntity.get()) {
				((NodeTileEntity) entity).index--;
				entity.markDirty();
			}
		}
	}
}
