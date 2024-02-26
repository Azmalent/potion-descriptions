package azmalent.potiondescriptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ModConstants {
    public static final String MODID = "potiondescriptions";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    //Config entries
    public static final class Config {
        public static final String SHIFT_REQUIRED = "Shift Required";
        public static final String SHIFT_REQUIRED_COMMENT = "Whether shift is required to display the tooltip.";

        public static final String PRESS_SHIFT_MESSAGE = "Enable 'Press Shift' message";
        public static final String PRESS_SHIFT_MESSAGE_COMMENT = "Whether to display a message telling the player to press shift.";

        public static final String SHOW_SOURCE_MOD = "Show Source Mod";
        public static final String SHOW_SOURCE_MOD_COMMENT = "If true, the source mod will be displayed for non-vanilla effects.";

        public static final String LOGGING = "Log Missing Descriptions";
        public static final String LOGGING_COMMENT = "If true, effects with missing descriptions will be listed in the logs.";

        public static final String SUSPICIOUS_STEW_TOOLTIP = "Suspicious Stew Tooltips";
        public static final String SUSPICIOUS_STEW_TOOLTIP_COMMENT = "Whether to display suspicious stew effects in the tooltip.";
    }
}
