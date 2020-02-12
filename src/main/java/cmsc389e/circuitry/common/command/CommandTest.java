package cmsc389e.circuitry.common.command;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;

import cmsc389e.circuitry.ConfigCircuitry;
import cmsc389e.circuitry.common.block.BlockCircuitryRedstoneWire;
import cmsc389e.circuitry.common.block.BlockNode;
import cmsc389e.circuitry.common.world.CircuitryWorldSavedData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandTest extends CommandCircuitryBase {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static Future<?> future;

    protected static void cancel() {
	future.cancel(true);
    }

    private static String getMessage(String[] line) {
	StringBuilder message = new StringBuilder();
	for (String element : line)
	    message.append(element + ' ');
	return message.toString();
    }

    protected static boolean isRunning() {
	return future != null && !future.isDone();
    }

    private static boolean runTest(Map<String, Pair<BlockPos, IBlockState>> tags, World world, String[] header,
	    String[] test) throws IllegalArgumentException {
	waitForUpdate(world);
	for (int i = 0; i < ConfigCircuitry.inputs.length; i++) {
	    Pair<BlockPos, IBlockState> pair = tags.get(header[i]);
	    BlockNode.setPowered(world, pair.getLeft(), pair.getRight(), test[i].equals("1"));
	}
	waitForUpdate(world);
	for (int i = ConfigCircuitry.inputs.length; i < header.length; i++)
	    if (BlockNode.isPowered(tags.get(header[i]).getRight()) != test[i].equals("1"))
		return false;
	return true;
    }

    private static String runTests(String[] header, String output, Map<String, Pair<BlockPos, IBlockState>> tags,
	    World world, int delay) {
	StringBuilder out = new StringBuilder();
	future = EXECUTOR.submit(() -> {
	    try {
		// Starts running tests
		boolean successful = true;
		for (int i = 2; i < header.length; i++)
		    if (output == null || header[i].equals(output)) {
			String[] test = TEST_LINES.get(i);
			char result = 'T';
			if (!runTest(tags, world, header, test)) {
			    successful = false;
			    result = 'F';
			}
			out.append(getMessage(test) + "| " + result);

			// Delay between tests specified by the command arguments
			Thread.sleep(delay * 1000);
		    }
		tags.values().forEach(pair -> BlockNode.setPowered(world, pair.getLeft(), pair.getRight(), false));
		out.append(successful ? "All tests completed successfully." : "Tests completed. Some tests failed.");
	    } catch (InterruptedException e) {
	    }
	});
	return out.toString();
    }

    private static void waitForUpdate(World world) {
	// Checks method in BlockCircuitryRedstoneWire that reports whether any redstone
	// wire is in the process of updating. Prevents overlapping tests (hopefully)
	while (BlockCircuitryRedstoneWire.isUpdating(world))
	    ;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
	// Parse arguments
	if (args.length > 2)
	    throw new CommandException("Usage: /test [delay] [outputs]");
	int delay = 1;
	String output = null;
	if (args.length == 1)
	    if (NumberUtils.isCreatable(args[0]))
		delay = parseInt(args[0]);
	    else
		output = args[0];
	else if (args.length == 2) {
	    delay = parseInt(args[0]);
	    output = args[1];
	}

	if (isRunning())
	    throw new CommandException("Test already running!");

	if (TEST_LINES.isEmpty())
	    tryReadTestsFile();

	// Prepare Map of tags paired with their BlockPos and IBlockState
	World world = server.getEntityWorld();
	Map<String, Pair<BlockPos, IBlockState>> tags = new HashMap<>();
	CircuitryWorldSavedData.get(world).posSet().forEach(pos -> {
	    IBlockState state = world.getBlockState(pos);
	    tags.put(BlockNode.getTag(world, pos, state), Pair.of(pos, state));
	});

	// Checks all tags are in the world
	String[] header = TEST_LINES.get(1);
	for (String tag : header)
	    if (!tags.containsKey(tag))
		throw new CommandException("No node block found with tag " + tag + '.');

	sendMessage(sender, runTests(header, output, tags, world, delay));
    }

    @Override
    public String getName() {
	return "test";
    }

    @Override
    public String getUsage(ICommandSender sender) {
	return "Run the passed project's test build";
    }
}