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
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class SetCommand {
	@SuppressWarnings("resource")
	private static int execute(CommandContext<CommandSource> context, @Nullable String tag) {
		boolean powered = BoolArgumentType.getBool(context, "Powered");
		CommandSource source = context.getSource();
		World world = source.getEntity().getEntityWorld();

		Block block = Circuitry.IN_NODE.get();
		TileEntityType<?> type = Circuitry.TYPE.get();
		world.loadedTileEntityList.forEach(te -> {
			if (te.getType() == type && (tag == null || ((NodeTileEntity) te).getTag().equals(tag))) {
				BlockState state = te.getBlockState();
				if (state.getBlock() == block)
					NodeBlock.setPowered(world, state, te.getPos(), powered);
			}
		});
		source.sendFeedback(new StringTextComponent((tag == null ? "All In Nodes" : "In Nodes with tag " + tag)
				+ " are now " + (powered ? "powered" : "unpowered") + '.'), true);
		return 0;
	}

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("set")
				.then(Commands.argument("Powered", BoolArgumentType.bool()).executes(context -> execute(context, null))
						.then(Commands.argument("Tag", StringArgumentType.word())
								.executes(context -> execute(context, StringArgumentType.getString(context, "Tag"))))));
	}
}