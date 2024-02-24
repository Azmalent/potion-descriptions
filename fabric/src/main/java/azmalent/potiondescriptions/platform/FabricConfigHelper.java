package azmalent.potiondescriptions.platform;

import azmalent.potiondescriptions.PotionDescriptions;
import azmalent.potiondescriptions.platform.services.IConfigHelper;

public class FabricConfigHelper implements IConfigHelper {
    @Override
    public boolean shiftRequired() {
        return PotionDescriptions.CONFIG.get().shiftRequired;
    }

    @Override
    public boolean pressShiftMessageEnabled() {
        return PotionDescriptions.CONFIG.get().pressShiftMessageEnabled;
    }

    @Override
    public boolean showSourceMod() {
        return PotionDescriptions.CONFIG.get().showSourceMod;
    }

    @Override
    public boolean suspiciousStewEnabled() {
        return PotionDescriptions.CONFIG.get().suspiciousStewEnabled;
    }

    @Override
    public boolean loggingEnabled() {
        return PotionDescriptions.CONFIG.get().loggingEnabled;
    }
}
