package cmsc389e.circuitry.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandAbort extends CommandBase {
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
	if (CommandTest.runningTest != null) {
	    CommandTest.runningTest.interrupt();
	    sender.sendMessage(new TextComponentString("Current test aborted"));
	} else
	    sender.sendMessage(new TextComponentString("There is no test currently running"));
    }

    @Override
    public String getName() {
	return "abort";
    }

    /**
     * Return the required permission level for this command.
     */
    @Override
    public int getRequiredPermissionLevel() {
	return 0;
    }

    @Override
    public String getUsage(ICommandSender sender) {
	return "Halts any currently running tests.";
    }
}