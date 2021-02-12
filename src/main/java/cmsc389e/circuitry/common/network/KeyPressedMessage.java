package cmsc389e.circuitry.common.network;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.NodeTileEntity;
import cmsc389e.circuitry.common.block.NodeBlock;
import cmsc389e.circuitry.common.network.PacketHandler.Message;
import net.minecraft.block.BlockState;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public final class KeyPressedMessage extends Message {
	public enum Key {
		DECREASE_TAG("Decrease Tag", GLFW.GLFW_KEY_G), TOGGLE_NODE("Toggle Node", GLFW.GLFW_KEY_R);

		public static final void register() {
			final String category = StringUtils.capitalize(Circuitry.MODID);
			for (final Key key : values()) {
				key.binding = new KeyBinding(key.description, key.keyCode, category);
				ClientRegistry.registerKeyBinding(key.binding);
			}
		}

		private final String description;
		private final int keyCode;
		public KeyBinding binding;

		Key(final String description, final int keyCode) {
			this.description = description;
			this.keyCode = keyCode;
		}
	}

	private final Key key;
	private final BlockPos pos;
	private final int pressTime;

	public KeyPressedMessage(final Key key, final BlockPos pos, final int pressTime) {
		this.key = key;
		this.pos = pos;
		this.pressTime = pressTime;
	}

	public KeyPressedMessage(final PacketBuffer buffer) {
		this(buffer.readEnumValue(Key.class), buffer.readBlockPos(), buffer.readInt());
	}

	@Override
	public void encode(final PacketBuffer buffer) {
		buffer.writeEnumValue(key).writeBlockPos(pos).writeInt(pressTime);
	}

	@Override
	public void handle(final Context context) {
		final World world = context.getSender().world;
		switch (key) {
		case DECREASE_TAG:
			final NodeTileEntity entity = NodeTileEntity.get(world, pos);
			if (entity != null)
				entity.changeIndex(-pressTime);
			break;
		case TOGGLE_NODE:
			final BlockState state = world.getBlockState(pos);
			if (state.getBlock() == Circuitry.inNode.get())
				NodeBlock.setPowered(world, state, pos, !NodeBlock.isPowered(state));
		}
	}
}
