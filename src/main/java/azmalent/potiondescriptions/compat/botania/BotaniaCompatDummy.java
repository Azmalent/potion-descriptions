package azmalent.potiondescriptions.compat.botania;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class BotaniaCompatDummy implements IBotaniaCompat {
    @Override
    public boolean isBrewItem(Item item) {
        return false;
    }

    @Override
    public List<StatusEffectInstance> getBrewEffects(ItemStack stack) {
        return null;
    }
}
