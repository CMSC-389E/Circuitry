package cmsc389e.circuitry.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;

import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.Type;

public class Config {
    public static ConfigValue<String> cvsAccount, oneTimePassword;
    public static ConfigValue<Integer> projectNumber;

    public static String[] inTags, outTags;
    public static boolean[][] inTests, outTests;

    public static void load() throws IOException {
	try (InputStream in = new URL("https://cs.umd.edu/~abrassel/proj" + projectNumber.get() + "tests.txt")
		.openStream()) {
	    List<String> lines = IOUtils.readLines(in, (Charset) null);
	    String[] tags = lines.get(1).split("\t(?=o)", 2);
	    inTags = tags[0].split("\t");
	    outTags = tags[1].split("\t");
	    inTests = new boolean[lines.size() - 2][inTags.length];
	    outTests = new boolean[inTests.length][outTags.length];
	    for (int i = 0; i < inTests.length; i++) {
		tags = lines.get(i + 2).split("\t");
		for (int j = 0; j < tags.length; j++) {
		    boolean value = tags[j].equals("1");
		    if (j < inTags.length)
			inTests[i][j] = value;
		    else
			outTests[i][j - inTags.length] = value;
		}
	    }
	}
    }

    public static void register() {
	Builder builder = new Builder();
	cvsAccount = builder.define("CVS Account", "");
	oneTimePassword = builder.define("One-Time Password", "");
	projectNumber = builder.defineInRange("Project Number", 0, 0, 9);
	ModLoadingContext.get().registerConfig(Type.SERVER, builder.build());
    }
}
