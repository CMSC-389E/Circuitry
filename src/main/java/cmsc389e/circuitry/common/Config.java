package cmsc389e.circuitry.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig.Type;

@EventBusSubscriber
public class Config {
	public static class Value<T> {
		private final String path;
		private final String comment;
		private final BiFunction<String, Builder, ConfigValue<T>> build;
		private ConfigValue<T> value;

		private Value(String path, String comment, BiFunction<String, Builder, ConfigValue<T>> build) {
			this.path = path;
			this.comment = comment;
			this.build = build;
		}

		private void build(Builder builder) {
			value = build.apply(path, builder.comment(comment));
		}

		public T get() {
			return value.get();
		}

		public void set(T value) {
			this.value.set(value);
		}
	}

	public static final Value<Integer> LIGHT = new Value<>("Light",
			"The light level of Nodes. Zero is no light and fifteen is the maximum brightness.",
			(path, builder) -> builder.defineInRange(path, 15, 0, 15)),
			POWER = new Value<>("Power",
					"The power level of In Nodes. The number determines the number of Redstone powered from the In Node. Zero is no power and fifteen is maximum power.",
					(path, builder) -> builder.defineInRange(path, 15, 0, 15));
	public static final Value<String> TEST_URL = new Value<>("Test URL",
			"Specifies the URL to download test files from. Load will not work if the URL is malformed or does not have exactly one format specifier, %d.",
			(path, builder) -> builder.define(path, "https://cs.umd.edu/~abrassel/proj%dtests.txt"));
	public static final Value<List<String>> IN_TAGS = new Value<>("In Tags",
			"The list of currently loaded tags for In Nodes.",
			(path, builder) -> builder.define(path, ArrayList::new, obj -> true)),
			OUT_TAGS = new Value<>("Out Tags", "The list of currently loaded tags for Out Nodes.",
					(path, builder) -> builder.define(path, ArrayList::new, obj -> true));
	public static final Value<List<List<Boolean>>> IN_TESTS = new Value<>("In Tests",
			"A two-dimensional list representing the currently loaded project test values for In Nodes.",
			(path, builder) -> builder.define(path, ArrayList::new, obj -> true)),
			OUT_TESTS = new Value<>("Out Tests",
					"A two-dimensional list representing the currently loaded project test values for Out Nodes.",
					(path, builder) -> builder.define(path, ArrayList::new, obj -> true));

	public static void register() {
		ModLoadingContext context = ModLoadingContext.get();
		register(context, Type.COMMON, LIGHT, POWER, TEST_URL);
		register(context, Type.SERVER, IN_TAGS, OUT_TAGS, IN_TESTS, OUT_TESTS);
	}

	private static void register(ModLoadingContext context, Type type, Value<?>... values) {
		Builder builder = new Builder();
		for (Value<?> value : values)
			value.build(builder);
		context.registerConfig(type, builder.build());
	}
}