package cmsc389e.circuitry.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import cmsc389e.circuitry.Circuitry;
import cmsc389e.circuitry.common.block.NodeBlock;
import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerTickList;

public class Tester {
	public static final Style DEFAULT_STYLE = new Style(), FAILED_STYLE = new Style().setColor(TextFormatting.RED),
			PASSED_STYLE = new Style().setColor(TextFormatting.GREEN);
	public static final Map<World, Tester> INSTANCES = new HashMap<>();

	private static String toString(Iterable<?> iterable) {
		return StringUtils.join(iterable, " ");
	}

	private static String toString(List<Boolean> list) {
		List<Integer> intList = new ArrayList<>();
		list.forEach(element -> intList.add(element ? 1 : 0));
		return toString(intList);
	}

	private final int inSize, testsSize;
	private final Style inStyle, outStyle;
	private final Map<String, TileEntity> tags;
	private final ServerTickList<Block> ticks;
	private final CommandSource source;
	private final World world;
	private final int delay;

	private int index, waiting;
	private List<Boolean> outTest;

	public Tester(CommandSource source, World world, int delay) {
		inSize = Config.inTags.size();
		testsSize = Config.inTests.size();
		inStyle = new Style().setColor(TextFormatting.LIGHT_PURPLE)
				.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new StringTextComponent(toString(Config.inTags))));
		outStyle = new Style().setColor(TextFormatting.AQUA)
				.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new StringTextComponent(toString(Config.outTags))));
		tags = new HashMap<>();
		ticks = (ServerTickList<Block>) world.getPendingBlockTicks();

		this.source = source;
		this.world = world;
		this.delay = delay;

		if (Config.inTags.isEmpty() || Config.outTags.isEmpty() || Config.inTests.isEmpty()
				|| Config.outTests.isEmpty())
			throw new CommandException(new StringTextComponent("No test is loaded! Try running load first."));

		world.loadedTileEntityList.forEach(te -> {
			if (te.getType() == Circuitry.nodeType) {
				String tag = ((NodeTileEntity) te).getTag();
				if (tags.put(tag, te) != null)
					throw new CommandException(new StringTextComponent("Duplicate tag found: " + tag));
			}
		});

		List<String> view = new ArrayList<>(Config.inTags);
		view.addAll(Config.outTags);
		view.removeAll(tags.keySet());
		if (!view.isEmpty())
			throw new CommandException(new StringTextComponent("The following tags are missing: " + view));

		String message = "Starting Testing...";
		String separator = StringUtils.repeat('-', message.length());
		sendFeedback(separator + '\n' + message + '\n' + separator, DEFAULT_STYLE);
	}

	private void sendFeedback(String message, Style style) {
		source.sendFeedback(new StringTextComponent(message).setStyle(style), true);
	}

	public void tick() {
		if (--waiting <= 0 && ticks.func_225420_a() == 0) {
			if (outTest != null) {
				List<Boolean> actual = new ArrayList<>();
				Config.outTags.forEach(tag -> actual.add(tags.get(tag).getBlockState().get(NodeBlock.POWERED)));
				sendFeedback("Actual: " + toString(actual) + '\n',
						actual.equals(outTest) ? PASSED_STYLE : FAILED_STYLE);

				index++;
				waiting = delay;
				outTest = null;
			}

			if (index == testsSize) {
				sendFeedback("Testing complete.", DEFAULT_STYLE);
				INSTANCES.remove(world);
			} else if (waiting <= 0) {
				List<Boolean> inTest = Config.inTests.get(index);
				outTest = Config.outTests.get(index);
				sendFeedback("Test " + index + ':', DEFAULT_STYLE);
				sendFeedback("In: " + toString(inTest), inStyle);
				sendFeedback("Out: " + toString(outTest), outStyle);
				for (int i = 0; i < inSize; i++) {
					TileEntity te = tags.get(Config.inTags.get(i));
					NodeBlock.setPowered(world, te.getBlockState(), te.getPos(), inTest.get(i));
				}
			}
		}
	}
}
