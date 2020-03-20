package cmsc389e.circuitry.common.network;

import org.lwjgl.input.Keyboard;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CircuitryMessage implements IMessage {
    public enum Key {
	DECREASE_TAG("Decrease Tag Modifier", Keyboard.KEY_LMENU) {
	    @Override
	    public boolean test(EntityPlayer player, boolean pressed, boolean down) {
		return pressed || CircuitryPacketHandler.isPlayerHoldingModifier(player) != down;
	    }
	},
	TOGGLE_NODE("Toggle Node", Keyboard.KEY_R) {
	    @Override
	    public boolean test(EntityPlayer player, boolean pressed, boolean down) {
		return pressed;
	    }
	};

	private final String description;
	private final int keyCode;

	private Key(String description, int keyCode) {
	    this.description = description;
	    this.keyCode = keyCode;
	}

	public int getKeyCode() {
	    return keyCode;
	}

	public abstract boolean test(EntityPlayer player, boolean pressed, boolean down);

	@Override
	public String toString() {
	    return description;
	}
    }

    private Key key;

    /**
     * A default constructor is always required
     */
    public CircuitryMessage() {
    }

    public CircuitryMessage(Key key) {
	this.key = key;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
	key = Key.values()[buf.readInt()];
    }

    public Key getKey() {
	return key;
    }

    @Override
    public void toBytes(ByteBuf buf) {
	buf.writeInt(key.ordinal());
    }
}
