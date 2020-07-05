package cmsc389e.circuitry.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig.Type;

@EventBusSubscriber
public class Config {
	public enum Key {
		LIGHT(Type.COMMON, "Light", "The light level of Nodes. Zero is no light and fifteen is the maximum brightness.",
				(path, builder) -> builder.defineInRange(path, 15, 0, 15)),
		POWER(Type.COMMON, "Power",
				"The power level of Nodes. The number determines the number of Redstone powered from the Node. Zero is no power and fifteen is maximum power.",
				(path, builder) -> builder.defineInRange(path, 15, 0, 15)),
		IN_TAGS(Type.SERVER, "In Tags", "The list of currently loaded tags for In Nodes.",
				(path, builder) -> builder.defineList(path, ArrayList::new, obj -> true)),
		OUT_TAGS(Type.SERVER, "Out Tags", "The list of currently loaded tags for Out Nodes.",
				(path, builder) -> builder.defineList(path, ArrayList::new, obj -> true)),
		TESTS(Type.SERVER, "Tests", "A two-dimensional list representing the currently loaded project tests.",
				(path, builder) -> builder.defineList(path, ArrayList::new, obj -> true));

		private final Type type;
		private final String path;
		private final String comment;
		private final BiFunction<String, Builder, ConfigValue<?>> build;

		private Key(Type type, String path, String comment, BiFunction<String, Builder, ConfigValue<?>> build) {
			this.type = type;
			this.path = path;
			this.comment = comment;
			this.build = build;
		}

		@Override
		public String toString() {
			return path;
		}
	}

	private static final Map<Key, ConfigValue<?>> map = new HashMap<>();

	/**
	 * Gets the current value for a {@link Key}. The type of the returned value is
	 * dynamically cast in order to make it easier to store it in variables. If the
	 * returned value is stored in a different type than the {@link Key} is
	 * associated with, a {@link ClassCastException} may be thrown.
	 *
	 * @param <T> the type of the returned {@link Config} value
	 * @param key the {@link Key} of the returned {@link Config} value
	 * @return the current value for the {@link Key}
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(Key key) {
		return (T) map.get(key).get();
	}

	public static String getString(Key key) {
		return get(key);
	}

	public static void register() {
		ModLoadingContext context = ModLoadingContext.get();
		for (Type type : Type.values()) {
			Builder builder = new Builder();
			boolean empty = true;
			for (Key key : Key.values())
				if (key.type == type) {
					builder.comment(key.comment);
					map.put(key, key.build.apply(key.toString(), builder));
					empty = false;
				}
			if (!empty)
				context.registerConfig(type, builder.build());
		}
	}

	/**
	 * Sets the current value for a {@link Key} to the value. If the type of the
	 * value differs from the type associated with the {@link Key}, a
	 * {@link ClassCastException} may be thrown.
	 *
	 * @param <T>   the type associated with the {@link Key}
	 * @param key   the {@link Key} of the {@link Config} value that will be changed
	 * @param value the new value for the {@link Key}
	 */
	@SuppressWarnings("unchecked")
	public static <T> void set(Key key, T value) {
		((ConfigValue<T>) map.get(key)).set(value);
	}
}