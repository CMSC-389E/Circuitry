package cmsc389e.circuitry.common.command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import cmsc389e.circuitry.common.CircuitryWorldSavedData;
import cmsc389e.circuitry.common.ConfigCircuitry;
import cmsc389e.circuitry.common.block.BlockNode;
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
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class CommandTest extends CommandCircuitryBase {
    private static final class Tester implements ITickable {
	private enum Phase {
	    SET, GET;

	    private Phase next() {
		Phase[] phases = values();
		return phases[(ordinal() + 1) % phases.length];
	    }
	}

	private static String toString(boolean[] array) {
	    return toString(array, array.length);
	}

	private static String toString(boolean[] array, int newLength) {
	    return toString(array, 0, newLength);
	}

	private static String toString(boolean[] array, int from, int size) {
	    StringBuilder string = new StringBuilder();
	    for (int i = 0; i < size; i++)
		string.append(" " + (array[i + from] ? '1' : '0'));
	    return string.toString();
	}

	private final World world;
	private final ICommandSender sender;
	private final int delay, from, size;

	private final Style style;
	private final Map<String, BlockPos> tags;
	private final boolean[][] tests;
	private final Set<?> tickSet;
	private final Path testLogs;

	private Phase phase;
	private int test, wait;

	private Tester(World world, ICommandSender sender, int delay, String output) throws CommandException {
	    System.out.println(FMLLog.log);
	    this.world = world;
	    this.sender = sender;
	    this.delay = delay;
	    if (output == null) {
		from = 0;
		size = ConfigCircuitry.outTags.length;
	    } else {
		from = ArrayUtils.indexOf(ConfigCircuitry.outTags, output);
		size = 1;
		if (from == ArrayUtils.INDEX_NOT_FOUND)
		    throw new CommandException("Invalid output tag!");
	    }

	    String in = "In: " + String.join(" ", ConfigCircuitry.inTags);
	    String out = "Out: " + StringUtils.join(ConfigCircuitry.outTags, " ", from, from + size);
	    style = new Style()
		    .setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new TextComponentString(in + '\n' + out)));
	    tags = new HashMap<>();
	    tests = CommandLoad.getTests();
	    tickSet = ObfuscationReflectionHelper.getPrivateValue(WorldServer.class, (WorldServer) world,
		    "field_73064_N"); // pendingTickListEntriesHashSet
	    testLogs = world.getSaveHandler().getWorldDirectory().toPath().resolve(ConfigCircuitry.testLogs);

	    phase = Phase.SET;

	    try {
		Files.deleteIfExists(testLogs);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	    for (BlockPos pos : CircuitryWorldSavedData.get(world)) {
		String tag = BlockNode.getTag(world, pos, world.getBlockState(pos));
		if (tags.put(tag, pos) != null)
		    throw new CommandException("More than node block found with tag: " + tag + '!');
	    }
	    String tag;
	    if ((tag = findMissingTag(ConfigCircuitry.inTags)) != null
		    || (tag = findMissingTag(ConfigCircuitry.outTags)) != null)
		throw new CommandException("No node block found with tag: " + tag + '!');
	    sendMessage(sender, "Starting tests...");
	    sendMessage(sender, in, new Style().setColor(TextFormatting.LIGHT_PURPLE));
	    sendMessage(sender, out, new Style().setColor(TextFormatting.AQUA));
	    setInputs(i -> true);

	}

	private String findMissingTag(String[] tags) {
	    for (String tag : tags)
		if (!this.tags.containsKey(tag))
		    return tag;
	    return null;
	}

	private void getOutputs() {
	    boolean[] results = new boolean[size];
	    boolean passed = true;
	    for (int i = 0; i < size; i++) {
		results[i] = BlockNode.isPowered(world.getBlockState(tags.get(ConfigCircuitry.outTags[i + from])));
		passed &= results[i] == tests[test][i + from + ConfigCircuitry.inTags.length];
	    }
	    sendMessage(sender, "Result:" + toString(results) + (passed ? "" : " [FAILED]"),
		    new Style().setColor(passed ? TextFormatting.GREEN : TextFormatting.RED));
	}

	public void sendMessage(ICommandSender sender, String msg) {
	    sendMessage(sender, msg, new Style());
	}

	public void sendMessage(ICommandSender sender, String msg, Style style) {
	    CommandCircuitryBase.sendMessage(sender, msg, style);
	    try {
		Files.write(testLogs, (msg + '\n').getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
	    } catch (IOException e) {
		CommandCircuitryBase.sendMessage(sender, "Can't write to " + testLogs + '!',
			new Style().setColor(TextFormatting.RED));
	    }
	}

	private void setInputs() {
	    sendMessage(sender, "\nIn:      " + toString(tests[test], ConfigCircuitry.inTags.length) + "\nOut:    "
		    + toString(tests[test], from + ConfigCircuitry.inTags.length, size), style);
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
	    return wait++ / 20 < delay || !tickSet.isEmpty();
	}

	@Override
	public void update() {
	    if (!shouldWait()) {
		switch (phase) {
		case SET:
		    setInputs();
		    break;
		case GET:
		    getOutputs();
		    if (++test == tests.length) {
			abort(world);
			sendMessage(sender, "Testing complete.");
		    }
		    break;
		default:
		}
		phase = phase.next();
		wait = 0;
	    }
	}
    }

    private static final Map<World, Tester> TESTERS = new HashMap<>();

    public static Tester abort(World world) {
	return TESTERS.remove(world);
    }

    public static void execute(World world, ICommandSender sender, @Optional Integer delay, @Optional String output)
	    throws CommandException {
	if (isRunning(world))
	    throw new CommandException("Another test is already running!");
	TESTERS.put(world, new Tester(world, sender, delay == null ? 0 : delay, output));
    }

    public static boolean isRunning(World world) {
	return TESTERS.get(world) != null;
    }

    public static void tick(World world) {
	TESTERS.get(world).update();
    }

    public CommandTest() {
	super("test");
    }
}