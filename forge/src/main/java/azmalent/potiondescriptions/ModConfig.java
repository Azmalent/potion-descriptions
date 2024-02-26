package azmalent.potiondescriptions;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class ModConfig {
    public static ForgeConfigSpec.BooleanValue shiftRequired;
    public static ForgeConfigSpec.BooleanValue pressShiftMessageEnabled;
    public static ForgeConfigSpec.BooleanValue showSourceMod;
    public static ForgeConfigSpec.BooleanValue suspiciousStewEnabled;

    public static ForgeConfigSpec SPEC;

    static {
        var builder = new ForgeConfigSpec.Builder();

        builder.push("Settings");

        shiftRequired = builder
            .comment(ModConstants.Config.SHIFT_REQUIRED_COMMENT)
            .define(ModConstants.Config.SHIFT_REQUIRED, true);

        pressShiftMessageEnabled = builder
            .comment(ModConstants.Config.PRESS_SHIFT_MESSAGE_COMMENT)
            .define(ModConstants.Config.PRESS_SHIFT_MESSAGE, true);

        showSourceMod = builder
            .comment(ModConstants.Config.SHOW_SOURCE_MOD_COMMENT)
            .define(ModConstants.Config.SHOW_SOURCE_MOD, true);

        suspiciousStewEnabled = builder
            .comment(ModConstants.Config.SUSPICIOUS_STEW_TOOLTIP_COMMENT)
            .define(ModConstants.Config.SUSPICIOUS_STEW_TOOLTIP, false);

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
