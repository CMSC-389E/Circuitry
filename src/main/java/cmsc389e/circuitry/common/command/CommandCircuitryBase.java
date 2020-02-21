package cmsc389e.circuitry.common.command;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public abstract class CommandCircuitryBase extends CommandBase {
    public static void sendMessage(ICommandSender sender, String msg) {
	sendMessage(sender, msg, new Style());
    }

    public static void sendMessage(ICommandSender sender, String msg, Style style) {
	sender.sendMessage(new TextComponentString(msg).setStyle(style.setItalic(true)));
    }

    private final Map<String, Object> argMap;
    private final Triple<String, Boolean, Class<?>>[] argTriples;
    private final String name;

    @SafeVarargs
    public CommandCircuitryBase(String name, Triple<String, Boolean, Class<?>>... args) {
	argMap = new HashMap<>();
	argTriples = args;
	this.name = name;
    }

    @Override
    public final void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
	if (args.length > argTriples.length)
	    throw new CommandException("Usage: " + getUsage(sender));
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

    public abstract void execute(World world, ICommandSender sender, String[] args) throws CommandException;

    @Override
    public String getName() {
	return name;
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String key, T defaultValue) throws CommandException {
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
    public String getUsage(ICommandSender sender) {
	StringBuilder usage = new StringBuilder('/' + getName());
	for (Triple<String, Boolean, Class<?>> argTriple : argTriples) {
	    String boundary = argTriple.getMiddle() ? "<>" : "[]";
	    usage.append(" " + boundary.charAt(0) + argTriple.getLeft() + boundary.charAt(1));
	}
	return usage.toString();
    }
}