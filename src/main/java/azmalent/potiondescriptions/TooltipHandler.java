package azmalent.potiondescriptions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class TooltipHandler {
    public static boolean BOTANIA_LOADED = ModList.get().isLoaded("botania");
    public static boolean RELIQUARY_LOADED = ModList.get().isLoaded("xreliquary");

    @SubscribeEvent
    public static void onTooltipDisplayed(final ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEmpty()) return;

        Item item = itemStack.getItem();

        if (itemStack.hasTag() && itemStack.getTag().contains("Potion")) {
            List<EffectInstance> effects = PotionUtils.getEffectsFromStack(itemStack);
            TooltipHandler.addPotionTooltip(effects, event.getToolTip());
        }
        else if (BOTANIA_LOADED && item instanceof IBrewItem) {
            List<EffectInstance> effects = ((IBrewItem) item).getBrew(itemStack).getPotionEffects(itemStack);
            TooltipHandler.addPotionTooltip(effects, event.getToolTip());
        }
        else if (RELIQUARY_LOADED &&
                (item == ModItems.POTION_ESSENCE
                || item == ModItems.TIPPED_ARROW
                || item instanceof PotionItemBase)) {
            List<EffectInstance> effects = XRPotionHelper.getPotionEffectsFromStack(itemStack);
            TooltipHandler.addPotionTooltip(effects, event.getToolTip());
        }
    }

    public static void addPotionTooltip(List<EffectInstance> effectList, List<ITextComponent> tooltip) {
        Set<EffectInstance> effects = new HashSet(effectList);
        if (effects.isEmpty()) return;

        KeyBinding sneakButton = Minecraft.getInstance().gameSettings.keyBindSneak;
        int keyCode = sneakButton.getKey().getKeyCode();
        boolean sneaking = InputMappings.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), keyCode);

        if (!sneaking && ModConfig.sneakRequired.get() && ModConfig.sneakMessageEnabled.get()) {
            addString(tooltip, I18n.format("tooltip.potiondescriptions.sneakToView", I18n.format(sneakButton.getTranslationKey())));
        }
        else if (sneaking || !ModConfig.sneakRequired.get()) {
            for (EffectInstance effectInstance : effects) {
                Effect effect = effectInstance.getPotion();

                String description = getEffectDescription(effect);
                TextFormatting effectFormat = effect.isBeneficial() ? TextFormatting.BLUE : TextFormatting.RED;
                String effectName = I18n.format(effect.getName());

                addString(tooltip, I18n.format("tooltip.potiondescriptions.effect", effectFormat, effectName));
                addString(tooltip, description != null ? description : I18n.format("tooltip.potiondescriptions.missingDescription", "description." + effect.getName()));
                addString(tooltip, I18n.format("tooltip.potiondescriptions.sourceMod", getModName(effect)));
            }
        }
    }

    private static void addString(List<ITextComponent> tooltip, String string) {
        tooltip.add(new StringTextComponent(string));
    }

    private static String getModName(IForgeRegistryEntry entry) {
        String modid = entry.getRegistryName().getNamespace();
        Optional<ModContainer> mod = (Optional<ModContainer>) ModList.get().getModContainerById(modid);
        if (mod.isPresent()) return mod.get().getModInfo().getDisplayName();

        return modid;
    }

    private static String getEffectDescription(Effect effect) {
        String translationKey = "description." + effect.getName();

        if (I18n.hasKey(translationKey)) {
            return I18n.format(translationKey);
        }

        return null;
    }
}
