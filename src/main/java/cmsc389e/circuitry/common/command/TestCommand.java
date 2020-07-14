package cmsc389e.circuitry.common.command;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import cmsc389e.circuitry.common.Config;
import cmsc389e.circuitry.common.Tester;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

public class TestCommand {
	private static void exception(@Nullable Exception e, String message) {
		if (e != null)
			e.printStackTrace();
		throw new CommandException(new StringTextComponent(message));
	}

	private static <T> T getOrDefault(CommandContext<CommandSource> context, String name, Class<T> clazz,
			T defaultValue) {
		try {
			return context.getArgument(name, clazz);
		} catch (@SuppressWarnings("unused") IllegalArgumentException e) {
			return defaultValue;
		}
	}

	private static int load(CommandContext<CommandSource> context) {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(
				String.format(Config.TESTS_URL.get(), IntegerArgumentType.getInteger(context, "Project Number")))
						.openStream()))) {
			in.readLine();
			String[] tags = in.readLine().split("\t(?=o)", 2);

			int inSize = Config.IN_TAGS.set(Arrays.asList(tags[0].split("\t"))).size();
			Config.OUT_TAGS.set(Arrays.asList(tags[1].split("\t")));
			List<List<Boolean>> inTests = Config.IN_TESTS.set(new ArrayList<>());
			List<List<Boolean>> outTests = Config.OUT_TESTS.set(new ArrayList<>());
			String line;
			while ((line = in.readLine()) != null) {
				List<Boolean> inTest = new ArrayList<>();
				List<Boolean> outTest = new ArrayList<>();
				inTests.add(inTest);
				outTests.add(outTest);
				tags = line.split("\t");
				for (int j = 0; j < tags.length; j++)
					(j < inSize ? inTest : outTest).add(tags[j].equals("1"));
			}

			context.getSource().sendFeedback(new StringTextComponent("The project has been loaded successfully."),
					true);
		} catch (IndexOutOfBoundsException e) {
			exception(e, "The tests file was malformed!");
		} catch (FileNotFoundException e) {
			exception(e, "No tests file found at " + e.getLocalizedMessage() + '!');
		} catch (MalformedURLException e) {
			exception(e, e.getLocalizedMessage() + " is not a valid URL!");
		} catch (IOException e) {
			exception(e, "Unable to read tests! Try running load again.");
		}
		return 0;
	}

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		ArgumentBuilder<CommandSource, ?> load = Commands.literal("load")
				.then(Commands.argument("Project Number", IntegerArgumentType.integer(0)).executes(TestCommand::load));
		ArgumentBuilder<CommandSource, ?> start = Commands.literal("start")
				.then(Commands.argument("Delay", IntegerArgumentType.integer(0))).executes(TestCommand::start);
		ArgumentBuilder<CommandSource, ?> stop = Commands.literal("stop").executes(TestCommand::stop);
		ArgumentBuilder<CommandSource, ?> submit = Commands.literal("submit").executes(TestCommand::submit);

		dispatcher.register(Commands.literal("test").then(load).then(start).then(stop).then(submit));
	}

	@SuppressWarnings("resource")
	public static int start(CommandContext<CommandSource> context) {
		int delay = getOrDefault(context, "Delay", int.class, 0);
		CommandSource source = context.getSource();
		World world = source.getWorld();
		if (Tester.INSTANCES.containsKey(world))
			exception(null, "A Tester is already running!");
		Tester.INSTANCES.put(world, new Tester(source, world, delay));
		return 0;
	}

	@SuppressWarnings("resource")
	private static int stop(CommandContext<CommandSource> context) {
		CommandSource source = context.getSource();
		World world = source.getWorld();
		if (Tester.INSTANCES.remove(world) == null)
			exception(null, "No Tester is currently running!");
		source.sendFeedback(new StringTextComponent("Tester stopped successfully."), true);
		return 0;
	}

	private static int submit(CommandContext<CommandSource> context) {
		return 0;
	}
}
