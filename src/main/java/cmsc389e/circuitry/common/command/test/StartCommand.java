package cmsc389e.circuitry.common.command.test;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import cmsc389e.circuitry.common.Tester;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class StartCommand {
	@SuppressWarnings("resource")
	public static int execute(CommandContext<CommandSource> context, int delay) {
		CommandSource source = context.getSource();
		World world = source.getWorld();
		if (Tester.INSTANCES.containsKey(world))
			throw new CommandException(new StringTextComponent("A Tester is already running!"));
		Tester.INSTANCES.put(world, new Tester(source, world, delay));
		return 0;
	}

	public static LiteralArgumentBuilder<CommandSource> getCommand() {
		return Commands.literal("start").executes(context -> execute(context, 0))
				.then(Commands.argument("Delay", IntegerArgumentType.integer(0))
						.executes(context -> execute(context, IntegerArgumentType.getInteger(context, "Delay"))));
	}
}
