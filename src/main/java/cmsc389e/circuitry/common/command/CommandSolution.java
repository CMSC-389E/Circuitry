package cmsc389e.circuitry.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;

public class CommandSolution extends CommandBase {
    ServerCommandManager manager;

    public CommandSolution(ServerCommandManager backreference) {
	manager = backreference;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

	throw new CommandException("unimplemented");

    }

    @Override
    public String getName() {
	return "solution";
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
	return "Load the submission world";
    }
}
