package cmsc389e.circuitry.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;

import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public class Config {
	public static ConfigValue<String> cvsAccount, oneTimePassword;
	public static ConfigValue<Integer> projectNumber;

	public static String[] inTags, outTags;
	public static int[][] inTests, outTests;
	public static boolean loaded;

	public static void load() throws IOException {
		loaded = false;
		try (InputStream in = new FastBufferedInputStream(
				new URL("https://cmsc-389e.github.io/tests/proj" + projectNumber.get() + ".txt").openStream())) {
			List<String> lines = IOUtils.readLines(in, (Charset) null);
			String[] tags = lines.get(1).split("\t(?=o)", 2);

			inTags = tags[0].split("\t");
			outTags = tags[1].split("\t");
			inTests = new int[lines.size() - 2][];
			outTests = new int[inTests.length][];
			for (int i = 0; i < inTests.length; i++) {
				int[] tests = Arrays.stream(lines.get(i + 2).split("\t")).mapToInt(Integer::parseUnsignedInt).toArray();
				inTests[i] = Arrays.copyOf(tests, inTags.length);
				outTests[i] = Arrays.copyOfRange(tests, inTags.length, tests.length);
			}
			loaded = true;
		}
	}

	public static void register() {
		Builder builder = new Builder();
		cvsAccount = builder.define("CVS Account", "");
		oneTimePassword = builder.define("One-Time Password", "");
		projectNumber = builder.defineInRange("Project Number", 0, 0, Integer.MAX_VALUE);
		ModLoadingContext.get().registerConfig(Type.SERVER, builder.build());
	}
}
