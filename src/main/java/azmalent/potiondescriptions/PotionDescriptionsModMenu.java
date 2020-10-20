package azmalent.potiondescriptions;

import azmalent.potiondescriptions.gui.ConfigScreen;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class PotionDescriptionsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory getModConfigScreenFactory() {
        return ConfigScreen::new;
    }
}
