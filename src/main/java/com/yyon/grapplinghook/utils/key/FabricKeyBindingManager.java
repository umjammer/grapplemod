/*
 * https://github.com/OrionDevelopment/Multi-Platform-Template
 */

package com.yyon.grapplinghook.utils.key;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;


public final class FabricKeyBindingManager implements IKeyBindingManager
{
    private static final FabricKeyBindingManager INSTANCE = new FabricKeyBindingManager();

    public static FabricKeyBindingManager getInstance()
    {
        return INSTANCE;
    }

    private FabricKeyBindingManager()
    {
    }

    @Override
    public void register(final KeyBinding mapping)
    {
        KeyBindingHelper.registerKeyBinding(mapping);
    }

    @Override
    public IKeyConflictContext getGuiKeyConflictContext()
    {
        return FabricGuiKeyConflictContext.INSTANCE;
    }

    @Override
    public KeyBinding createNew(
            final String translationKey, final IKeyConflictContext keyConflictContext, final InputUtil.Type inputType, final int key, final String groupTranslationKey)
    {
        return new KeyBinding(
          translationKey,
          inputType,
          key,
          groupTranslationKey
        );
    }

    @Override
    public KeyBinding createNew(
      final String translationKey,
      final IKeyConflictContext keyConflictContext,
      final KeyModifier keyModifier,
      final InputUtil.Type inputType,
      final int key,
      final String groupTranslationKey)
    {
        return new ModifiedKeyMapping(translationKey, inputType, key, groupTranslationKey, keyConflictContext, keyModifier);
    }

    @Override
    public boolean isKeyConflictOfActive(final KeyBinding keybinding)
    {
        if (keybinding instanceof ModifiedKeyMapping modifiedKeyMapping) {
            return modifiedKeyMapping.context.isActive();
        }

        return true;
    }

    @Override
    public boolean isKeyModifierActive(final KeyBinding keybinding)
    {
        if (keybinding instanceof ModifiedKeyMapping modifiedKeyMapping) {
            return modifiedKeyMapping.isKeyModifierActive();
        }

        return true;
    }

    private static final class FabricGuiKeyConflictContext implements IKeyConflictContext {

        private static final FabricGuiKeyConflictContext INSTANCE = new FabricGuiKeyConflictContext();

        private FabricGuiKeyConflictContext()
        {
        }

        @Override
        public boolean isActive()
        {
            return MinecraftClient.getInstance().currentScreen != null;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other)
        {
            return this == other;
        }
    }

    private static class ModifiedKeyMapping extends KeyBinding
    {
        private final IKeyConflictContext context;
        private final KeyModifier keyModifier;

        public ModifiedKeyMapping(
          final String translationKey,
          final InputUtil.Type inputType,
          final int key,
          final String groupTranslationKey,
          final IKeyConflictContext context,
          final KeyModifier keyModifier)
        {
            super(translationKey,
              inputType,
              key,
              groupTranslationKey);
            this.context = context;
            this.keyModifier = keyModifier;
        }

        @Override
        public boolean isPressed()
        {
            return super.isPressed() && isKeyModifierActive();
        }

        private boolean isKeyModifierActive() {
            return switch (keyModifier) {
                case CONTROL -> Screen.hasControlDown();
                case SHIFT -> Screen.hasShiftDown();
                case ALT -> Screen.hasAltDown();
            };
        }

        @Override
        public Text getBoundKeyLocalizedText()
        {
            return getKeyModifierMessage().append(super.getBoundKeyLocalizedText());
        }

        private LiteralText getKeyModifierMessage()
        {
            return switch (keyModifier) {
                case CONTROL -> new LiteralText("CTRL + ");
                case SHIFT -> new LiteralText("SHIFT + ");
                case ALT -> new LiteralText("ALT + ");
            };
        }
    }
}