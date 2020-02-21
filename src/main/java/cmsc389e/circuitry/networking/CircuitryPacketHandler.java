package cmsc389e.circuitry.networking;

import java.util.HashSet;
import java.util.Set;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.block.BlockNode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class CircuitryPacketHandler {
    public static final Set<EntityPlayer> HOLDING_MODIFIER = new HashSet<>();
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(Circuitry.MODID);

    public static void init() {
	INSTANCE.registerMessage((message, ctx) -> {
	    // This is the player the packet was sent to the server from
	    EntityPlayerMP player = ctx.getServerHandler().player;
	    WorldServer world = player.getServerWorld();

	    // Execute the action on the main server thread by adding it as a scheduled task
	    world.addScheduledTask(() -> {
		switch (message.key) {
		case DECREASE_TAG:
		    if (!HOLDING_MODIFIER.remove(player))
			HOLDING_MODIFIER.add(player);
		    break;
		case TOGGLE_NODE:
		    BlockPos pos = BlockNode.rayTraceEyes(player);
		    if (pos != null) {
			IBlockState state = world.getBlockState(pos);
			BlockNode.setPowered(world, pos, state, !BlockNode.isPowered(state));
		    }
		    break;
		default:
		}
	    });

	    // No response packet
	    return null;
	}, CircuitryMessage.class, 0, Side.SERVER);
    }

    public static boolean isPlayerHoldingModifier(EntityPlayer player) {
	return HOLDING_MODIFIER.contains(player);
    }

    public static void sendMessage(CommonKey key) {
	INSTANCE.sendToServer(new CircuitryMessage(key));
    }
}