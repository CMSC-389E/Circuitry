package cmsc389e.circuitry.common.command;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;

import cmsc389e.circuitry.ConfigCircuitry;
import cmsc389e.circuitry.common.block.BlockNode;
import cmsc389e.circuitry.common.event.WorldHandler;
import cmsc389e.circuitry.common.world.CircuitryWorldSavedData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import scala.actors.threadpool.Arrays;

public final class CommandTest extends CommandCircuitryBase {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private static Future<?> future;

    protected static void cancel(World world) {
	future.cancel(true);
	resetInputs(world);
    }

    protected static boolean isRunning() {
	return future != null && !future.isDone();
    }

    protected static void resetInputs(World world) {
	CircuitryWorldSavedData.get(world)
		.forEach(pos -> BlockNode.setPowered(world, pos, world.getBlockState(pos), false));
    }

    public CommandTest() {
	super("test", Triple.of("delay", false, int.class), Triple.of("output", false, String.class));
    }

    @Override
    public void execute(World world, ICommandSender sender, String[] args) throws CommandException {
	String output = getOrDefault("output", null);
	int delay = getOrDefault("delay", 0);

	if (isRunning())
	    throw new CommandException("Test already running!");

	if (CommandLoad.TESTS.isEmpty())
	    CommandLoad.readTests();

	// Prepare Map of tags paired with their BlockPos and IBlockState
	Map<String, BlockPos> tags = new HashMap<>();
	CircuitryWorldSavedData.get(world).forEach(pos -> tags.put(BlockNode.getTag(world, pos), pos));

	if (tags.size() != ConfigCircuitry.inputs.length + ConfigCircuitry.outputs.length)
	    throw new CommandException("Missing tags. Make sure every tag is used.");

	sendMessage(sender,
		StringUtils.repeat('=', 10) + "\nRunning test on "
			+ (output == null ? "all outputs" : "output " + output) + " with a delay of " + delay
			+ " seconds.\n" + StringUtils.repeat('=', 10) + '\n' + Arrays.toString(ConfigCircuitry.inputs)
			+ ' ' + Arrays.toString(ConfigCircuitry.outputs));

	future = EXECUTOR.submit(new Thread() {
	    @Override
	    public void run() {
		try {
		    // Starts running tests
		    boolean allPassed = true;
		    for (String[] test : CommandLoad.TESTS) {
			StringBuilder message = new StringBuilder("\nIn:       ");
			for (int i = 0; i < ConfigCircuitry.inputs.length; i++)
			    message.append(test[i] + ' ');
			message.append("\nOut:     ");
			for (int i = ConfigCircuitry.inputs.length; i < test.length; i++)
			    message.append(test[i] + ' ');
			sendMessage(sender, message.toString());

			waitForPendingTicks(world);

			for (int i = 0; i < ConfigCircuitry.inputs.length; i++) {
			    BlockPos pos = tags.get(ConfigCircuitry.inputs[i]);
			    BlockNode.setPowered(world, pos, world.getBlockState(pos), test[i].equals("1"));
			}

			waitForPendingTicks(world);

			// Get output states
			boolean passed = true;
			String[] results = new String[ConfigCircuitry.outputs.length];
			for (int i = 0; i < results.length; i++) {
			    results[i] = BlockNode.isPowered(world.getBlockState(tags.get(ConfigCircuitry.outputs[i])))
				    ? "1"
				    : "0";
			    passed &= results[i].equals(test[ConfigCircuitry.inputs.length + i]);
			}
			allPassed &= passed;

			message = new StringBuilder("Actual: ");
			for (String result : results)
			    message.append(result + ' ');
			sendMessage(sender, message.toString(), passed ? TextFormatting.GREEN : TextFormatting.RED);

			// Delay between tests specified by the command arguments
			sleep(delay * 1000);
		    }

		    sendMessage(sender,
			    allPassed ? "All tests completed successfully." : "Tests completed. Some tests failed.");
		} catch (InterruptedException e) {
		}
		resetInputs(world);
	    }

	    private synchronized void waitForPendingTicks(World world) throws InterruptedException {
		do
		    sleep(100);
		while (!WorldHandler.PENDING_TICKS.get(world).isEmpty());
	    }
	});
    }
}