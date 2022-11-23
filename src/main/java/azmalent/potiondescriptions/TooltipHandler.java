package azmalent.potiondescriptions;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import reliquary.init.ModItems;
import reliquary.items.PotionItemBase;
import reliquary.util.potions.XRPotionHelper;
import vazkii.botania.api.brew.IBrewItem;

import java.util.*;

@SuppressWarnings({"unchecked", "ConstantConditions"})
@OnlyIn(Dist.CLIENT)
public class TooltipHandler {
    public static boolean BOTANIA_LOADED = ModList.get().isLoaded("botania");
    public static boolean RELIQUARY_LOADED = ModList.get().isLoaded("reliquary");

    @SubscribeEvent
    public static void onTooltipDisplayed(final ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEmpty()) return;

        Item item = itemStack.getItem();

        List<MobEffectInstance> effects = null;
        if (itemStack.hasTag() && itemStack.getTag().contains("Potion")) {
            effects = PotionUtils.getMobEffects(itemStack);
        }
        else if (item == Items.SUSPICIOUS_STEW && ModConfig.suspiciousStewEnabled.get()) {
            effects = getSuspiciousStewEffects(itemStack);
            addEffectsTooltip(effects, event.getToolTip());
        }
        else if (BOTANIA_LOADED && item instanceof IBrewItem) {
            effects = ((IBrewItem) item).getBrew(itemStack).getPotionEffects(itemStack);
        }
        else if (RELIQUARY_LOADED && (item == ModItems.POTION_ESSENCE.get() || item == ModItems.TIPPED_ARROW.get() || item instanceof PotionItemBase)) {
            effects = XRPotionHelper.getPotionEffectsFromStack(itemStack);
        }

        if (effects != null) {
            addPotionTooltip(effects, event.getToolTip());
        }
    }

    public static void addPotionTooltip(List<MobEffectInstance> effectList, List<Component> tooltip) {
        Set<MobEffectInstance> effects = Sets.newHashSet(effectList);
        if (effects.isEmpty()) return;

        boolean sneaking = Screen.hasShiftDown();

        if (!sneaking && ModConfig.shiftRequired.get() && ModConfig.pressShiftMessageEnabled.get()) {
            tooltip.add(new TextComponent(I18n.get("tooltip.potiondescriptions.sneakToView")));
        } else if (sneaking || !ModConfig.shiftRequired.get()) {
            for (MobEffectInstance effectInstance : effects) {
                MobEffect effect = effectInstance.getEffect();

                Component description = getEffectDescription(effect);
                ChatFormatting effectFormat = effect.isBeneficial() ? ChatFormatting.BLUE : ChatFormatting.RED;
                String effectName = I18n.get(effect.getDescriptionId());

                tooltip.add(new TextComponent(I18n.get("tooltip.potiondescriptions.effect", effectFormat, effectName)));
                tooltip.add(description != null ? description : new TranslatableComponent("tooltip.potiondescriptions.missingDescription", "description." + effect.getDescriptionId()));

                var modid = effect.getRegistryName().getNamespace();
                if (ModConfig.showSourceModEnabled.get() && !modid.equals("minecraft")) {
                    tooltip.add(new TextComponent(I18n.get("tooltip.potiondescriptions.sourceMod", getModName(modid))));
                }
            }
        }
    }

    private static String getModName(String modid) {
        Optional<ModContainer> mod = (Optional<ModContainer>) ModList.get().getModContainerById(modid);
        if (mod.isPresent()) return mod.get().getModInfo().getDisplayName();

        return modid;
    }

    private static Component getEffectDescription(MobEffect effect) {
        String translationKey = "description." + effect.getDescriptionId();
        return I18n.exists(translationKey) ? new TranslatableComponent(translationKey) : null;
    }

    private static void addEffectsTooltip(List<MobEffectInstance> effects, List<Component> tooltip) {
        MutableComponent noEffect = (new TranslatableComponent("effect.none")).withStyle(ChatFormatting.GRAY);

        List<Pair<Attribute, AttributeModifier>> list1 = Lists.newArrayList();
        if (effects.isEmpty()) tooltip.add(noEffect);
        else {
            for(MobEffectInstance effectInstance : effects) {
                MutableComponent line = new TranslatableComponent(effectInstance.getEffect().getDescriptionId());
                MobEffect effect = effectInstance.getEffect();
                Map<Attribute, AttributeModifier> map = effect.getAttributeModifiers();
                if (!map.isEmpty()) {
                    for(Map.Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
                        AttributeModifier attributemodifier = entry.getValue();
                        AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(), effect.getAttributeModifierValue(effectInstance.getAmplifier(), attributemodifier), attributemodifier.getOperation());
                        list1.add(new Pair<>(entry.getKey(), attributemodifier1));
                    }
                }

                if (effectInstance.getAmplifier() > 0) {
                    line = new TranslatableComponent("potion.withAmplifier", line, new TranslatableComponent("potion.potency." + effectInstance.getAmplifier()));
                }
                else if (effectInstance.getDuration() > 20) {
                    line = new TranslatableComponent("potion.withDuration", line, MobEffectUtil.formatDuration(effectInstance, 1));
                }

                tooltip.add(line.withStyle(effect.getCategory().getTooltipFormatting()));
            }
        }

        if (!list1.isEmpty()) {
            tooltip.add(TextComponent.EMPTY);
            tooltip.add((new TranslatableComponent("potion.whenDrank")).withStyle(ChatFormatting.DARK_PURPLE));

            for(Pair<Attribute, AttributeModifier> pair : list1) {
                AttributeModifier modifier = pair.getSecond();
                double amount = modifier.getAmount();
                if (amount == 0) continue;

                double d1 = modifier.getAmount();
                if (modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE || modifier.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    d1 *= 100.0D;
                }

                if (amount < 0.0D) d1 *= -1.0D;

                tooltip.add((
                    new TranslatableComponent(
                        String.format("attribute.modifier.%s.%d", amount > 0 ? "plus" : "take", modifier.getOperation().toValue()),
                        ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1),
                        new TranslatableComponent(pair.getFirst().getDescriptionId())
                    )
                ).withStyle(amount > 0 ? ChatFormatting.BLUE : ChatFormatting.RED));
            }
        }
    }

    private static List<MobEffectInstance> getSuspiciousStewEffects(ItemStack stew) {
        List<MobEffectInstance> effectInstances = new ArrayList<>();

        CompoundTag tag = stew.getTag();
        if (tag != null && tag.contains("Effects", Tag.TAG_LIST)) {
            ListTag list = tag.getList("Effects", Tag.TAG_COMPOUND);
            for(int i = 0; i < list.size(); i++) {
                CompoundTag effectTag = list.getCompound(i);
                byte id = effectTag.getByte("EffectId");
                int duration = effectTag.contains("EffectDuration", Tag.TAG_INT) ? effectTag.getInt("EffectDuration") : 160;

                var effect = MobEffect.byId(id);
                if (effect != null) {
                    effectInstances.add(new MobEffectInstance(effect, duration));
                }
            }
        }

        return effectInstances;
    }
}
