package azmalent.potiondescriptions.client;

import WayofTime.bloodmagic.core.RegistrarBloodMagicItems;
import WayofTime.bloodmagic.item.ItemPotionFlask;
import azmalent.potiondescriptions.ModConfig;
import com.mojang.realmsclient.gui.ChatFormatting;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.mod.items.InitItems;
import de.ellpeck.actuallyadditions.mod.items.ItemCoffee;
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
import rustic.common.items.ItemElixir;
import rustic.common.items.ModItems;
import rustic.common.util.ElixirUtils;
import vazkii.botania.api.brew.IBrewItem;

import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class TooltipHandler {
    private static final boolean BOTANIA_LOADED = Loader.isModLoaded("botania");
    private static final boolean BLOOD_MAGIC_LOADED = Loader.isModLoaded("bloodmagic");
    private static final boolean ACTUALLY_ADDITIONS_LOADED = Loader.isModLoaded("actuallyadditions");
    private static final boolean RUSTIC_LOADED = Loader.isModLoaded("rustic");

    @SubscribeEvent
    public void onPotionTooltip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEmpty()) return;

        Item item = itemStack.getItem();
        if (item instanceof ItemPotion || item instanceof ItemTippedArrow
                || BLOOD_MAGIC_LOADED && item == RegistrarBloodMagicItems.POTION_FLASK) {
            List<PotionEffect> effects = PotionUtils.getEffectsFromStack(itemStack);
            addTooltip(effects, event.getToolTip());
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

    private void addTooltip(List<PotionEffect> effects, List<String> tooltip) {
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

    private String getModName(Potion potion) {
        String modid = potion.getRegistryName().getNamespace();
        ModContainer mod = Loader.instance().getIndexedModList().get(modid);
        if (mod != null) return mod.getName();

        return modid;
    }

    private String getEffectDescription(Potion potion) {
        String translationKey = potion.getName() + ".desc";

        if (I18n.hasKey(translationKey)) {
            return I18n.format(translationKey);
        }

        return null;
    }
}
