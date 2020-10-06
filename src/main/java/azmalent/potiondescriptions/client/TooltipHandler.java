package azmalent.potiondescriptions.client;

import WayofTime.bloodmagic.core.RegistrarBloodMagicItems;
import azmalent.potiondescriptions.ModConfig;
import azmalent.potiondescriptions.PotionDescriptions;
import com.mojang.realmsclient.gui.ChatFormatting;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.mod.items.InitItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTippedArrow;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry;
import rustic.common.items.ModItems;
import rustic.common.util.ElixirUtils;
import vazkii.botania.api.brew.IBrewItem;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SideOnly(Side.CLIENT)
public class TooltipHandler {
    private static final boolean BOTANIA_LOADED = Loader.isModLoaded("botania");
    private static final boolean BLOOD_MAGIC_LOADED = Loader.isModLoaded("bloodmagic");
    private static final boolean ACTUALLY_ADDITIONS_LOADED = Loader.isModLoaded("actuallyadditions");
    private static final boolean EXTRA_ALCHEMY_LOADED = Loader.isModLoaded("extraalchemy");
    private static final boolean RUSTIC_LOADED = Loader.isModLoaded("rustic");

    @SubscribeEvent
    public void onPotionTooltip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEmpty()) return;

        Item item = itemStack.getItem();
        if (item instanceof ItemPotion || isTippedAmmo(item)
                || BLOOD_MAGIC_LOADED && item == RegistrarBloodMagicItems.POTION_FLASK
                || EXTRA_ALCHEMY_LOADED && item == zabi.minecraft.extraalchemy.items.ModItems.potion_ring) {
            List<PotionEffect> effects = PotionUtils.getEffectsFromStack(itemStack);
            addTooltip(effects, event.getToolTip());

            if (!ModConfig.isModIgnored("extraalchemy")) {
                removeExtraAlchemyCredits(event.getToolTip());
            }
        }
        else if(BOTANIA_LOADED && item instanceof IBrewItem) {
            List<PotionEffect> effects = ((IBrewItem) item).getBrew(itemStack).getPotionEffects(itemStack);
            addTooltip(effects, event.getToolTip());
        }
        else if (ACTUALLY_ADDITIONS_LOADED && item == InitItems.itemCoffee) {
            PotionEffect[] effects = ActuallyAdditionsAPI.methodHandler.getEffectsFromStack(itemStack);
            if (effects != null && effects.length > 0) {
                addTooltip(Arrays.asList(effects), event.getToolTip());
            }
        }
        else if (RUSTIC_LOADED && item == ModItems.ELIXIR) {
            List<PotionEffect> effects = ElixirUtils.getEffects(itemStack);
            addTooltip(effects, event.getToolTip());
        }
    }

    private void addTooltip(List<PotionEffect> effectList, List<String> tooltip) {
        Set<PotionEffect> effects = effectList.stream().filter(effect -> !isIgnored(effect)).collect(Collectors.toSet());
        if (effects.isEmpty()) return;

        KeyBinding sneakButton = Minecraft.getMinecraft().gameSettings.keyBindSneak;
        boolean sneaking = GameSettings.isKeyDown(sneakButton);

        if (!sneaking && ModConfig.sneakRequired && ModConfig.sneakMessageEnabled) {
            tooltip.add(I18n.format("tooltip.potiondescriptions.sneakToView", sneakButton.getDisplayName()));
            return;
        }

        if (sneaking || !ModConfig.sneakRequired) {
            for (PotionEffect effect : effects) {
                Potion potion = effect.getPotion();

                String description = getEffectDescription(potion);
                if (description == null) continue;

                ChatFormatting effectFormat = potion.isBeneficial() ? ChatFormatting.BLUE : ChatFormatting.RED;
                String potionName = I18n.format(potion.getName());

                tooltip.add(I18n.format("tooltip.potiondescriptions.effect", effectFormat, potionName));
                tooltip.add(description);
                tooltip.add(I18n.format("tooltip.potiondescriptions.sourceMod", getModName(potion)));
            }
        }
    }

    private void removeExtraAlchemyCredits(List<String> tooltip) {
        String credit = ChatFormatting.GOLD + I18n.format("tooltip.credit", "");
        String uncertainCredit = ChatFormatting.GOLD + I18n.format("tooltip.credit.uncertain", "");

        Iterator<String> iterator = tooltip.iterator();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.startsWith(credit) || line.startsWith(uncertainCredit)) {
                iterator.remove();
            }
        }
    }

    private static String getModName(IForgeRegistryEntry entry) {
        String modid = entry.getRegistryName().getNamespace();
        ModContainer mod = Loader.instance().getIndexedModList().get(modid);
        if (mod != null) return mod.getName();

        return modid;
    }

    private static String getEffectDescription(Potion potion) {
        String translationKey = "description." + potion.getName();

        if (I18n.hasKey(translationKey)) {
            return I18n.format(translationKey);
        }

        return null;
    }

    private static Boolean isIgnored(PotionEffect effect) {
        String modid = effect.getPotion().getRegistryName().getNamespace();
        return ModConfig.isModIgnored(modid);
    }

    private static Boolean isTippedAmmo(Item item) {
        if (item instanceof ItemTippedArrow) return true;

        if (item.getRegistryName().getNamespace().equals("spartanweaponry")) {
            if (item.getRegistryName().toString().endsWith("_tipped")) return true;
        }

        return false;
    }
}
