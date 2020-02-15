package cmsc389e.circuitry.common.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;

public final class CommandAbort extends CommandCircuitryBase {
    public CommandAbort() {
	super("abort");
    }

    @Override
    public void execute(World world, ICommandSender sender, String[] args) throws CommandException {
	if (!CommandTest.isRunning())
	    throw new CommandException("No test currently running.");
	CommandTest.cancel();
	sendMessage(sender, "Test aborted!");
	resetInputs(world);
    }
}