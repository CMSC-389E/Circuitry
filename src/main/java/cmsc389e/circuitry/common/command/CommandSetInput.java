package cmsc389e.circuitry.common.command;

import java.util.List;

import cmsc389e.circuitry.common.block.BlockNode;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandSetInput extends CommandBase {

    public static class TestRun {
	String testResults;

	public TestRun() {
	    testResults = null; // clear test results for a new one.
	}

	public void setResult(String result) {
	    testResults = result;
	}
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

	World world = sender.getEntityWorld();
	List<BlockPos> inputs = CommandTest.getInputs(world);

	if (args.length != 2 && (args.length != 1 || !args[0].equals("off")))
	    throw new CommandException("You must provide the desired input block as well as the desired state");

	boolean state = false;
	if (args.length == 2) // defaults to setting to false, but if a boolean val is provided, read that in
			      // instead.
	    state = Boolean.parseBoolean(args[1]);

	if ("off".equals(args[0])) { // off command sets everything false instead of just one thing.
	    for (BlockPos input : inputs)
		BlockNode.setPowered(world, input, world.getBlockState(input), false);
	    return;
	}
	List<BlockPos> matches = CommandTest.getInputMatches("i" + args[0].toUpperCase(), inputs); // add i (to make it
												   // input) and look
												   // for the block
	for (BlockPos match : matches)
	    BlockNode.setPowered(world, match, world.getBlockState(match), state); // set the block

    }

    @Override
    public String getName() {
	return "set";
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
	return "Set an input block to a desired state";
    }
}
