package azmalent.potiondescriptions.platform;

import azmalent.potiondescriptions.ModConstants;
import azmalent.potiondescriptions.platform.services.IConfigHelper;
import azmalent.potiondescriptions.platform.services.IModIntegrationHelper;
import azmalent.potiondescriptions.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

public class Services {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final IConfigHelper CONFIG = load(IConfigHelper.class);
    public static final IModIntegrationHelper INTEGRATION = load(IModIntegrationHelper.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
            .findFirst()
            .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        ModConstants.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
