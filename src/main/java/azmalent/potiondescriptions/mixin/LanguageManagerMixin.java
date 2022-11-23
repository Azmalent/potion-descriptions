package azmalent.potiondescriptions.mixin;

import azmalent.potiondescriptions.ModConfig;
import azmalent.potiondescriptions.PotionDescriptions;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(LanguageManager.class)
public class LanguageManagerMixin {
    @Inject(method = "onResourceManagerReload", at = @At("RETURN"))
    private void onReload(ResourceManager resourceManager, CallbackInfo ci) {
        if (ModConfig.loggingEnabled.get()) {
            Collection<MobEffect> effects = ForgeRegistries.MOB_EFFECTS.getValues();
            for (MobEffect effect : effects) {
                String translationKey = "description." + effect.getDescriptionId();
                if (!I18n.exists(translationKey)) {
                    PotionDescriptions.LOGGER.warn(String.format(
                        "Missing description for effect '%s' (expected translation key: %s)",
                        effect.getRegistryName(), translationKey)
                    );
                }
            }
        }
    }
}
