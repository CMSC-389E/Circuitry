package cmsc389e.circuitry.networking;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.block.BlockNode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class CircuitryPacketHandler {
    /**
     * The params of the IMessageHandler are <REQ, REPLY> This means that the first
     * param is the packet you are receiving, and the second is the packet you are
     * returning. The returned packet can be used as a "response" from a sent
     * packet.
     */
    public static class MessageHandler implements IMessageHandler<CircuitryMessage, IMessage> {
	@Override
	public IMessage onMessage(CircuitryMessage message, MessageContext ctx) {
	    // This is the player the packet was sent to the server from
	    EntityPlayerMP player = ctx.getServerHandler().player;
	    WorldServer world = player.getServerWorld();

	    // Execute the action on the main server thread by adding it as a scheduled task
	    world.addScheduledTask(() -> {
		if (message.key == CircuitryMessage.Key.TOGGLE_INPUT) {
		    BlockPos pos = BlockNode.rayTraceEyes(player);
		    if (pos != null) {
			IBlockState state = world.getBlockState(pos);
			BlockNode.setPowered(world, pos, state, !BlockNode.isPowered(state));
		    }
		}
	    });

	    // No response packet
	    return null;
	}
    }

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Circuitry.MODID);

    public static void init() {
	INSTANCE.registerMessage(MessageHandler.class, CircuitryMessage.class, 0, Side.SERVER);
    }
}