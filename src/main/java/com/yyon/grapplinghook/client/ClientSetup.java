package com.yyon.grapplinghook.client;

import java.util.ArrayList;
import java.util.List;

import com.yyon.grapplinghook.controllers.AirfrictionController;
import com.yyon.grapplinghook.controllers.ForcefieldController;
import com.yyon.grapplinghook.entities.grapplehook.RenderGrapplehookEntity;
import com.yyon.grapplinghook.network.DetachSingleHookMessage;
import com.yyon.grapplinghook.network.GrappleAttachMessage;
import com.yyon.grapplinghook.network.GrappleAttachPosMessage;
import com.yyon.grapplinghook.network.GrappleDetachMessage;
import com.yyon.grapplinghook.network.LoggedInMessage;
import com.yyon.grapplinghook.network.SegmentMessage;
import com.yyon.grapplinghook.utils.dist.DistExecutor;
import com.yyon.grapplinghook.utils.key.FabricKeyBindingManager;
import com.yyon.grapplinghook.utils.key.IKeyBindingManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import static com.yyon.grapplinghook.client.ClientControllerManager.controllers;
import static com.yyon.grapplinghook.common.CommonSetup.forcefieldItem;
import static com.yyon.grapplinghook.common.CommonSetup.grapplehookEntityType;
import static com.yyon.grapplinghook.common.CommonSetup.grapplingHookItem;
import static net.minecraft.client.util.InputUtil.UNKNOWN_KEY;


public class ClientSetup implements ClientModInitializer {

	public static List<KeyBinding> keyBindings = new ArrayList<>();

	public static KeyBinding createKeyBinding(KeyBinding k) {
		keyBindings.add(k);
		return k;
	}

