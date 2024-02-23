package azmalent.potiondescriptions.platform;

import azmalent.potiondescriptions.ModConfig;
import azmalent.potiondescriptions.platform.services.IConfigHelper;

public class ForgeConfigHelper implements IConfigHelper {
    @Override
    public boolean shiftRequired() {
        return ModConfig.shiftRequired.get();
    }

    @Override
    public boolean pressShiftMessageEnabled() {
        return ModConfig.pressShiftMessageEnabled.get();
    }

    @Override
    public boolean showSourceMod() {
        return ModConfig.showSourceMod.get();
    }

    @Override
    public boolean suspiciousStewEnabled() {
        return ModConfig.suspiciousStewEnabled.get();
    }

    @Override
    public boolean loggingEnabled() {
        return ModConfig.loggingEnabled.get();
    }
}
