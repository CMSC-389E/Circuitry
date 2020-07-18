package cmsc389e.circuitry.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public class Config {
	public static class Value<T> {
		private final String path, comment;
		private final T defaultValue;
		private ConfigValue<T> value;

		private Value(String path, T defaultValue, String comment) {
			this.path = path;
			this.defaultValue = defaultValue;
			this.comment = comment;
		}

		private void build(Builder builder) {
			value = builder.comment(comment).define(path, defaultValue);
		}

		public T get() {
			return value.get();
		}

		public T set(T value) {
			this.value.set(value);
			return value;
		}
	}

	public static final Value<Integer> LIGHT = new Value<>("Light", 15,
			"The light level of Nodes. Zero is no light and fifteen is the maximum brightness."),
			POWER = new Value<>("Power", 15,
					"The power level of In Nodes. The number determines the number of Redstone powered from the In Node. Zero is no power and fifteen is maximum power.");
	public static final Value<String> TESTS_URL = new Value<>("Tests URL",
			"https://cs.umd.edu/~abrassel/proj%dtests.txt",
			"Specifies the URL to download test files from. Load will not work if the URL is malformed or does not have exactly one format specifier, %d.");
	public static final Value<List<String>> IN_TAGS = new Value<>("In Tags", new ArrayList<>(),
			"The list of currently loaded tags for In Nodes."),
			OUT_TAGS = new Value<>("Out Tags", new ArrayList<>(), "The list of currently loaded tags for Out Nodes.");
	public static final Value<List<List<Boolean>>> IN_TESTS = new Value<>("In Tests", new ArrayList<>(),
			"A two-dimensional list representing the currently loaded project test values for In Nodes."),
			OUT_TESTS = new Value<>("Out Tests", new ArrayList<>(),
					"A two-dimensional list representing the currently loaded project test values for Out Nodes.");

	public static void register() {
		ModLoadingContext context = ModLoadingContext.get();
		register(context, Type.COMMON, LIGHT, POWER, TESTS_URL);
		register(context, Type.SERVER, IN_TAGS, OUT_TAGS, IN_TESTS, OUT_TESTS);
	}

	private static void register(ModLoadingContext context, Type type, Value<?>... values) {
		Builder builder = new Builder();
		for (Value<?> value : values)
			value.build(builder);
		context.registerConfig(type, builder.build());
	}
}