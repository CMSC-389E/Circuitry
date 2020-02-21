package cmsc389e.circuitry.common.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Triple;

import cmsc389e.circuitry.ConfigCircuitry;
import cmsc389e.circuitry.common.block.BlockNode;
import cmsc389e.circuitry.common.world.CircuitryWorldSavedData;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class CommandTest extends CommandCircuitryBase {
    private final class Tester implements ITickable {
	private final HoverEvent hoverEvent;
	private final String output;
	private int phase, test, wait;
	private final ICommandSender sender;
	private final Map<String, BlockPos> tags;
	private final boolean[][] tests;
	private final Set<?> tickSet;
	private final World world;

	private Tester(World world, ICommandSender sender) throws CommandException {
	    this.world = world;
	    this.sender = sender;

	    output = getOrDefault("output", null);

	    tags = new HashMap<>();
	    tests = CommandLoad.getTests();
	    tickSet = ObfuscationReflectionHelper.getPrivateValue(WorldServer.class, (WorldServer) world,
		    "field_73064_N"); // pendingTickListEntriesHashSet

	    String in = "In: " + Arrays.toString(ConfigCircuitry.inTags);
	    String out = "Out: " + Arrays.toString(ConfigCircuitry.outTags);
	    hoverEvent = new HoverEvent(Action.SHOW_TEXT, new TextComponentString(in + '\n' + out));

	    init(in, out);
	}

	private void checkOutputs() {
	    boolean[] outputs = new boolean[ConfigCircuitry.outTags.length];
	    boolean passed = true;
	    for (int i = 0; i < outputs.length; i++) {
		outputs[i] = BlockNode.isPowered(world.getBlockState(tags.get(ConfigCircuitry.outTags[i])));
		passed &= output != null && ConfigCircuitry.outTags[i].equals(output)
			|| outputs[i] == tests[test][i + ConfigCircuitry.inTags.length];
	    }
	    sendMessage(sender, "Result: " + toString(outputs) + (passed ? "" : " [FAILED]"),
		    new Style().setColor(passed ? TextFormatting.GREEN : TextFormatting.RED));
	}

	private void init(String in, String out) {
	    CircuitryWorldSavedData.get(world)
		    .forEach(pos -> tags.put(BlockNode.getTag(world, pos, world.getBlockState(pos)), pos));

	    sendMessage(sender, "Starting tests...");
	    sendMessage(sender, in, new Style().setColor(TextFormatting.LIGHT_PURPLE));
	    sendMessage(sender, out, new Style().setColor(TextFormatting.AQUA));
	    setInputs(i -> true);
	}

	private void setInputs() {
	    sendMessage(sender,
		    "\nIn:       " + toString(Arrays.copyOf(tests[test], ConfigCircuitry.inTags.length)) + "\nOut:     "
			    + toString(
				    Arrays.copyOfRange(tests[test], ConfigCircuitry.inTags.length, tests[test].length)),
		    new Style().setHoverEvent(hoverEvent));
	    setInputs(i -> tests[test][i]);
	}

	private void setInputs(Predicate<Integer> predicate) {
	    for (int i = 0; i < ConfigCircuitry.inTags.length; i++) {
		BlockPos pos = tags.get(ConfigCircuitry.inTags[i]);
		BlockNode.setPowered(world, pos, world.getBlockState(pos), predicate.test(i));
	    }
	}

	private boolean shouldWait() {
	    // Twenty ticks is one second.
	    return wait < 20 && !tickSet.isEmpty();
	}

	private String toString(boolean[] array) {
	    StringBuilder string = new StringBuilder();
	    for (boolean element : array)
		string.append(" " + (element ? '1' : '0'));
	    return string.substring(1);
	}

	@Override
	public void update() {
	    if (shouldWait())
		wait++;
	    else {
		switch (phase % 2) {
		case 0:
		    setInputs();
		    break;
		case 1:
		    checkOutputs();
		    if (++test == tests.length) {
			abort(world);
			sendMessage(sender, "Testing complete.");
		    }
		}
		phase++;
		wait = 0;
	    }
	}
    }

    private static final Map<World, Tester> TESTERS = new HashMap<>();

    public static void abort(World world) {
	TESTERS.remove(world);
    }

    public static boolean isRunning(World world) {
	return TESTERS.get(world) != null;
    }

    public static void sendMessage(ICommandSender sender, String msg) {
	sendMessage(sender, msg, new Style());
    }

    public static void sendMessage(ICommandSender sender, String msg, Style style) {
	CommandCircuitryBase.sendMessage(sender, msg, style);
	try {
	    Files.write(Paths.get(ConfigCircuitry.testLogs), (msg + '\n').getBytes(), StandardOpenOption.APPEND,
		    StandardOpenOption.CREATE);
	} catch (IOException e) {
	    CommandCircuitryBase.sendMessage(sender, "Can't write to " + ConfigCircuitry.testLogs + '!',
		    new Style().setColor(TextFormatting.RED));
	}
    }

    public static void tick(World world) {
	TESTERS.get(world).update();
    }

    public CommandTest() {
	super("test", Triple.of("output", false, String.class));
	try {
	    Files.deleteIfExists(Paths.get(ConfigCircuitry.testLogs));
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void execute(World world, ICommandSender sender, String[] args) throws CommandException {
	TESTERS.put(world, new Tester(world, sender));
    }
}