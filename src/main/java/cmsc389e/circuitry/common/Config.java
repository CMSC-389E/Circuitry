package cmsc389e.circuitry.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.plexus.util.FastMap;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig.Type;

@EventBusSubscriber
public class Config {
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	private static @interface Value {
		public String comment() default "";

		public Type type();
	}

	@Value(type = Type.COMMON, comment = "The light level of Nodes. Zero is no light and fifteen is the maximum brightness.")
	public static int light = 15;

	@Value(type = Type.COMMON, comment = "The power level of In Nodes. The number determines the number of Redstone powered from the In Node. Zero is no power and fifteen is maximum power.")
	public static int power = 15;

	@Value(type = Type.COMMON, comment = "Specifies the URL to download test files from. Load will not work if the URL is malformed or does not have exactly one format specifier, %d.")
	public static String testsURL = "https://cs.umd.edu/~abrassel/proj%dtests.txt";

	@Value(type = Type.SERVER, comment = "The list of currently loaded tags for In Nodes.")
	public static List<String> inTags = new ArrayList<>();

	@Value(type = Type.SERVER, comment = "The list of currently loaded tags for Out Nodes.")
	public static List<String> outTags = new ArrayList<>();

	@Value(type = Type.SERVER, comment = "A two-dimensional list representing the currently loaded project test values for In Nodes.")
	public static List<List<Boolean>> inTests = new ArrayList<>();

	@Value(type = Type.SERVER, comment = "A two-dimensional list representing the currently loaded project test values for Out Nodes.")
	public static List<List<Boolean>> outTests = new ArrayList<>();

	private static final Map<Field, ConfigValue<Object>> values = new FastMap<>();

	public static void register() throws IllegalAccessException {
		EnumMap<Type, Builder> builders = new EnumMap<>(Type.class);
		for (Field field : Config.class.getFields()) {
			Value value = field.getAnnotation(Value.class);
			if (value != null) {
				String path = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(field.getName()), ' ');
				path = Character.toUpperCase(path.charAt(0)) + path.substring(1);
				values.put(field, builders.computeIfAbsent(value.type(), type -> new Builder()).comment(value.comment())
						.define(path, field.get(null)));
			}
		}
		ModLoadingContext context = ModLoadingContext.get();
		builders.forEach((type, builder) -> context.registerConfig(type, builder.build()));
	}

	public static void sync() {
		values.forEach((field, value) -> {
			try {
				value.set(field.get(null));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}
}