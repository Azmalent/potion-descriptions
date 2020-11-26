package azmalent.potiondescriptions;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ModConfig {
    public static ForgeConfigSpec.BooleanValue sneakRequired;
    public static ForgeConfigSpec.BooleanValue sneakMessageEnabled;
    public static ForgeConfigSpec.BooleanValue loggingEnabled;
    public static ForgeConfigSpec.BooleanValue suspiciousStewEnabled;

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec SPEC;

    static {
        BUILDER.push("Settings");
        sneakRequired = BUILDER
                .comment("Whether sneaking is required to display the tooltip.")
                .define("Sneaking Required", true);
        sneakMessageEnabled = BUILDER
                .comment("Whether to display a message telling the player to sneak.")
                .define("Enable Sneaking Message", true);
        loggingEnabled = BUILDER
                .comment("If true, potions without descriptions will be listed in the logs.")
                .define("Log Missing Descriptions", false);
        suspiciousStewEnabled = BUILDER
                .comment("Whether to display suspicious stew effects in the tooltip.")
                .define("Suspicious Stew Tooltips", false);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static void init(Path filePath) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(filePath)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        SPEC.setConfig(configData);
    }
}
