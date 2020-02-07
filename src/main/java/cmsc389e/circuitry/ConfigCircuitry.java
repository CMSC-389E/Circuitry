package cmsc389e.circuitry;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

/**
 * Configuration file for the mod.
 */
@Config(modid = Circuitry.MODID)
public class ConfigCircuitry {
    @Comment("Sets when tag numbers will start to cycle, exclusive. Automatically adjusted when loading tests.")
    @RangeInt(min = 0)
    @Name("Tag Limit")
    @RequiresMcRestart
    public static int tagLimit = 10;

}
