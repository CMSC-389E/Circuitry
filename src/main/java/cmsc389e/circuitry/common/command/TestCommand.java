package cmsc389e.circuitry.common.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import cmsc389e.circuitry.common.Config;
import cmsc389e.circuitry.common.Config.Key;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class TestCommand {
	private static String[] inTags, outTags;
	private static boolean[][] tests;

	private static int execute(CommandContext<CommandSource> context) {
		try {
			readTests();
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommandException(
					new StringTextComponent("Unable to read " + Config.get(Key.TESTS) + ". Try running /load again."));
		}
		return 0;
	}

	public static LiteralArgumentBuilder<CommandSource> getCommand() {
		return Commands.literal("test").executes(TestCommand::execute);
	}

	private static void readTests() throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(Config.getString(Key.TESTS)));
		String[] tags = lines.get(1).split("\t(?=o)", 2);
		inTags = tags[0].split("\t");
		outTags = tags[1].split("\t");

		tests = new boolean[lines.size() - 2][inTags.length + outTags.length];
		for (int i = 2; i < lines.size(); i++) {
			tags = lines.get(i).split("\t");
			for (int j = 0; j < tags.length; j++)
				tests[i - 2][j] = tags[j].equals("1");
		}
	}
}
