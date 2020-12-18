package azmalent.potiondescriptions;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.IForgeRegistryEntry;
import vazkii.botania.api.brew.IBrewItem;
import xreliquary.init.ModItems;
import xreliquary.items.PotionItemBase;
import xreliquary.util.potions.XRPotionHelper;

import java.util.*;

import static net.minecraftforge.common.util.Constants.*;

@OnlyIn(Dist.CLIENT)
public class TooltipHandler {
    public static boolean BOTANIA_LOADED = ModList.get().isLoaded("botania");
    public static boolean RELIQUARY_LOADED = ModList.get().isLoaded("xreliquary");

    @SubscribeEvent
    public static void onTooltipDisplayed(final ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEmpty()) return;

        Item item = itemStack.getItem();

        List<EffectInstance> effects = null;
        if (itemStack.hasTag() && itemStack.getTag().contains("Potion")) {
            effects = PotionUtils.getEffectsFromStack(itemStack);
        }
        else if (item == Items.SUSPICIOUS_STEW && ModConfig.suspiciousStewEnabled.get()) {
            effects = getSuspiciousStewEffects(itemStack);
            addEffectsTooltip(effects, event.getToolTip());
        }
        else if (BOTANIA_LOADED && item instanceof IBrewItem) {
            effects = ((IBrewItem) item).getBrew(itemStack).getPotionEffects(itemStack);
        }
        else if (RELIQUARY_LOADED && (item == ModItems.POTION_ESSENCE || item == ModItems.TIPPED_ARROW || item instanceof PotionItemBase)) {
            effects = XRPotionHelper.getPotionEffectsFromStack(itemStack);
        }

        if (effects != null) {
            addPotionTooltip(effects, event.getToolTip());
        }
    }

    public static void addPotionTooltip(List<EffectInstance> effectList, List<ITextComponent> tooltip) {
        Set<EffectInstance> effects = new HashSet(effectList);
        if (effects.isEmpty()) return;

        KeyBinding sneakButton = Minecraft.getInstance().gameSettings.keyBindSneak;
        int keyCode = sneakButton.getKey().getKeyCode();
        boolean sneaking = InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), keyCode);

        if (!sneaking && ModConfig.sneakRequired.get() && ModConfig.sneakMessageEnabled.get()) {
            tooltip.add(new StringTextComponent(I18n.format("tooltip.potiondescriptions.sneakToView", I18n.format(sneakButton.getTranslationKey()))));
        }
        else if (sneaking || !ModConfig.sneakRequired.get()) {
            for (EffectInstance effectInstance : effects) {
                Effect effect = effectInstance.getPotion();

                ITextComponent description = getEffectDescription(effect);
                TextFormatting effectFormat = effect.isBeneficial() ? TextFormatting.BLUE : TextFormatting.RED;
                String effectName = I18n.format(effect.getName());

                tooltip.add(new StringTextComponent(I18n.format("tooltip.potiondescriptions.effect", effectFormat, effectName)));
                tooltip.add(description != null ? description : new TranslationTextComponent("tooltip.potiondescriptions.missingDescription", "description." + effect.getName()));
                tooltip.add(new StringTextComponent(I18n.format("tooltip.potiondescriptions.sourceMod", getModName(effect))));
            }
        }
    }

    private static String getModName(IForgeRegistryEntry entry) {
        String modid = entry.getRegistryName().getNamespace();
        Optional<ModContainer> mod = (Optional<ModContainer>) ModList.get().getModContainerById(modid);
        if (mod.isPresent()) return mod.get().getModInfo().getDisplayName();

        return modid;
    }

    private static ITextComponent getEffectDescription(Effect effect) {
        String translationKey = "description." + effect.getName();
        return I18n.hasKey(translationKey) ? new TranslationTextComponent(translationKey) : null;
    }

    private static void addEffectsTooltip(List<EffectInstance> effects, List<ITextComponent> tooltip) {
        IFormattableTextComponent noEffect = (new TranslationTextComponent("effect.none")).mergeStyle(TextFormatting.GRAY);

        List<Pair<Attribute, AttributeModifier>> list1 = Lists.newArrayList();
        if (effects.isEmpty()) tooltip.add(noEffect);
        else {
            for(EffectInstance effectInstance : effects) {
                IFormattableTextComponent line = new TranslationTextComponent(effectInstance.getEffectName());
                Effect effect = effectInstance.getPotion();
                Map<Attribute, AttributeModifier> map = effect.getAttributeModifierMap();
                if (!map.isEmpty()) {
                    for(Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                        AttributeModifier attributemodifier = entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), effect.getAttributeModifierAmount(effectInstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                        list1.add(new Pair<>(entry.getKey(), attributemodifier1));
                    }
                }

                if (effectInstance.getAmplifier() > 0) {
                    line = new TranslationTextComponent("potion.withAmplifier", line, new TranslationTextComponent("potion.potency." + effectInstance.getAmplifier()));
                }
                else if (effectInstance.getDuration() > 20) {
                    line = new TranslationTextComponent("potion.withDuration", line, EffectUtils.getPotionDurationString(effectInstance, 1));
                }

                tooltip.add(line.mergeStyle(effect.getEffectType().getColor()));
            }
        }

        if (!list1.isEmpty()) {
            tooltip.add(StringTextComponent.EMPTY);
            tooltip.add((new TranslationTextComponent("potion.whenDrank")).mergeStyle(TextFormatting.DARK_PURPLE));

            for(Pair<Attribute, AttributeModifier> pair : list1) {
                AttributeModifier attributemodifier2 = pair.getSecond();
                double d0 = attributemodifier2.getAmount();
                double d1;
                if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    d1 = attributemodifier2.getAmount();
                } else {
                    d1 = attributemodifier2.getAmount() * 100.0D;
                }

                if (d0 > 0.0D) {
                    tooltip.add((new TranslationTextComponent("attribute.modifier.plus." + attributemodifier2.getOperation().getId(), ItemStack.DECIMALFORMAT.format(d1), new TranslationTextComponent(pair.getFirst().getAttributeName()))).mergeStyle(TextFormatting.BLUE));
                } else if (d0 < 0.0D) {
                    d1 = d1 * -1.0D;
                    tooltip.add((new TranslationTextComponent("attribute.modifier.take." + attributemodifier2.getOperation().getId(), ItemStack.DECIMALFORMAT.format(d1), new TranslationTextComponent(pair.getFirst().getAttributeName()))).mergeStyle(TextFormatting.RED));
                }
            }
        }
    }

    private static List<EffectInstance> getSuspiciousStewEffects(ItemStack stew) {
        List<EffectInstance> effectInstances = new ArrayList<>();

        CompoundNBT tag = stew.getTag();
        if (tag != null && tag.contains("Effects", NBT.TAG_LIST)) {
            ListNBT list = tag.getList("Effects", NBT.TAG_COMPOUND);
            for(int i = 0; i < list.size(); i++) {
                CompoundNBT effect = list.getCompound(i);
                byte id = effect.getByte("EffectId");
                int duration = effect.contains("EffectDuration", NBT.TAG_INT) ? effect.getInt("EffectDuration") : 160;

                if (Effect.get(id) != null) {
                    effectInstances.add(new EffectInstance(Effect.get(id), duration));
                }
            }
        }

        return effectInstances;
    }
}
