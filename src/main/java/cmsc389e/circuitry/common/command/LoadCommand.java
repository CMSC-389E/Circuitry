package cmsc389e.circuitry.common.command;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import cmsc389e.circuitry.common.Config;
import cmsc389e.circuitry.common.Config.Key;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class LoadCommand {
	private static int execute(CommandContext<CommandSource> context) {
		try {
			int projectNumber = IntegerArgumentType.getInteger(context, "Project Number");
			FileUtils.copyURLToFile(new URL("https://cs.umd.edu/~abrassel/proj" + projectNumber + "tests.txt"),
					new File(Config.getString(Key.TESTS)));
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommandException(new StringTextComponent("Unable to read tests. Try running /load again."));
		}
		return 0;
	}

	public static LiteralArgumentBuilder<CommandSource> getCommand() {
		return Commands.literal("load").then(
				Commands.argument("Project Number", IntegerArgumentType.integer(0)).executes(LoadCommand::execute));
	}
}