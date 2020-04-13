package cmsc389e.circuitry.common.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import cmsc389e.circuitry.common.ConfigCircuitry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

/**
 * TODO
 */
public abstract class CommandCircuitryBase extends CommandBase {
    /**
     * TODO
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public static @interface Optional {
	// TODO
    }

    /**
     * TODO
     *
     * @param parameter TODO
     * @return TODO
     */
    private static boolean isOptional(Parameter parameter) {
	return parameter.isAnnotationPresent(Optional.class);
    }

    /**
     * TODO
     *
     * @param parameter TODO
     * @param arg       TODO
     * @return TODO
     * @throws CommandException TODO
     */
    @Nullable
    private static Object parseArg(Parameter parameter, String arg) throws CommandException {
	Class<?> type = parameter.getType();
	if (StringUtils.isNumeric(arg)) {
	    if (type.equals(Double.class))
		return parseDouble(arg);
	    if (type.equals(Integer.class))
		return parseInt(arg);
	    if (type.equals(Long.class))
		return parseLong(arg);
	}
	if ((arg.equals("true") || arg.equals("false")) && type.equals(Boolean.class))
	    return parseBoolean(arg);
	if (type.equals(String.class))
	    return arg;
	return null;
    }

    /**
     * TODO
     *
     * @param sender TODO
     * @param msg    TODO
     */
    public static void sendMessage(ICommandSender sender, String msg) {
	sendMessage(sender, msg, new Style());
    }

    /**
     * TODO
     *
     * @param sender TODO
     * @param msg    TODO
     * @param style  TODO
     */
    public static void sendMessage(ICommandSender sender, String msg, Style style) {
	sender.sendMessage(new TextComponentString(msg).setStyle(style.setItalic(true)));
    }

    private final String name;

    private final Method execute;

    /**
     * TODO
     *
     * @param name TODO
     */
    public CommandCircuitryBase(String name) {
	this.name = name;
	execute = findMethod();
    }

    /**
     * TODO
     */
    @Override
    public final void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
	try {
	    Object[] parsedArgs = new Object[execute.getParameterCount()];
	    parsedArgs[0] = server.getEntityWorld();
	    parsedArgs[1] = sender;
	    Parameter[] parameters = execute.getParameters();
	    int i = 2;
	    for (String arg : args) {
		if (i >= execute.getParameterCount())
		    throw new CommandException("Too many arguments!");
		while ((parsedArgs[i] = parseArg(parameters[i], arg)) == null) {
		    if (!isOptional(parameters[i]))
			throw new CommandException(parameters[i].getName() + " is required!");
		    i++;
		}
		i++;
	    }
	    for (i = 2; i < execute.getParameterCount(); i++)
		if (parsedArgs[i] == null && !isOptional(parameters[i]))
		    throw new CommandException(parameters[i].getName() + " is required!");
	    execute.invoke(null, parsedArgs);
	} catch (IllegalAccessException e) {
	    e.printStackTrace();
	    throw new CommandException("Cannot execute command for some reason. Send the log to one of the TAs.");
	} catch (InvocationTargetException e) {
	    throw (CommandException) e.getTargetException();
	}
    }

    /**
     * TODO
     *
     * @return TODO
     */
    private Method findMethod() {
	for (Method method : getClass().getMethods())
	    if (method.getName().equals("execute") && method.getParameterCount() >= 2) {
		Parameter[] parameters = method.getParameters();
		if (parameters[0].getType().equals(World.class) && parameters[1].getType().equals(ICommandSender.class))
		    return method;
	    }
	throw new NullPointerException("No method found in " + getClass().getCanonicalName()
		+ " matching signature execute(World, ICommandSender, ...).");
    }

    /**
     * TODO
     */
    @Override
    public String getName() {
	return name;
    }

    /**
     * TODO
     */
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
	    @Nullable BlockPos targetPos) {
	List<String> tags = new ArrayList<>();
	Collections.addAll(tags, ConfigCircuitry.inTags);
	Collections.addAll(tags, ConfigCircuitry.outTags);
	return tags;
    }

    /**
     * TODO
     */
    @Override
    public String getUsage(ICommandSender sender) {
	StringBuilder usage = new StringBuilder('/' + getName());
	Parameter[] parameters = execute.getParameters();
	for (int i = 2; i < parameters.length; i++) {
	    String border = isOptional(parameters[i]) ? "[]" : "<>";
	    usage.append(" " + border.charAt(0) + parameters[i].getName() + border.charAt(1));
	}
	return usage.toString();
    }
}