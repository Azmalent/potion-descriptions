package azmalent.potiondescriptions.util;

import azmalent.potiondescriptions.ConfigManager;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public class TooltipUtil {
    public static void addDescriptionTooltip(List<StatusEffectInstance> effects, List<Text> tooltip) {
        if (effects.isEmpty()) return;

        KeyBinding sneakButton = MinecraftClient.getInstance().options.keySneak;
        int keyCode = ((IKeyBindingMixin) sneakButton).getBoundKey().getCode();
        boolean sneaking = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), keyCode);

        if (!sneaking && ConfigManager.config.sneakingRequired && ConfigManager.config.sneakingMessageEnabled) {
            tooltip.add(new LiteralText(I18n.translate("tooltip.potiondescriptions.sneakToView", I18n.translate(sneakButton.getBoundKeyTranslationKey()))));
        }
        else if (sneaking || !ConfigManager.config.sneakingRequired) {
            for (StatusEffectInstance effectInstance : effects) {
                StatusEffect effect = effectInstance.getEffectType();

                Formatting effectFormat = effect.isBeneficial() ? Formatting.BLUE : Formatting.RED;
                Text effectName = new LiteralText(I18n.translate(effect.getTranslationKey())).formatted(effectFormat);
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

    public static void addEffectTooltip(List<StatusEffectInstance> effects, List<Text> list) {
        List<Pair<EntityAttribute, EntityAttributeModifier>> attributeMods = Lists.newArrayList();

        if (effects.isEmpty()) {
            list.add(new TranslatableText("effect.none").formatted(Formatting.GRAY));
        } else {
            for (StatusEffectInstance statusEffectInstance : effects) {
                TranslatableText mutableText = new TranslatableText(statusEffectInstance.getTranslationKey());
                StatusEffect statusEffect = statusEffectInstance.getEffectType();
                Map<EntityAttribute, EntityAttributeModifier> map = statusEffect.getAttributeModifiers();
                if (!map.isEmpty()) {

                    for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : map.entrySet()) {
                        EntityAttributeModifier value = entry.getValue();
                        EntityAttributeModifier operation = new EntityAttributeModifier(value.getName(), statusEffect.adjustModifierAmount(statusEffectInstance.getAmplifier(), value), value.getOperation());
                        attributeMods.add(new Pair(entry.getKey(), operation));
                    }
                }

                if (statusEffectInstance.getAmplifier() > 0) {
                    mutableText = new TranslatableText("potion.withAmplifier", mutableText, new TranslatableText("potion.potency." + statusEffectInstance.getAmplifier()));
                }

                if (statusEffectInstance.getDuration() > 20) {
                    mutableText = new TranslatableText("potion.withDuration", mutableText, StatusEffectUtil.durationToString(statusEffectInstance, 1.0f));
                }
                list.add(mutableText.formatted(statusEffect.getType().getFormatting()));
            }
        }

        if (!attributeMods.isEmpty()) {
            list.add(LiteralText.EMPTY);
            list.add((new TranslatableText("potion.whenDrank")).formatted(Formatting.DARK_PURPLE));

            for (Pair<EntityAttribute, EntityAttributeModifier> pair : attributeMods) {
                EntityAttributeModifier entityAttributeModifier3 = pair.getSecond();
                double d = entityAttributeModifier3.getValue();
                double g;
                if (entityAttributeModifier3.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_BASE && entityAttributeModifier3.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
                    g = entityAttributeModifier3.getValue();
                } else {
                    g = entityAttributeModifier3.getValue() * 100.0D;
                }

                if (d > 0.0D) {
                    list.add((new TranslatableText("attribute.modifier.plus." + entityAttributeModifier3.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(g), new TranslatableText(pair.getFirst().getTranslationKey()))).formatted(Formatting.BLUE));
                } else if (d < 0.0D) {
                    g *= -1.0D;
                    list.add((new TranslatableText("attribute.modifier.take." + entityAttributeModifier3.getOperation().getId(), ItemStack.MODIFIER_FORMAT.format(g), new TranslatableText(pair.getFirst().getTranslationKey()))).formatted(Formatting.RED));
                }
            }
        }
    }
}
