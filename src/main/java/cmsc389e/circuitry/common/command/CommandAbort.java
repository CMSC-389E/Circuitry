package cmsc389e.circuitry.common.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandAbort extends CommandCircuitryBase {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
	if (args.length != 0)
	    throw new CommandException("Usage: /abort");

	if (!CommandTest.isRunning())
	    throw new CommandException("No test currently running.");
	CommandTest.cancel();
	sendMessage(sender, "Test aborted!");

    }

    @Override
    public String getName() {
	return "abort";
    }

    @Override
    public String getUsage(ICommandSender sender) {
	return "Cancel currently running test";
    }
}