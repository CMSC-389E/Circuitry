package cmsc389e.circuitry.common.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public abstract class CommandCircuitryBase extends CommandBase {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public static @interface Optional {
    }

    private static boolean isOptional(Parameter parameter) {
	return parameter.isAnnotationPresent(Optional.class);
    }

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

    public static void sendMessage(ICommandSender sender, String msg) {
	sendMessage(sender, msg, new Style());
    }

    public static void sendMessage(ICommandSender sender, String msg, Style style) {
	sender.sendMessage(new TextComponentString(msg).setStyle(style.setItalic(true)));
    }

    private final String name;

    private final Method execute;

    public CommandCircuitryBase(String name) {
	this.name = name;
	execute = findMethod();
    }

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

    @Override
    public String getName() {
	return name;
    }

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