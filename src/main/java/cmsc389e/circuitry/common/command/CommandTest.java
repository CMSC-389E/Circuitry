package cmsc389e.circuitry.common.command;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import cmsc389e.circuitry.ConfigCircuitry;
import cmsc389e.circuitry.common.block.BlockNode;
import cmsc389e.circuitry.common.world.CircuitryWorldSavedData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public final class CommandTest extends CommandCircuitryBase {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static Future<?> future;

    protected static void cancel() {
	future.cancel(true);
    }

    protected static boolean isRunning() {
	return future != null && !future.isDone();
    }

    private static boolean runTest(World world, Map<String, Pair<BlockPos, IBlockState>> tags, String[] header,
	    String[] test) throws IllegalArgumentException {
	for (int i = 0; i < ConfigCircuitry.inputs.length; i++) {
	    Pair<BlockPos, IBlockState> pair = tags.get(header[i]);
	    BlockNode.setPowered(world, pair.getLeft(), pair.getRight(), test[i].equals("1"));
	}
	for (int i = ConfigCircuitry.inputs.length; i < header.length; i++)
	    if (BlockNode.isPowered(tags.get(header[i]).getRight()) != test[i].equals("1"))
		return false;
	return true;
    }

    private static void runTests(World world, ICommandSender sender, String output, int delay,
	    Map<String, Pair<BlockPos, IBlockState>> tags, String[] header) {
	future = EXECUTOR.submit(() -> {
	    try {
		sendMessage(sender, "Running test on " + (output == null ? "all outputs" : "output " + output)
			+ " with delay " + delay);
		// Starts running tests
		boolean successful = true;
		for (int i = 2; i < header.length; i++)
		    if (output == null || header[i].equals(output)) {
			String[] test = TEST_LINES.get(i);
			char result = 'T';
			TextFormatting color = TextFormatting.GREEN;
			if (!runTest(world, tags, header, test)) {
			    successful = false;
			    result = 'F';
			    color = TextFormatting.RED;
			}
			StringBuilder message = new StringBuilder();
			for (String tag : test)
			    message.append(tag + ' ');
			message.append("| " + result);
			sendMessage(sender, message.toString(), color);

			// Delay between tests specified by the command arguments
			Thread.sleep(delay * 1000);
		    }
		tags.values().forEach(pair -> BlockNode.setPowered(world, pair.getLeft(), pair.getRight(), false));
		sendMessage(sender,
			successful ? "All tests completed successfully." : "Tests completed. Some tests failed.");
	    } catch (InterruptedException e) {
	    }
	    resetInputs(world);
	});
    }

    public CommandTest() {
	super("test", Triple.of("delay", false, int.class), Triple.of("output", false, String.class));
    }

    @Override
    public void execute(World world, ICommandSender sender, String[] args) throws CommandException {
	String output = getOrDefault("output", null);
	int delay = getOrDefault("delay", 1);

	if (isRunning())
	    throw new CommandException("Test already running!");

	if (TEST_LINES.isEmpty())
	    tryReadTestsFile();

	// Prepare Map of tags paired with their BlockPos and IBlockState
	Map<String, Pair<BlockPos, IBlockState>> tags = new HashMap<>();
	CircuitryWorldSavedData.get(world).forEach(pos -> {
	    IBlockState state = world.getBlockState(pos);
	    tags.put(BlockNode.getTag(world, pos, state), Pair.of(pos, state));
	});

	// Checks all tags are in the world
	String[] header = TEST_LINES.get(1);
	for (String tag : header)
	    if (!tags.containsKey(tag))
		throw new CommandException("No node block found with tag " + tag + '.');

	runTests(world, sender, output, delay, tags, header);
    }
}