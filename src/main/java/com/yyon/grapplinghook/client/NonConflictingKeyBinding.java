package com.yyon.grapplinghook.client;

import javax.annotation.Nonnull;

import com.yyon.grapplinghook.utils.key.IKeyConflictContext;
import com.yyon.grapplinghook.utils.key.KeyModifier;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;


// TODO
public class NonConflictingKeyBinding extends KeyBinding {

	public NonConflictingKeyBinding(String description, int keyCode, String category) {
		super(description, keyCode, category);
	}

	public NonConflictingKeyBinding(String description, InputUtil.Type type, int keyCode, String category) {
		super(description, type, keyCode, category);
	}

	@Override
	public boolean equals(KeyBinding keyBinding) {
	   return false;
   }

	public boolean isDown = false;

	@Override
	public boolean isPressed() {
	   return isDown;
   }

	@Override
	public void setPressed(boolean value) {
	   this.isDown = value;
   }
}
