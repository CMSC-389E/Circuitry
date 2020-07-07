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
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class LoadCommand {
	private static int execute(CommandContext<CommandSource> context) {
		int projectNumber = IntegerArgumentType.getInteger(context, "Project Number");
		try (BufferedReader in = new BufferedReader(
				new InputStreamReader(new URL(String.format(Config.TEST_URL.get(), projectNumber)).openStream()))) {
			in.readLine();
			String[] tags = in.readLine().split("\t(?=o)", 2);

			List<String> inTags = Arrays.asList(tags[0].split("\t"));
			int size = inTags.size();
			Config.IN_TAGS.set(inTags);
			Config.OUT_TAGS.set(Arrays.asList(tags[1].split("\t")));

			List<List<Boolean>> inTests = new ArrayList<>();
			List<List<Boolean>> outTests = new ArrayList<>();
			String line;
			while ((line = in.readLine()) != null) {
				List<Boolean> inTest = new ArrayList<>();
				List<Boolean> outTest = new ArrayList<>();
				inTests.add(inTest);
				outTests.add(outTest);
				tags = line.split("\t");
				for (int i = 0; i < tags.length; i++)
					(i < size ? inTest : outTest).add(tags[i].equals("1"));
			}
			Config.IN_TESTS.set(inTests);
			Config.OUT_TESTS.set(outTests);
			context.getSource().sendFeedback(
					new StringTextComponent("Project " + projectNumber + " has been loaded successfully."), true);
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommandException(new StringTextComponent("Unable to read tests! Try running load again."));
		}
		return 0;
	}

	public static LiteralArgumentBuilder<CommandSource> getCommand() {
		return Commands.literal("load").then(
				Commands.argument("Project Number", IntegerArgumentType.integer(0, 9)).executes(LoadCommand::execute));
	}
}