package cmsc389e.circuitry.common.network;

import java.util.function.Function;
import java.util.function.Supplier;

import com.google.common.base.Predicates;

import cmsc389e.circuitry.Circuitry;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
	public static abstract class Message {
		private boolean consume(Supplier<Context> supplier) {
			Context context = supplier.get();
			context.enqueueWork(() -> handle(context));
			return true;
		}

		public abstract void encode(PacketBuffer buffer);

		public abstract void handle(Context context);
	}

	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(Circuitry.MODID),
			() -> "", Predicates.alwaysTrue(), Predicates.alwaysTrue());
	private static int id = 0;

	public static void register() {
		register(KeyPressedMessage.class, KeyPressedMessage::new);
	}

	public static <T extends Message> void register(Class<T> type, Function<PacketBuffer, T> decoder) {
		CHANNEL.messageBuilder(type, id++).consumer(Message::consume).decoder(decoder).encoder(Message::encode).add();
	}
}
