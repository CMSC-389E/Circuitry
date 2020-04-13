package cmsc389e.circuitry.common.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;

/**
 * Command that aborts any currently running tests. The correct syntax is
 * {@code /abort} with no arguments.
 */
public class CommandAbort extends CommandCircuitryBase {
    /**
     * TODO
     *
     * @param world  TODO
     * @param sender TODO
     * @throws CommandException TODO
     */
    public static void execute(World world, ICommandSender sender) throws CommandException {
	if (CommandTest.abort(world) == null)
	    throw new CommandException("No test currently running.");
	sendMessage(sender, "Test aborted!");
    }

    /**
     * TODO
     */
    public CommandAbort() {
	super("abort");
    }
}