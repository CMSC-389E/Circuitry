package cmsc389e.circuitry.common.network;

import org.lwjgl.input.Keyboard;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * TODO
 */
public class CircuitryMessage implements IMessage {
    /**
     * TODO
     */
    public enum Key {
	DECREASE_TAG("Decrease Tag Modifier", Keyboard.KEY_LMENU) {
	    /**
	     * TODO
	     */
	    @Override
	    public boolean test(EntityPlayer player, boolean pressed, boolean down) {
		return pressed || CircuitryPacketHandler.isPlayerHoldingModifier(player) != down;
	    }
	},
	TOGGLE_NODE("Toggle Node", Keyboard.KEY_R) {
	    /**
	     * TODO
	     */
	    @Override
	    public boolean test(EntityPlayer player, boolean pressed, boolean down) {
		return pressed;
	    }
	};

	private final String description;
	private final int keyCode;

	/**
	 * TODO
	 *
	 * @param description TODO
	 * @param keyCode     TODO
	 */
	private Key(String description, int keyCode) {
	    this.description = description;
	    this.keyCode = keyCode;
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public int getKeyCode() {
	    return keyCode;
	}

	/**
	 * TODO
	 *
	 * @param player  TODO
	 * @param pressed TODO
	 * @param down    TODO
	 * @return TODO
	 */
	public abstract boolean test(EntityPlayer player, boolean pressed, boolean down);

	/**
	 * TODO
	 */
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

    /**
     * TODO
     *
     * @param key TODO
     */
    public CircuitryMessage(Key key) {
	this.key = key;
    }

    /**
     * TODO
     */
    @Override
    public void fromBytes(ByteBuf buf) {
	key = Key.values()[buf.readInt()];
    }

    /**
     * TODO
     *
     * @return TODO
     */
    public Key getKey() {
	return key;
    }

    /**
     * TODO
     */
    @Override
    public void toBytes(ByteBuf buf) {
	buf.writeInt(key.ordinal());
    }
}
