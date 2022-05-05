package com.yyon.grapplinghook.client;

import com.yyon.grapplinghook.utils.key.IKeyConflictContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

// TODO
public class NonConflictingKeyBinding extends KeyBinding {

	public NonConflictingKeyBinding(String description, int keyCode, String category) {
		super(description, keyCode, category);
		this.setNonConflict();
	}

	boolean isActive = false;

	private void setNonConflict() {
//		this.setKeyConflictContext(new IKeyConflictContext() {
//			@Override
//			public boolean isActive() {
//				return false;
//			}
//			@Override
//			public boolean conflicts(IKeyConflictContext other) {
//				return false;
//			}
//		});
	}

	public NonConflictingKeyBinding(String description, InputUtil.Type type, int keyCode, String category) {
		super(description, type, keyCode, category);
		this.setNonConflict();
	}

	@Override
	public boolean equals(KeyBinding keyBinding) {
	   return false;
   }

	public boolean hasKeyCodeModifierConflict(KeyBinding other) {
	   return true;
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
