package cmsc389e.circuitry.common;

import java.io.File;

import cmsc389e.circuitry.Circuitry;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;

/**
 * Configuration file for the mod.
 */
@Config(modid = Circuitry.MODID)
public class ConfigCircuitry {
    @Comment("List of tags that inputs can cycle through. If this list is empty, displayed tags will be integers. Tests will expect every tag in this list to be used in the world with no two inputs having the same tag.")
    @Name("Input Tags")
    public static String[] inTags = new String[0];
    @Comment("List of tags that outputs can cycle through. If this list is empty, displayed tags will be integers. Tests will expect every tag in this list to be used in the world with no two outputs having the same tag.")
    @Name("Output Tags")
    public static String[] outTags = new String[0];
    @Comment("Server whence the submit JAR and the tests file are retrieved.")
    @Name("Homework Server")
    public static String server = "https://cs.umd.edu/~abrassel/";
    @Comment("Path of the submit JAR used for submitting projects. Tests should be reloaded after changing this field.")
    @Name("Submit JAR Path")
    public static String submit = "submit" + File.separatorChar + "submit.jar";
    @Comment("Path of the test logs file.")
    @Name("Test Logs Path")
    public static String testLogs = "test_logs.txt";
    @Comment("Path of the tests file used for running tests. Tests should be reloade after changing this field.")
    @Name("Tests Path")
    public static String tests = "tests.txt";

    /**
     * Syncs changed values in the configuration to the file system. Otherwise,
     * changed values will be overwritten the next time the configuration file is
     * loaded.
     */
    public static void sync() {
	ConfigManager.sync(Circuitry.MODID, Type.INSTANCE);
    }
}