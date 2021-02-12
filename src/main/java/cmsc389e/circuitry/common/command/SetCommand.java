package cmsc389e.circuitry.common.command;

import javax.annotation.Nullable;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.NodeTileEntity;
import cmsc389e.circuitry.common.block.NodeBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public final class SetCommand {
	@SuppressWarnings("resource")
	private static int execute(final CommandContext<CommandSource> context, final boolean powered,
			@Nullable final String tag) {
		final CommandSource source = context.getSource();
		final Block block = Circuitry.inNode.get();
		final World world = source.getWorld();
		NodeTileEntity.forEach(world, entity -> {
			if (tag == null || entity.tag.equals(tag)) {
				final BlockState state = entity.getBlockState();
				if (state.getBlock() == block)
					NodeBlock.setPowered(world, state, entity.getPos(), powered);
			}
		});
		source.sendFeedback(new StringTextComponent((tag == null ? "All In Nodes" : "In Nodes with tag " + tag)
				+ " are now " + (powered ? "powered" : "unpowered") + '.'), true);
		return 0;
	}

	public static void register(final CommandDispatcher<CommandSource> dispatcher) {
		final String powered = "Powered";
		final String tag = "Tag";

		dispatcher.register(Commands.literal("set").requires(context -> context.hasPermissionLevel(4))
				.then(Commands.argument(powered, BoolArgumentType.bool())
						.executes(context -> execute(context, BoolArgumentType.getBool(context, powered), null))
						.then(Commands.argument(tag, StringArgumentType.word())
								.executes(context -> execute(context, BoolArgumentType.getBool(context, powered),
										StringArgumentType.getString(context, tag))))));
	}
}
