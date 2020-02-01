package cmsc389e.circuitry.common.command;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cmsc389e.circuitry.common.block.BlockInNode;
import cmsc389e.circuitry.common.block.BlockNode;
import cmsc389e.circuitry.common.block.BlocksCircuitry;
import cmsc389e.circuitry.common.world.data.NodeWorldSavedData;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class CommandTest extends CommandBase {
    private static class Task implements Runnable {
	String[] args;
	ICommandSender sender;
	MinecraftServer server;

	public Task(MinecraftServer server, ICommandSender sender, String[] args) {
	    this.server = server;
	    this.sender = sender;
	    this.args = args;
	}

	@Override
	public void run() {
	    while (lock)
		try {
		    Thread.sleep(500); // wait for awhile to wait for the "lock" which is just a boolean var
		} catch (InterruptedException e1) {
		    e1.printStackTrace();
		}

	    sender.sendMessage(new TextComponentString("\n===============================\n=========Running Test "
		    + testCount + (testCount < 10 ? " " : "") + "=========\n==============================="));
	    lock = true; // this appears to be a useless line unless its hacking a race condition
	    try {
		execute2(server, sender, args, new TestRun());
		lock = false;
	    } catch (CommandException e) {
		testCount--; // make sure the test ends properly and gracefully in case of crash
		sender.sendMessage(new TextComponentString(e.getLocalizedMessage()));
		lock = false;
	    } finally {
		runningTest = null;
	    }
	}

    }

    public static class TestRun {
	String testResults;

	public TestRun() {
	    testResults = null;
	}

	public void setResult(String result) {
	    testResults = result;
	}
    }

    public static String[] keys = null;
    public static boolean lock = false;
    public static String options = null;
    public static Thread runningTest = null;

    public static List<String[]> runs = null;

    public static int testCount = 0;

    // public static String[] labels = null;
    public static byte[] testFile = null;

    public static void execute2(MinecraftServer server, ICommandSender sender, String[] args, TestRun test)
	    throws CommandException {
	if (runs == null) { // runs is apparently a badly named variable which is just the actual contents
			    // of the test
	    // set up
	    FileReader in;
	    try {
		in = new FileReader("tests.txt");
		testFile = Files.readAllBytes(Paths.get("tests.txt"));
	    } catch (IOException e) {
		throw new CommandException(
			"initialize the test framework with \"/load test <path-to-tests>, or /load <proj#>\"",
			new Object[0]);
	    }

	    BufferedReader i = new BufferedReader(in);
	    runs = new LinkedList<>();
	    try {
		options = i.readLine();
//    			labels = i.readLine().split("\t"); //get the input/output group labels
		keys = i.readLine().split("\t");
		String line = null;
		while ((line = i.readLine()) != null) {
		    if (line.isEmpty() || line.charAt(0) == '#') // janky comment and whitespace detection
			continue;
		    String[] map = line.split("\t");
		    if (map.length == 0)
			continue;
		    runs.add(map);
		}

	    } catch (Exception e) {
		try {
		    i.close();
		} catch (IOException e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}
		keys = null;
		runs = null;
		throw new CommandException("Test framework was improperly formatted", new Object[0]);
	    }

	    try {
		i.close();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	}

	if (args.length > 2 && !Arrays.asList(args).contains("-norepeat") && !Arrays.asList(args).contains("-nr"))
	    throw new CommandException("/test [delay] [gate] [-nr/-norepeat]", new Object[0]);

	double delay = 0; // approx time between tests

	if (args.length >= 1 && args[0].matches("\\d*\\.?\\d+"))
	    try {
		delay = Double.valueOf(args[0]);
	    } catch (NumberFormatException e) {
		throw new CommandException("You must pass a valid time length (or none at all).", new Object[0]);
	    }
	World world = sender.getEntityWorld();
	Collection<BlockPos> inputs = getInputs(world);
	Collection<BlockPos> outputs = getOutputs(world);
	boolean fullrun = true; // looks like i removed an old parameter really jankily
	String results = "";
	sender.sendMessage(new TextComponentString(Arrays.toString(keys)));
	boolean filter = Arrays.asList(args).contains("-norepeat") || Arrays.asList(args).contains("-nr");
	for (String[] run : runs) {
	    boolean testPassed = true;
	    Map<String, Boolean> expectedOutputs = new HashMap<>(); // label vs what was expected
	    List<String> entriesInOrder = new LinkedList<>();
	    for (int i = 0; i < run.length; i++) {
		String dest = keys[i]; // get the current tag
		if (dest.charAt(0) == 'i') { // match for input block
		    List<BlockPos> matches = getMatches(dest, world, inputs); // list of input blocks in the
									      // world
		    if ("n".equals(options) && matches.size() > 1) // earlier if there was a line saying "n" in settings
								   // then no duplicate inputs are allowed
			throw new CommandException("Duplicate inputs are not allowed for this project.");
		    if (matches.size() == 0)
			throw new CommandException("You are missing input block: " + dest, new Object[0]);
		    // turn all blocks to appropriate states
		    // set all blocks in the world with matching label to be desired state
		    // (convert to bool)
		    for (BlockPos pos : matches)
			BlockNode.setPowered(world, pos, world.getBlockState(pos), run[i].equals("1"));
		} else if (!run[i].equals("o")) {
		    expectedOutputs.put(dest, run[i].equals("1")); // add the output expected value to match for
		    entriesInOrder.add(dest);
		}

	    }

	    lock = false; // release the lock so the game can continue to game cycle.
	    try { // delay for a set amount of time
		if (delay == 0)
		    Thread.sleep(500);
		else
		    Thread.sleep((int) (delay * 1000));
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    while (lock || Minecraft.getMinecraft().isGamePaused())
		try {
		    Thread.sleep(500); // do not run the tests while the game is paused
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
	    lock = true;
	    int target = 0;
	    String actual = "";
	    int j = -1;
	    for (String next : entriesInOrder) {
		j++;
		if (args.length >= 1) // this bit seems to be for testing a specific line. so fullrun does matter
		    if (args.length == 1 && !args[0].matches("\\d*.?\\d+") && args[0].charAt(0) != '-') {
			fullrun = false;
			if (!next.equals("o" + args[0].toUpperCase()))
			    continue;
			else
			    target = j;
		    } else if (args.length == 2 && args[1].charAt(0) != '-') {
			fullrun = false;
			if (!next.equals("o" + args[1].toUpperCase()))
			    continue;
			else
			    target = j;
		    } else if (args.length == 3) {
			fullrun = false;
			if (!next.equals("o" + args[1].toUpperCase()))
			    continue;
			else
			    target = j;
		    }
		boolean key = expectedOutputs.get(next); // answer key
		List<BlockPos> matches = getMatches(next, world, outputs); // matching blocks for current
		if (matches.size() == 0)
		    throw new CommandException("You are missing output block: " + next, new Object[0]);
		boolean state = false;
		boolean set = false;
		boolean consistent = true;

		while (Minecraft.getMinecraft().isGamePaused())
		    try {
			Thread.sleep(500);
		    } catch (InterruptedException e) {
			sender.sendMessage(new TextComponentString("Test interrupted while game paused."));
		    }

		for (BlockPos match : matches) { // janky way of asserting that all blocks for a specific label
						 // are the
		    // same
		    boolean curstate = BlockNode.isPowered(world.getBlockState(match));
		    if (!set) {
			set = true;
			state = curstate;
		    }
		    if (curstate != state) {
			consistent = false;
			break;
		    }
		}

		if (consistent) { // this builds up the outputs for the test ('e' for inconsistent) t / f else.
		    boolean powered = BlockNode.isPowered(world.getBlockState(matches.get(0)));
		    if (powered != key) {
			testPassed = false;
			results += "false\n";
		    } else
			results += "true\n";
		    actual += (powered ? 1 : 0) + " ";
		} else {
		    testPassed = false;
		    actual += "e ";
		    results += "false \n";
		}

	    }

	    String outTest = Arrays.toString(Arrays.copyOfRange(run, CommandLoad.inputs.length, run.length));
	    // above just copies the output portion of the line of input / outputs
	    if (!fullrun)
		outTest = run[target + CommandLoad.inputs.length];
	    TextComponentString out = new TextComponentString(
		    Arrays.toString(Arrays.copyOf(run, CommandLoad.inputs.length)) + " | " + outTest + "  |  " + actual
			    + " | " + (testPassed ? " P" : " F")); // this is just an info dump to the player
	    if (!testPassed)
		out.getStyle().setColor(TextFormatting.DARK_RED);

	    if (!filter || !testPassed)
		sender.sendMessage(out);

	    for (BlockPos i : inputs) // reset
		BlockNode.setPowered(world, i, world.getBlockState(i), false);

	}

	test.setResult(results);
	if (fullrun)
	    CommandSubmit.mostRecentTestRun = test; // add this as a finished test run for submission later.
	sender.sendMessage(new TextComponentString("Test finished successfully."));
    }

    /**
     * gibberish to get all of the inputs block
     *
     * More specficially, the code calls the static
     * {@link NodeWorldSavedData#get(World)} to get an instance of saved data for
     * the given {@link World}. Then it asks for the {@link Collection} of
     * {@link BlockInNode}s stored.
     */
    static Collection<BlockPos> getInputs(World world) {
	return getNodes(world, BlocksCircuitry.IN_NODE);
    }

    /**
     * go through each block and pick the ones whose tags match the search filter
     */
    public static List<BlockPos> getMatches(String key, World world, Collection<BlockPos> poses) {
	List<BlockPos> matches = new ArrayList<>();

	poses.forEach(pos -> {
	    if (key.equals(getTag(world, pos)))
		matches.add(pos);
	});

	return matches;
    }

    private static Collection<BlockPos> getNodes(World world, Block node) {
	return NodeWorldSavedData.get(world).getPos(node);
    }

    /**
     * gibberish to get all of the output blocks.
     */
    private static Collection<BlockPos> getOutputs(World world) {
	return getNodes(world, BlocksCircuitry.OUT_NODE);
    }

    private static String getTag(World world, BlockPos pos) {
	return NodeWorldSavedData.get(world).getTag(pos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
	Task t = new Task(server, sender, args);

	// we launch a new thread so that the game doesnt freeze while the tests runs.
	Thread th = new Thread(t);
	runningTest = th;
	th.start();
	testCount++;

    }

    @Override
    public String getName() {
	return "test";
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
	return "Run the passed project's test build";
    }
}
