package cmsc389e.circuitry.networking;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CircuitryMessage implements IMessage {
    public CommonKey key;

    /**
     * A default constructor is always required
     */
    public CircuitryMessage() {
    }

    public CircuitryMessage(CommonKey key) {
	this.key = key;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
	key = CommonKey.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
	buf.writeInt(key.ordinal());
    }
}
