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

public class SetCommand {
	@SuppressWarnings("resource")
	private static int execute(CommandContext<CommandSource> context, boolean powered, @Nullable String tag) {
		CommandSource source = context.getSource();
		Block block = Circuitry.IN_NODE.get();
		NodeTileEntity.forEach(source.getServer(), te -> {
			if (tag == null || te.getTag().equals(tag)) {
				BlockState state = te.getBlockState();
				if (state.getBlock() == block)
					NodeBlock.setPowered(te.getWorld(), state, te.getPos(), powered);
			}
		});
		source.sendFeedback(new StringTextComponent((tag == null ? "All In Nodes" : "In Nodes with tag " + tag)
				+ " are now " + (powered ? "powered" : "unpowered") + '.'), true);
		return 0;
	}

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		String powered = "Powered";
		String tag = "Tag";

		dispatcher.register(Commands.literal("set")
				.then(Commands.argument(powered, BoolArgumentType.bool())
						.executes(context -> execute(context, BoolArgumentType.getBool(context, powered), null))
						.then(Commands.argument(tag, StringArgumentType.word())
								.executes(context -> execute(context, BoolArgumentType.getBool(context, powered),
										StringArgumentType.getString(context, tag))))));
	}
}