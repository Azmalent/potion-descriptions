package azmalent.potiondescriptions;

import net.minecraftforge.common.config.Config;
import static net.minecraftforge.common.config.Config.*;

@Config(modid=PotionDescriptions.MODID)
public class ModConfig {
    @Name("Sneaking Required")
    @Comment("Whether sneaking is required to display the tooltip.")
    public static boolean sneakRequired = true;

    @Name("Enable Sneaking Message")
    @Comment("Whether to display a message telling the player to sneak.")
    public static boolean sneakMessageEnabled = true;

    @Name("Log missing descriptions")
    @Comment("If true, potions without descriptions will be listed in the logs.")
    public static boolean loggingEnabled = false;
}
