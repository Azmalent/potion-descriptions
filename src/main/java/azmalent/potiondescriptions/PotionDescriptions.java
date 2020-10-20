package azmalent.potiondescriptions;

import net.fabricmc.api.ClientModInitializer;

public class PotionDescriptions implements ClientModInitializer {
    public static final String MODID = "potiondescriptions";

    @Override
    public void onInitializeClient() {
        ConfigManager.init();
    }
}
