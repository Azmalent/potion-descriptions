package azmalent.potiondescriptions.compat.botania;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.brew.IBrewItem;

import java.util.List;

public class BotaniaCompatImpl implements IBotaniaCompat {
    @Override
    public boolean isBrewItem(Item item) {
        return item instanceof IBrewItem;
    }

    @Override
    public List<StatusEffectInstance> getBrewEffects(ItemStack stack) {
        IBrewItem brew = (IBrewItem) stack.getItem();
        return brew.getBrew(stack).getPotionEffects(stack);
    }
}
