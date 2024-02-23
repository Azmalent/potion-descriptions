package azmalent.potiondescriptions.platform;

import azmalent.potiondescriptions.platform.services.IModIntegrationHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import reliquary.init.ModItems;
import reliquary.items.PotionItemBase;
import reliquary.util.potions.XRPotionHelper;
import vazkii.botania.api.brew.IBrewItem;

import java.util.List;

public class ForgeModIntegrationHelper implements IModIntegrationHelper {
    @Override
    public boolean isBotaniaPotion(ItemStack itemStack) {
        return itemStack.getItem() instanceof IBrewItem;
    }

    @Override
    public List<MobEffectInstance> getBotaniaEffects(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return ((IBrewItem) item).getBrew(itemStack).getPotionEffects(itemStack);
    }

    @Override
    public boolean isReliquaryPotion(ItemStack itemStack) {
        Item item = itemStack.getItem();
        return item == ModItems.POTION_ESSENCE.get()
            || item == ModItems.TIPPED_ARROW.get()
            || item == ModItems.NEUTRAL_BULLET.get()
            || item == ModItems.NEUTRAL_MAGAZINE.get()
            || item instanceof PotionItemBase;
    }

    @Override
    public List<MobEffectInstance> getReliquaryEffects(ItemStack itemStack) {
        return XRPotionHelper.getPotionEffectsFromStack(itemStack);
    }
}
