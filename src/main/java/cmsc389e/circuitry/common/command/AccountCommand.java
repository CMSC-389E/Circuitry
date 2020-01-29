package cmsc389e.circuitry.common.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class AccountCommand extends CommandBase {

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

	if (args.length != 2)
	    throw new CommandException("You must enter both a username and password: \"/account <uid> <pwd\"",
		    new Object[0]);

	try {
	    Files.deleteIfExists(Paths.get("submit", ".submitUser"));
	} catch (IOException e) {
	    e.printStackTrace();
	}

	CommandSubmit.uname = args[0];
	CommandSubmit.pwd = args[1];

	sender.sendMessage(new TextComponentString("Identity created - will be verified upon submission"));

    }

    @Override
    public String getName() {
	return "account";
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
	return "Create user profile";
    }
}
