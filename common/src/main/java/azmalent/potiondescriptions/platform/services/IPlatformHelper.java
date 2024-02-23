package azmalent.potiondescriptions.platform.services;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import java.util.Collection;

public interface IPlatformHelper {
    boolean isModLoaded(String modid);
    String getModName(String modid);
    ResourceLocation getEffectRegistryName(MobEffect effect);
    Collection<MobEffect> getEffectRegistry();
}
