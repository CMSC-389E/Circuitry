package cmsc389e.circuitry.common.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;
import org.codehaus.plexus.util.StringUtils;

import cmsc389e.circuitry.common.block.BlockNode;
import cmsc389e.circuitry.common.world.CircuitryWorldSavedData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public abstract class CommandCircuitryBase extends CommandBase {
    protected static final String SUBMIT = "submit" + File.separatorChar + "submit.jar", TESTS = "tests.txt";
    @Nullable
    protected static final List<String[]> TEST_LINES = new ArrayList<>();

    protected static final void resetInputs(World world) {
	CircuitryWorldSavedData.get(world)
		.forEach(pos -> BlockNode.setPowered(world, pos, world.getBlockState(pos), false));
    }

    protected static final void sendMessage(ICommandSender sender, String message) {
	sendMessage(sender, message, new Style().getColor());
    }

    protected static final void sendMessage(ICommandSender sender, String message, TextFormatting color) {
	sender.sendMessage(new TextComponentString(message).setStyle(new Style().setItalic(true).setColor(color)));
    }

    protected static final void tryReadTestsFile() throws CommandException {
	// Load in valid tags and rows for the current test
	TEST_LINES.clear();
	try (BufferedReader in = Files.newBufferedReader(Paths.get(TESTS))) {
	    String line;
	    while ((line = in.readLine()) != null)
		TEST_LINES.add(line.split("\t"));
	} catch (IOException e) {
	    throw new CommandException("Unable to read " + TESTS + ". Try running /load again.");
	}
    }

    private final Map<String, Object> argMap;
    private final Triple<String, Boolean, Class<?>>[] argTriples;
    private final String name;

    @SafeVarargs
    protected CommandCircuitryBase(String name, Triple<String, Boolean, Class<?>>... args) {
	argMap = new HashMap<>();
	argTriples = args;
	this.name = name;
    }

    @Override
    public final void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
	if (args.length > argTriples.length)
	    throw new CommandException(getUsage(sender));
	for (int i = 0; i < args.length; i++) {
	    boolean isNumeric = StringUtils.isNumeric(args[i]);
	    for (int j = i; j < argTriples.length; j++)
		if (isNumeric == (argTriples[j].getRight() == int.class)) {
		    argMap.put(argTriples[j].getLeft(), isNumeric ? parseInt(args[i]) : args[i]);
		    break;
		}
	}

	execute(server.getEntityWorld(), sender, args);
    }

    protected abstract void execute(World world, ICommandSender sender, String[] args) throws CommandException;

    @Override
    public final String getName() {
	return name;
    }

    @SuppressWarnings("unchecked")
    protected final <T> T getOrDefault(String key, T defaultValue) throws CommandException {
	if (argMap.containsKey(key))
	    return (T) argMap.get(key);
	for (Triple<String, Boolean, Class<?>> argTriple : argTriples)
	    if (key.equals(argTriple.getLeft())) {
		if (argTriple.getMiddle())
		    throw new CommandException(key + " is a required argument.");
		return defaultValue;
	    }
	throw new IllegalArgumentException(getClass().getSimpleName() + " did not specify an argument by the name of '"
		+ key + "' in it's constructor.");
    }

    @Override
    public final String getUsage(ICommandSender sender) {
	StringBuilder usage = new StringBuilder('/' + getName());
	for (Triple<String, Boolean, Class<?>> argTriple : argTriples) {
	    String boundary = argTriple.getMiddle() ? "<>" : "[]";
	    usage.append(" " + boundary.charAt(0) + argTriple.getLeft() + boundary.charAt(1));
	}
	return usage.toString();
    }
}