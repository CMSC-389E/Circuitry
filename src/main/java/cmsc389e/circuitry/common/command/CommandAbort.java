package cmsc389e.circuitry.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandAbort extends CommandBase {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
	String message = "There is no test currently running";
	if (CommandTest.runningTest != null) {
	    CommandTest.runningTest.interrupt();
	    message = "Current test aborted";
	}
	sender.sendMessage(new TextComponentString(message));
    }

    @Override
    public String getName() {
	return "abort";
    }

    @Override
    public String getUsage(ICommandSender sender) {
	return "Halts any currently running tests.";
    }
}