package com.yyon.grapplinghook.blocks.modifierblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;


public class GuiModifier extends Screen {
	private static final Identifier texture = new Identifier("grapplemod",
			"gui/guimodifier_bg");

	int xSize = 221;
	int ySize = 221;

	protected int guiLeft;
	protected int guiTop;

	int posy;
	int id;
	Map<ClickableWidget, String> options;

	TileEntityGrappleModifier tileEnt;
	GrappleCustomization customization;

	GrappleCustomization.upgradeCategories category = null;

	public GuiModifier(TileEntityGrappleModifier tileent) {
		super(new TranslatableText("grapplemodifier.title.desc"));

		this.tileEnt = tileent;
		customization = tileent.customization;
	}

	@Override
	public void init() {
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;

		mainScreen();
	}

	class PressCategory implements ButtonWidget.PressAction {
		GrappleCustomization.upgradeCategories category;
		public PressCategory(GrappleCustomization.upgradeCategories category) {
			this.category = category;
		}

		public void onPress(ButtonWidget w) {
			boolean unlocked = tileEnt.isUnlocked(category) || MinecraftClient.getInstance().player.isCreative();

			if (unlocked) {
				showCategoryScreen(category);
			} else {
				notAllowedScreen(category);
			}
		}
	}

	class PressBack implements ButtonWidget.PressAction {
		public void onPress(ButtonWidget p_onPress_1_) {
			mainScreen();
		}
	}

	public void mainScreen() {
		clearScreen();

		this.addDrawableChild(new ButtonWidget(this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10,
			50, 20, new TranslatableText("grapplemodifier.close.desc"), p_onPress_1_ -> close()));
		this.addDrawableChild(new ButtonWidget(this.guiLeft + this.xSize - 50 - 10, this.guiTop + this.ySize - 20 - 10,
			50, 20, new TranslatableText("grapplemodifier.reset.desc"), p_onPress_1_ -> {
				customization = new GrappleCustomization();
				mainScreen();
			}));
		this.addDrawableChild(new ButtonWidget(this.guiLeft + 10 + 75, this.guiTop + this.ySize - 20 - 10,
			50, 20, new TranslatableText("grapplemodifier.helpbutton.desc"), p_onPress_1_ -> helpScreen()));

		int y = 0;
		int x = 0;
		for (int i = 0; i < GrappleCustomization.upgradeCategories.size(); i++) {
			GrappleCustomization.upgradeCategories category = GrappleCustomization.upgradeCategories.fromInt(i);
			if (category != GrappleCustomization.upgradeCategories.LIMITS) {
				if (i == GrappleCustomization.upgradeCategories.size()/2) {
					y = 0;
					x += 1;
				}
				this.addDrawableChild(
						new ButtonWidget(this.guiLeft + 10 + 105*x, this.guiTop + 15 + 30 * y, 95, 20, Text.of(category.getName()), new PressCategory(category)));
				y += 1;
			}
		}

		this.addDrawableChild(new TextWidget(new TranslatableText("grapplemodifier.apply.desc"), this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10 - 10));
	}

	static class BackgroundWidget extends ClickableWidget {
		public BackgroundWidget(int x, int y, int width, int height, Text text) {
			super(x, y, width, height, text);
			this.active = false;
		}

		public BackgroundWidget(int x, int y, int w, int h) {
			this(x, y, w, h, Text.of(""));
		}

		public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			RenderSystem.setShaderTexture(0, texture);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexture(matrices, this.x, this.y, 0, 0, this.width, this.height);
		}

		@Override
		public SelectionType getType() {
			return null;
		}

