/*
 * https://github.com/OrionDevelopment/Multi-Platform-Template
 */

package com.yyon.grapplinghook.utils.key;

import net.minecraft.client.option.KeyBinding;

/**
 * Defines the context that a {@link KeyBinding} is used.
 * Key conflicts occur when a {@link KeyBinding} has the same {@link IKeyConflictContext} and has conflicting modifiers and keyCodes.
 */
public interface IKeyConflictContext {

    /**
     * The gui conflict context which is active when a GUI is open.
     * @return
     */
    static IKeyConflictContext getGui()
    {
        return IKeyBindingManager.getInstance().getGuiKeyConflictContext();
    }

    /**
     * @return true if conditions are met to activate {@link KeyBinding}s with this context
     */
    boolean isActive();

    /**
     * @return true if the other context can have {@link KeyBinding} conflicts with this one.
     * This will be called on both contexts to check for conflicts.
     */
    boolean conflicts(IKeyConflictContext other);
}