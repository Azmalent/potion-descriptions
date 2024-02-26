package azmalent.potiondescriptions.platform;

import azmalent.potiondescriptions.platform.services.IPlatformHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.Optional;

public class ForgePlatformHelper implements IPlatformHelper {
    @Override
    public boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getModName(String modid) {
        Optional<ModContainer> mod = (Optional<ModContainer>) ModList.get().getModContainerById(modid);
        if (mod.isPresent()) return mod.get().getModInfo().getDisplayName();

        return modid;
    }

    @Override
    public ResourceLocation getEffectRegistryName(MobEffect effect) {
        return ForgeRegistries.MOB_EFFECTS.getKey(effect);
    }

    @Override
    public Collection<MobEffect> getEffectRegistry() {
        return ForgeRegistries.MOB_EFFECTS.getValues();
    }
}
