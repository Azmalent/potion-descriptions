package azmalent.potiondescriptions;

import blue.endless.jankson.Comment;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = ModConstants.MODID)
public class ModConfig implements ConfigData {
    @Comment(ModConstants.Config.SHIFT_REQUIRED_COMMENT)
    public boolean shiftRequired = true;

    @Comment(ModConstants.Config.PRESS_SHIFT_MESSAGE_COMMENT)
    public boolean pressShiftMessageEnabled = true;

    @Comment(ModConstants.Config.SHOW_SOURCE_MOD_COMMENT)
    public boolean showSourceMod = true;

    @Comment(ModConstants.Config.LOGGING_COMMENT)
    public boolean loggingEnabled = false;

    @Comment(ModConstants.Config.SUSPICIOUS_STEW_TOOLTIP_COMMENT)
    public boolean suspiciousStewEnabled = false;
}
