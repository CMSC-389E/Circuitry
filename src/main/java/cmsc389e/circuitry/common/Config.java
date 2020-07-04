package cmsc389e.circuitry.common;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class Config {
	public enum Key {
		LIGHT("Light", "", (path, builder) -> builder.defineInRange(path, 15, 0, 15)),
		POWER("Power", "", (path, builder) -> builder.defineInRange(path, 15, 0, 15)),
		TESTS("Tests", "", (path, builder) -> builder.define(path, "tests.txt"));

		private final BiFunction<String, Builder, ConfigValue<?>> build;
		private final String comment;
		private final String path;

		private Key(String path, String comment, BiFunction<String, Builder, ConfigValue<?>> build) {
			this.path = path;
			this.comment = comment;
			this.build = build;
		}

		@Override
		public String toString() {
			return path;
		}
	}

	public static final Pair<Config, ForgeConfigSpec> SPEC_PAIR = new Builder().configure(Config::new);

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
		return (T) SPEC_PAIR.getLeft().map.get(key).get();
	}

	public static String getString(Key key) {
		return get(key);
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
		((ConfigValue<T>) SPEC_PAIR.getLeft().map.get(key)).set(value);
	}

	private final Map<Key, ConfigValue<?>> map;

	private Config(Builder builder) {
		map = new HashMap<>();
		for (Key key : Key.values()) {
			builder.comment(key.comment);
			map.put(key, key.build.apply(key.toString(), builder));
		}
	}
}