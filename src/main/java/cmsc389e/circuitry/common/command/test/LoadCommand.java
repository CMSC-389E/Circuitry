package cmsc389e.circuitry.common.command.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
			try (BufferedReader in = new BufferedReader(new InputStreamReader(
					new URL("https://cs.umd.edu/~abrassel/proj" + projectNumber + "tests.txt").openStream()))) {
				in.readLine();
				String[] tags = in.readLine().split("\t(?=o)", 2);
				Config.set(Key.IN_TAGS, Arrays.asList(tags[0].split("\t")));
				Config.set(Key.OUT_TAGS, Arrays.asList(tags[1].split("\t")));

				List<List<Boolean>> tests = new ArrayList<>();
				String line;
				while ((line = in.readLine()) != null) {
					List<Boolean> test = new ArrayList<>();
					tags = line.split("\t");
					for (String tag : tags)
						test.add(tag.equals("1"));
					tests.add(test);
				}
				Config.set(Key.TESTS, tests);
			}
			context.getSource().sendFeedback(
					new StringTextComponent("Project " + projectNumber + " has been loaded successfully."), true);
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