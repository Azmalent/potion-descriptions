package azmalent.potiondescriptions.util;

import azmalent.potiondescriptions.ConfigManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class TooltipUtil {
    public static void addPotionTooltip(List<StatusEffectInstance> effects, List<Text> tooltip) {
        if (effects.isEmpty()) return;

        KeyBinding sneakButton = MinecraftClient.getInstance().options.keySneak;
        int keyCode = sneakButton.getDefaultKey().getCode();
        boolean sneaking = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), keyCode);

        if (!sneaking && ConfigManager.config.sneakingRequired && ConfigManager.config.sneakingMessageEnabled) {
            tooltip.add(new TranslatableText("tooltip.potiondescriptions.sneakToView", I18n.translate(sneakButton.getBoundKeyTranslationKey())));
        }
        else if (sneaking || !ConfigManager.config.sneakingRequired) {
            for (StatusEffectInstance effectInstance : effects) {
                StatusEffect effect = effectInstance.getEffectType();

                Formatting effectFormat = effect.isBeneficial() ? Formatting.BLUE : Formatting.RED;
                Text effectName = new TranslatableText(effect.getTranslationKey()).formatted(effectFormat);
                tooltip.add(new TranslatableText("tooltip.potiondescriptions.effect").append(effectName));

                String descriptionKey = effect.getTranslationKey() + ".description";
                TranslatableText description = getEffectDescription(descriptionKey);
                tooltip.add(description != null ? description : new TranslatableText("tooltip.potiondescriptions.missingDescription", descriptionKey));

                if (ConfigManager.config.showSourceMod) {
                    Text modName = new LiteralText(getModName(effect)).formatted(Formatting.ITALIC);
                    tooltip.add(new TranslatableText("tooltip.potiondescriptions.sourceMod").append(modName));
                }
            }
        }
    }

    private static String getModName(StatusEffect effect) {
        String modid = Registry.STATUS_EFFECT.getId(effect).getNamespace();
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(modid);
        if (container.isPresent()) {
            return container.get().getMetadata().getName();
        }

        return modid;
    }

    private static TranslatableText getEffectDescription(String translationKey) {
        if (I18n.hasTranslation(translationKey)) {
            return new TranslatableText(translationKey);
        }

        return null;
    }
}
