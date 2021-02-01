package cmsc389e.circuitry.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import cmsc389e.circuitry.common.block.NodeBlock;
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
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
	public static Tester tester;

	private static String join(int[] array) {
		return StringUtils.join(array, ' ');
	}

	private static String join(Object[] array) {
		return StringUtils.join(array, ' ');
	}

	public final StringBuilder results;
	public boolean running;

	private final Map<String, TileEntity> map;
	private final ServerTickList<Block> tickList;
	private final ServerWorld world;

	private int delay, index, passed, wait;
	private CommandSource source;
	private boolean waiting;

	public Tester(ServerWorld world) {
		this.world = world;

		map = new HashMap<>();
		results = new StringBuilder();
		tester = this;
		tickList = world.getPendingBlockTicks();
	}

	@Override
	@SuppressWarnings("resource")
	public void run() {
		if (running)
			if (waiting) {
				if (--wait <= 0) {
					sendFeedback("Test " + index + ':', DEFAULT);
					sendFeedback("In: " + join(Config.inTests[index]), IN);
					sendFeedback("Out: " + join(Config.outTests[index]), OUT);
					for (int i = 0; i < Config.inTags.length; i++) {
						TileEntity te = map.get(Config.inTags[i]);
						NodeBlock.setPowered(te.getWorld(), te.getBlockState(), te.getPos(),
								Config.inTests[index][i] == 1);
					}
					waiting = false;
				}
			} else if (tickList.func_225420_a() == 0) {
				int[] actual = Arrays.stream(Config.outTags)
						.mapToInt(tag -> NodeBlock.isPowered(map.get(tag).getBlockState()) ? 1 : 0).toArray();
				Style style = FAILED;
				if (Arrays.equals(actual, Config.outTests[index])) {
					passed++;
					style = PASSED;
				}
				sendFeedback("Actual: " + join(actual) + '\n', style);

				if (++index == Config.inTests.length) {
					sendFeedback("Testing complete. Passed " + passed + " out of " + Config.inTests.length + " tests.",
							passed == Config.inTests.length ? PASSED : FAILED);
					running = false;
				}
				waiting = true;
				wait = delay;
			}
	}

	private void sendFeedback(String msg, Style style) {
		results.append(msg + '\n');
		source.sendFeedback(new StringTextComponent(msg).setStyle(style), true);
	}

	public void start(CommandSource source, int delay) {
		if (!Config.loaded)
			throw new IllegalStateException("No tests are loaded!");

		map.clear();
		results.setLength(0);

		NodeTileEntity.forEach(world, entity -> {
			if (map.put(entity.tag, entity) != null)
				throw new IllegalStateException("The following tag is duplicated: " + entity.tag);
		});
		String tags = Stream.concat(Arrays.stream(Config.inTags), Arrays.stream(Config.outTags))
				.filter(tag -> !map.containsKey(tag)).collect(Collectors.joining(", "));
		if (!tags.isEmpty())
			throw new IllegalStateException("The following tag(s) are missing: " + tags);

		IN.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new StringTextComponent(join(Config.inTags))));
		OUT.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new StringTextComponent(join(Config.outTags))));

		this.delay = delay;
		this.source = source;

		index = 0;
		passed = 0;
		wait = 0;
		waiting = true;

		String message = "Starting Testing...";
		String separator = StringUtils.repeat('-', message.length());
		sendFeedback(separator + '\n' + message + '\n' + separator, DEFAULT);

		running = true;
	}
}
