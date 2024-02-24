package azmalent.potiondescriptions.platform;

import azmalent.potiondescriptions.platform.services.IPlatformHelper;
import net.fabricmc.fabric.impl.registry.sync.FabricRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import java.util.Collection;
import java.util.Optional;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    @Override
    public String getModName(String modid) {
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(modid);
        if (container.isPresent()) {
            return container.get().getMetadata().getName();
        }

        return modid;
    }

    @Override
    public ResourceLocation getEffectRegistryName(MobEffect effect) {
        return Registry.MOB_EFFECT.getKey(effect);
    }

    @Override
    public Collection<MobEffect> getEffectRegistry() {
        return Registry.MOB_EFFECT.stream().toList();
    }
}