		@Override
		public void appendNarrations(NarrationMessageBuilder builder) {
		}
	}

	public void clearScreen() {
		this.category = null;
		posy = 10;
		id = 10;
		options = new HashMap<>();
		this.clearChildren();

		this.addDrawableChild(new BackgroundWidget(this.guiLeft, this.guiTop, this.xSize, this.ySize));
	}

	static class TextWidget extends ClickableWidget {
		public TextWidget(int x, int y, int width, int height, Text text) {
			super(x, y, width, height, text);
		}

		public TextWidget(Text text, int x, int y) {
			this(x, y, 50, 15 * text.getString().split("\n").length + 5, text);
		}

		public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			MinecraftClient minecraft = MinecraftClient.getInstance();
			TextRenderer fontrenderer = minecraft.textRenderer;
			RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			int j = this.getMessage().getStyle().getColor().getRgb();
			int lineno = 0;
			for (String s : this.getMessage().getString().split("\n")) {
				drawTextWithShadow(matrices, fontrenderer, Text.of(s), this.x, this.y + lineno * 15, j | (int) Math.ceil(this.alpha * 255.0F) << 24);
				lineno++;
			}
		}

		@Override
		public SelectionType getType() {
			return null;
		}

		@Override
		public void appendNarrations(NarrationMessageBuilder builder) {
		}
	}

	public void notAllowedScreen(GrappleCustomization.upgradeCategories category) {
		clearScreen();

		this.addDrawableChild(new ButtonWidget(this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20, new TranslatableText("grapplemodifier.back.desc"), new PressBack()));
		this.category = category;
		this.addDrawableChild(new TextWidget(new TranslatableText("grapplemodifier.unlock1.desc"), this.guiLeft + 10, this.guiTop + 10));
		this.addDrawableChild(new TextWidget(Text.of(this.category.getName()), this.guiLeft + 10, this.guiTop + 25));
		this.addDrawableChild(new TextWidget(new TranslatableText("grapplemodifier.unlock2.desc"), this.guiLeft + 10, this.guiTop + 40));
		this.addDrawableChild(new TextWidget(new TranslatableText("grapplemodifier.unlock3.desc"), this.guiLeft + 10, this.guiTop + 55));
		this.addDrawableChild(new TextWidget(new ItemStack(this.category.getItem()).getName(), this.guiLeft + 10, this.guiTop + 70));
		this.addDrawableChild(new TextWidget(new TranslatableText("grapplemodifier.unlock4.desc"), this.guiLeft + 10, this.guiTop + 85));
	}

	public void helpScreen() {
		clearScreen();

		this.addDrawableChild(new ButtonWidget(this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20, new TranslatableText("grapplemodifier.back.desc"), new PressBack()));

		this.addDrawableChild(new TextWidget(new TranslatableText("grapplemodifier.help.desc"), this.guiLeft + 10, this.guiTop + 10));
	}

	class GuiCheckbox extends CheckboxWidget {
		String option;
		public Text tooltip;

		public GuiCheckbox(int x, int y, int w, int h,
						   Text text, boolean val, String option, Text tooltip) {
			super(x, y, w, h, text, val);
			this.option = option;
			this.tooltip = tooltip;
		}

		@Override
		public void onPress() {
			super.onPress();

			customization.setBoolean(option, this.isChecked());

			updateEnabled();
		}

		@Override
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			super.renderButton(matrices, mouseX, mouseY, delta);

			if (this.isHovered()) {
				String tooltiptext = tooltip.getString();
				List<Text> lines = new ArrayList<Text>();
				for (String line : tooltiptext.split("\n")) {
					lines.add(Text.of(line));
				}
				renderTooltip(matrices, mouseX, mouseY);
			}
		}
	}

	public void addCheckbox(String option) {
		String text = new TranslatableText(this.customization.getName(option)).getString();
		String desc = new TranslatableText(this.customization.getDescription(option)).toString();
		GuiCheckbox checkbox = new GuiCheckbox(10 + this.guiLeft, posy + this.guiTop, this.xSize - 20, 20, Text.of(text), customization.getBoolean(option), option, Text.of(desc));
		posy += 22;
		this.addDrawableChild(checkbox);
		options.put(checkbox, option);
	}

	class GuiSlider extends SliderWidget {
		double min, max, val;
		String text, option;
		public Text tooltip;
		public GuiSlider(int x, int y, int w, int h,
						 Text text, double min, double max, double val, String option, Text tooltip) {
			super(x, y, w, h, text, (val - min) / (max - min));
			this.min = min;
			this.max = max;
			this.val = val;
			this.text = text.getString();
			this.option = option;
			this.tooltip = tooltip;

			this.updateMessage();
		}

		@Override
		protected void updateMessage() {
			this.setMessage(Text.of(text + ": " + String.format("%.1f", this.val)));
		}

		@Override
		protected void applyValue() {
			this.val = (this.value * (this.max - this.min)) + this.min;
//			d = Math.floor(d * 10 + 0.5) / 10;
			customization.setDouble(option, this.val);
		}

		@Override
		public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			super.renderButton(matrices, mouseX, mouseY, delta);

			if (this.isHovered()) {
				String tooltiptext = tooltip.getString();
				List<Text> lines = new ArrayList<Text>();
				for (String line : tooltiptext.split("\n")) {
					lines.add(Text.of(line));
				}
				renderTooltip(matrices, mouseX, mouseY);
			}
		}
	}

	public void addSlider(String option) {
		double d = customization.getDouble(option);
		d = Math.floor(d * 10 + 0.5) / 10;

		double max = customization.getMax(option, this.getLimits());
		double min = customization.getMin(option, this.getLimits());

		String text = new TranslatableText(this.customization.getName(option)).getString();
		String desc = new TranslatableText(this.customization.getDescription(option)).toString();
		GuiSlider slider = new GuiSlider(10 + this.guiLeft, posy + this.guiTop, this.xSize - 20, 20, Text.of(text), min, max, d, option, Text.of(desc));

		posy += 22;
		this.addDrawableChild(slider);
		options.put(slider, option);
	}

	public void showCategoryScreen(GrappleCustomization.upgradeCategories category) {
		clearScreen();

		this.addDrawableChild(new ButtonWidget(this.guiLeft + 10, this.guiTop + this.ySize - 20 - 10, 50, 20, new TranslatableText("grapplemodifier.back.desc"), new PressBack()));
		this.category = category;

		if (category == GrappleCustomization.upgradeCategories.ROPE) {
			addSlider("maxlen");
			addCheckbox("phaserope");
			addCheckbox("sticky");
		} else if (category == GrappleCustomization.upgradeCategories.THROW) {
			addSlider("hookgravity");
			addSlider("throwspeed");
			addCheckbox("reelin");
			addSlider("verticalthrowangle");
			addSlider("sneakingverticalthrowangle");
			addCheckbox("detachonkeyrelease");
		} else if (category == GrappleCustomization.upgradeCategories.MOTOR) {
			addCheckbox("motor");
			addSlider("motormaxspeed");
			addSlider("motoracceleration");
			addCheckbox("motorwhencrouching");
			addCheckbox("motorwhennotcrouching");
			addCheckbox("smartmotor");
			addCheckbox("motordampener");
			addCheckbox("pullbackwards");
		} else if (category == GrappleCustomization.upgradeCategories.SWING) {
			addSlider("playermovementmult");
		} else if (category == GrappleCustomization.upgradeCategories.STAFF) {
			addCheckbox("enderstaff");
		} else if (category == GrappleCustomization.upgradeCategories.FORCEFIELD) {
			addCheckbox("repel");
			addSlider("repelforce");
		} else if (category == GrappleCustomization.upgradeCategories.MAGNET) {
			addCheckbox("attract");
			addSlider("attractradius");
		} else if (category == GrappleCustomization.upgradeCategories.DOUBLE) {
			addCheckbox("doublehook");
			addCheckbox("smartdoublemotor");
			addSlider("angle");
			addSlider("sneakingangle");
			addCheckbox("oneropepull");
		} else if (category == GrappleCustomization.upgradeCategories.ROCKET) {
			addCheckbox("rocket");
			addSlider("rocket_force");
			addSlider("rocket_active_time");
			addSlider("rocket_refuel_ratio");
			addSlider("rocket_vertical_angle");
		}

		this.updateEnabled();
	}

	@Override
	public void close() {
//		this.updateOptions();
		this.tileEnt.setCustomizationClient(customization);

		super.close();
	}

	public void updateEnabled() {
		for (ClickableWidget b : this.options.keySet()) {
			String option = this.options.get(b);
			boolean enabled = true;

			String desc = new TranslatableText(this.customization.getDescription(option)).toString();

			if (this.customization.isOptionValid(option)) {
			} else {
				desc = new TranslatableText("grapplemodifier.incompatability.desc") + "\n" + desc;
				enabled = false;
			}

			int level = this.customization.optionEnabled(option);
			if (this.getLimits() < level) {
				if (level == 1) {
					desc = new TranslatableText("grapplemodifier.limits.desc") + "\n" + desc;
				} else {
					desc = new TranslatableText("grapplemodifier.locked.desc") + "\n" + desc;
				}
				enabled = false;
			}

			b.active = enabled;

			if (b instanceof GuiSlider) {
				((GuiSlider) b).tooltip = Text.of(desc);
				b.setAlpha(enabled ? 1.0F : 0.5F);
			}
			if (b instanceof GuiCheckbox) {
				((GuiCheckbox) b).tooltip = Text.of(desc);
				b.setAlpha(enabled ? 1.0F : 0.5F);
			}
		}
	}

	public int getLimits() {
		if (this.tileEnt.isUnlocked(GrappleCustomization.upgradeCategories.LIMITS) || MinecraftClient.getInstance().player.isCreative()) {
			return 1;
		}
		return 0;
	}
}
