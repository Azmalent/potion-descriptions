package azmalent.potiondescriptions;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class ModConfig {
    public static ForgeConfigSpec.BooleanValue shiftRequired;
    public static ForgeConfigSpec.BooleanValue pressShiftMessageEnabled;
    public static ForgeConfigSpec.BooleanValue showSourceModEnabled;
    public static ForgeConfigSpec.BooleanValue loggingEnabled;
    public static ForgeConfigSpec.BooleanValue suspiciousStewEnabled;

    public static ForgeConfigSpec SPEC;

    static {
        var builder = new ForgeConfigSpec.Builder();

        builder.push("Settings");

        shiftRequired = builder
            .comment("Whether shift is required to display the tooltip.")
            .define("Shift Required", true);

        pressShiftMessageEnabled = builder
            .comment("Whether to display a message telling the player to press shift.")
            .define("Enable 'Press Shift' Message", true);

        showSourceModEnabled = builder
            .comment("If true, the source mod will be displayed for non-vanilla effects.")
            .define("Show Source Mod", true);

        loggingEnabled = builder
            .comment("If true, effects with missing descriptions will be listed in the logs.")
            .define("Log Missing Descriptions", true);

        suspiciousStewEnabled = builder
            .comment("Whether to display suspicious stew effects in the tooltip.")
            .define("Suspicious Stew Tooltips", false);

        builder.pop();

        SPEC = builder.build();
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