	public static KeyBinding key_boththrow = createKeyBinding(new NonConflictingKeyBinding("key.boththrow.desc", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_2, "key.grapplemod.category"));
	public static KeyBinding key_leftthrow = createKeyBinding(new NonConflictingKeyBinding("key.leftthrow.desc", UNKNOWN_KEY.getCode(), "key.grapplemod.category"));
	public static KeyBinding key_rightthrow = createKeyBinding(new NonConflictingKeyBinding("key.rightthrow.desc", UNKNOWN_KEY.getCode(), "key.grapplemod.category"));
	public static KeyBinding key_motoronoff = createKeyBinding(new NonConflictingKeyBinding("key.motoronoff.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
	public static KeyBinding key_jumpanddetach = createKeyBinding(new NonConflictingKeyBinding("key.jumpanddetach.desc", GLFW.GLFW_KEY_SPACE, "key.grapplemod.category"));
	public static KeyBinding key_slow = createKeyBinding(new NonConflictingKeyBinding("key.slow.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
	public static KeyBinding key_climb = createKeyBinding(new NonConflictingKeyBinding("key.climb.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));
	public static KeyBinding key_climbup = createKeyBinding(new NonConflictingKeyBinding("key.climbup.desc", UNKNOWN_KEY.getCode(), "key.grapplemod.category"));
	public static KeyBinding key_climbdown = createKeyBinding(new NonConflictingKeyBinding("key.climbdown.desc", UNKNOWN_KEY.getCode(), "key.grapplemod.category"));
	public static KeyBinding key_enderlaunch = createKeyBinding(new NonConflictingKeyBinding("key.enderlaunch.desc", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_1, "key.grapplemod.category"));
	public static KeyBinding key_rocket = createKeyBinding(new NonConflictingKeyBinding("key.rocket.desc", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_1, "key.grapplemod.category"));
	public static KeyBinding key_slide = createKeyBinding(new NonConflictingKeyBinding("key.slide.desc", GLFW.GLFW_KEY_LEFT_SHIFT, "key.grapplemod.category"));

	@Override
	public void onInitializeClient() {
		// register all the key bindings
		IKeyBindingManager keyBindingManager = IKeyBindingManager.getInstance();
		for (KeyBinding keyBinding : keyBindings) {
			keyBindingManager.register(keyBinding);
		}

		// client network
		ClientPlayNetworking.registerGlobalReceiver(GrappleAttachMessage.IDENTIFIER, (client, handler, buf, responseSender) -> {
			GrappleAttachMessage m = new GrappleAttachMessage(buf);
			client.execute(m::processMessage);
		});
		ClientPlayNetworking.registerGlobalReceiver(GrappleDetachMessage.IDENTIFIER, (client, handler, buf, responseSender) -> {
			GrappleDetachMessage m = new GrappleDetachMessage(buf);
			client.execute(m::processMessage);
		});
		ClientPlayNetworking.registerGlobalReceiver(DetachSingleHookMessage.IDENTIFIER, (client, handler, buf, responseSender) -> {
			DetachSingleHookMessage m = new DetachSingleHookMessage(buf);
			client.execute(m::processMessage);
		});
		ClientPlayNetworking.registerGlobalReceiver(GrappleAttachPosMessage.IDENTIFIER, (client, handler, buf, responseSender) -> {
			GrappleAttachPosMessage m = new GrappleAttachPosMessage(buf);
			client.execute(m::processMessage);
		});
		ClientPlayNetworking.registerGlobalReceiver(SegmentMessage.IDENTIFIER, (client, handler, buf, responseSender) -> {
			SegmentMessage m = new SegmentMessage(buf);
			client.execute(m::processMessage);
		});
		ClientPlayNetworking.registerGlobalReceiver(LoggedInMessage.IDENTIFIER, (client, handler, buf, responseSender) -> {
			LoggedInMessage m = new LoggedInMessage(buf);
			client.execute(m::processMessage);
		});

		// entity renderer
		EntityRendererRegistry.register(grapplehookEntityType, RenderGrapplehookEntity::new);

		// model predicates
		ModelPredicateProviderRegistry.register(grapplingHookItem, new Identifier("rocket"), (stack, world, entity, seed) -> grapplingHookItem.getPropertyRocket(stack, world, entity) ? 1 : 0);
		ModelPredicateProviderRegistry.register(grapplingHookItem, new Identifier("double"), (stack, world, entity, seed) -> grapplingHookItem.getPropertyDouble(stack, world, entity) ? 1 : 0);
		ModelPredicateProviderRegistry.register(grapplingHookItem, new Identifier("motor"), (stack, world, entity, seed) -> grapplingHookItem.getPropertyMotor(stack, world, entity) ? 1 : 0);
		ModelPredicateProviderRegistry.register(grapplingHookItem, new Identifier("smart"), (stack, world, entity, seed) -> grapplingHookItem.getPropertySmart(stack, world, entity) ? 1 : 0);
		ModelPredicateProviderRegistry.register(grapplingHookItem, new Identifier("enderstaff"), (stack, world, entity, seed) -> grapplingHookItem.getPropertyEnderstaff(stack, world, entity) ? 1 : 0);
		ModelPredicateProviderRegistry.register(grapplingHookItem, new Identifier("magnet"), (stack, world, entity, seed) -> grapplingHookItem.getPropertyMagnet(stack, world, entity) ? 1 : 0);
		ModelPredicateProviderRegistry.register(grapplingHookItem, new Identifier("attached"), (stack, world, entity, seed) -> {
			if (entity == null) {return 0;}
			return (controllers.containsKey(entity.getId()) && !(controllers.get(entity.getId()) instanceof AirfrictionController)) ? 1 : 0;
		});
		ModelPredicateProviderRegistry.register(forcefieldItem, new Identifier("attached"), (stack, world, entity, seed) -> {
			if (entity == null) {return 0;}
			return (controllers.containsKey(entity.getId()) && controllers.get(entity.getId()) instanceof ForcefieldController) ? 1 : 0;
		});

		// predefined events
		ClientTickEvents.END_CLIENT_TICK.register(new ClientTickEvent()::onEndTick);
		HudRenderCallback.EVENT.register(new CrosshairRenderer()::onHudRender);
		LootTableLoadingCallback.EVENT.register(new LootTableLoader()::onLootTableLoading);
		ClientLifecycleEvents.CLIENT_STOPPING.register(new ClientStoppingEvent()::onClientStopping);

		// client models
		clientControllerManager = new ClientControllerManager();
		clientProxy = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> () -> null);
	}

	public static ClientControllerManager clientControllerManager;

	public static ClientProxyInterface clientProxy;
}
