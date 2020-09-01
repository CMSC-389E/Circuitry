package cmsc389e.circuitry.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Predicates;

import cmsc389e.circuitry.common.block.NodeBlock;
import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;
import net.minecraft.world.server.ServerTickList;
import net.minecraft.world.server.ServerWorld;

public class Tester implements Runnable {
    private static final Style DEFAULT = new Style(), FAILED = new Style().setColor(TextFormatting.RED),
	    IN = new Style().setColor(TextFormatting.LIGHT_PURPLE), OUT = new Style().setColor(TextFormatting.AQUA),
	    PASSED = new Style().setColor(TextFormatting.GREEN);
    public static Tester INSTANCE;

    private static String join(boolean[] actual) {
	Integer[] integers = new Integer[actual.length];
	for (int i = 0; i < integers.length; i++)
	    integers[i] = actual[i] ? 1 : 0;
	return join(integers);
    }

    private static String join(Object[] array) {
	return StringUtils.join(array, ' ');
    }

    public final StringBuilder results;
    public boolean running;

    private final MinecraftServer server;
    private final Map<String, TileEntity> tagMap;
    private final List<ServerTickList<Block>> tickList;
    private CommandSource source;
    private int delay, index, wait;
    private boolean waiting;

    public Tester(MinecraftServer server) {
	this.server = server;
	results = new StringBuilder();
	tagMap = new HashMap<>();
	tickList = StreamSupport.stream(server.getWorlds().spliterator(), true).map(ServerWorld::getPendingBlockTicks)
		.collect(Collectors.toList());
    }

    @SuppressWarnings("resource")
    @Override
    public void run() {
	if (running)
	    if (waiting) {
		if (--wait <= 0) {
		    sendFeedback("Test " + index + ':', DEFAULT);
		    sendFeedback("In: " + join(Config.inTests[index]), IN);
		    sendFeedback("Out: " + join(Config.outTests[index]), OUT);
		    for (int i = 0; i < Config.inTags.length; i++) {
			TileEntity te = tagMap.get(Config.inTags[i]);
			NodeBlock.setPowered(te.getWorld(), te.getBlockState(), te.getPos(), Config.inTests[index][i]);
		    }
		    waiting = false;
		}
	    } else if (tickList.parallelStream().map(ServerTickList::func_225420_a).allMatch(Predicates.equalTo(0))) {
		boolean[] actual = new boolean[Config.outTags.length];
		for (int i = 0; i < Config.outTags.length; i++)
		    actual[i] = tagMap.get(Config.outTags[i]).getBlockState().get(NodeBlock.POWERED);
		sendFeedback("Actual: " + join(actual) + '\n',
			Arrays.equals(actual, Config.outTests[index]) ? PASSED : FAILED);

		if (++index == Config.inTests.length) {
		    sendFeedback("Testing complete.", DEFAULT);
		    running = false;
		}
		waiting = true;
		wait = delay;
	    }
    }

    private void sendFeedback(String message, Style style) {
	results.append(message + '\n');
	source.sendFeedback(new StringTextComponent(message).setStyle(style), true);
    }

    public void start(CommandSource source, int delay) {
	if (!Config.loaded)
	    throw new CommandException(new StringTextComponent("No tests are loaded!"));

	results.setLength(0);
	tagMap.clear();

	List<String> tags = new ArrayList<>(Arrays.asList(Config.inTags));
	tags.addAll(Arrays.asList(Config.outTags));
	NodeTileEntity.forEach(server, te -> {
	    String tag = te.getTag();
	    if (tagMap.put(tag, te) != null)
		throw new CommandException(new StringTextComponent("Duplicate tag found: " + tag));
	    tags.remove(tag);
	});
	if (!tags.isEmpty())
	    throw new CommandException(new StringTextComponent("The following tags are missing: " + tags));

	IN.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new StringTextComponent(join(Config.inTags))));
	OUT.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new StringTextComponent(join(Config.outTags))));

	this.source = source;
	this.delay = delay;
	index = 0;
	wait = 0;
	waiting = true;
	running = true;

	String message = "Starting Testing...";
	String separator = StringUtils.repeat('-', message.length());
	sendFeedback(separator + '\n' + message + '\n' + separator, DEFAULT);
    }
}
