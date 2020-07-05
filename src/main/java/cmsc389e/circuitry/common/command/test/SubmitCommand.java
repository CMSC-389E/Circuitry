package cmsc389e.circuitry.common.command.test;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class SubmitCommand {
	public static int execute(CommandContext<CommandSource> source) {
		return 0;
	}

	public static LiteralArgumentBuilder<CommandSource> getCommand() {
		return Commands.literal("submit").executes(SubmitCommand::execute);
	}
}