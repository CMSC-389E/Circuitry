package cmsc389e.circuitry.common.command.test;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class TestCommand {
	public static LiteralArgumentBuilder<CommandSource> getCommand() {
		return Commands.literal("test").then(StopCommand.getCommand()).then(LoadCommand.getCommand())
				.then(StartCommand.getCommand()).then(SubmitCommand.getCommand());
	}
}
