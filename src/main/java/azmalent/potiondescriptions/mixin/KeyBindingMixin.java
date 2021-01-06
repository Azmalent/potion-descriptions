package azmalent.potiondescriptions.mixin;

import azmalent.potiondescriptions.util.IKeyBindingMixin;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyBinding.class)
public class KeyBindingMixin implements IKeyBindingMixin {
    @Shadow private InputUtil.Key boundKey;

    public InputUtil.Key getBoundKey() {
        return this.boundKey;
    }
}
