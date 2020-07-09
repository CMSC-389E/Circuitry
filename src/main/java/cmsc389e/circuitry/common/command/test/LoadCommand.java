package cmsc389e.circuitry.common.command.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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
	private static void exception(Exception e, String msg) {
		e.printStackTrace();
		throw new CommandException(new StringTextComponent(msg));
	}

	private static int execute(CommandContext<CommandSource> context) {
		String spec = String.format(Config.testsURL, IntegerArgumentType.getInteger(context, "Project Number"));
		try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(spec).openStream()))) {
			in.readLine();
			String[] tags = in.readLine().split("\t(?=o)", 2);

			Config.inTags = Arrays.asList(tags[0].split("\t"));
			Config.outTags = Arrays.asList(tags[1].split("\t"));
			Config.inTests.clear();
			Config.outTests.clear();
			String line;
			while ((line = in.readLine()) != null) {
				List<Boolean> inTest = new ArrayList<>();
				List<Boolean> outTest = new ArrayList<>();
				Config.inTests.add(inTest);
				Config.outTests.add(outTest);
				tags = line.split("\t");
				for (int j = 0; j < tags.length; j++)
					(j < Config.inTags.size() ? inTest : outTest).add(tags[j].equals("1"));
			}
			Config.sync();

			context.getSource().sendFeedback(new StringTextComponent("The project has been loaded successfully."),
					true);
		} catch (IndexOutOfBoundsException e) {
			exception(e, "The file at " + spec + " was malformed!");
		} catch (FileNotFoundException e) {
			exception(e, "No file found at " + spec + '!');
		} catch (MalformedURLException e) {
			exception(e, spec + " is not a valid URL!");
		} catch (IOException e) {
			exception(e, "Unable to read tests! Try running load again.");
		}
		return 0;
	}

	public static LiteralArgumentBuilder<CommandSource> getCommand() {
		return Commands.literal("load").then(
				Commands.argument("Project Number", IntegerArgumentType.integer(0)).executes(LoadCommand::execute));
	}
}