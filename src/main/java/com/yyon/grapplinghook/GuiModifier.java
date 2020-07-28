
package com.yyon.grapplinghook;

import java.awt.Color;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.mojang.blaze3d.systems.RenderSystem;
import com.yyon.grapplinghook.blocks.GrappleModifierBlockEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;


public class GuiModifier extends HandledScreen<ModifireScreenHandler> {

    private static final Identifier texture = new Identifier("grapplemod", "textures/gui/guimodifier_bg.png");

    int xSize = 221;
    int ySize = 221;

    protected int guiLeft;
    protected int guiTop;

    int posy;

    int id;

    Map<AbstractButtonWidget, String> options;
    Map<AbstractButtonWidget, String> tooltips;

    GrappleModifierBlockEntity blockEntity;
    GrappleCustomization customization;
    GrappleMod.UpgradeCategories category = null;

    boolean allowed = false;
    boolean showingHelpScreen = false;

    PlayerEntity player;

    public GuiModifier(ModifireScreenHandler desc, PlayerInventory inventory, Text title) {
        super(desc, inventory, title);

        this.blockEntity = blockEntity;
        customization = blockEntity.customization;
        this.player = inventory.player;
    }

    @Override
    public void init() {
        super.init();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        clearScreen();
        mainScreen();
    }

    private void mainScreen() {
        this.buttons.add(new ButtonWidget(this.guiLeft + 10,
                                          this.guiTop + this.ySize - 20 - 10,
                                          50,
                                          20,
                                          new LiteralText(GrappleMod.proxy.localize("grapplemodifier.close.desc")),
                                          this::button1));
        this.buttons.add(new ButtonWidget(this.guiLeft + this.xSize - 50 - 10,
                                          this.guiTop + this.ySize - 20 - 10,
                                          50,
                                          20,
                                          new LiteralText(GrappleMod.proxy.localize("grapplemodifier.reset.desc")),
                                          this::button2));
        this.buttons.add(new ButtonWidget(this.guiLeft + 10 + 75,
                                          this.guiTop + this.ySize - 20 - 10,
                                          50,
                                          20,
                                          new LiteralText(GrappleMod.proxy.localize("grapplemodifier.helpbutton.desc")),
                                          this::button4));

        int y = 0;
        int x = 0;
        for (GrappleMod.UpgradeCategories category : GrappleMod.UpgradeCategories.values()) {
            if (category != GrappleMod.UpgradeCategories.LIMITS) {
                if (category.ordinal() == GrappleMod.UpgradeCategories.values().length / 2) {
                    y = 0;
                    x += 1;
                }
                this.buttons.add(new ButtonWidget(this.guiLeft + 10 + 105 * x,
                                                  this.guiTop + 15 + 30 * y,
                                                  95,
                                                  20,
                                                  new LiteralText(category.description),
                                                  this::button99));
                y += 1;
            }
        }
    }

    private void clearScreen() {
        this.buttons.clear();
        this.category = null;
        this.allowed = false;
        posy = 10;
        id = 10;
        options = new HashMap<>();
        tooltips = new HashMap<>();
    }

    private void notAllowedScreen(GrappleMod.UpgradeCategories category) {
        this.buttons.add(new ButtonWidget(this.guiLeft + 10,
                                          this.guiTop + this.ySize - 20 - 10,
                                          50,
                                          20,
                                          new LiteralText(GrappleMod.proxy.localize("grapplemodifier.back.desc")),
                                          this::button3));
        this.category = category;
        this.allowed = false;
    }

    private void helpScreen() {
        this.buttons.add(new ButtonWidget(this.guiLeft + 10,
                                          this.guiTop + this.ySize - 20 - 10,
                                          50,
                                          20,
                                          new LiteralText(GrappleMod.proxy.localize("grapplemodifier.back.desc")),
                                          this::button3));
    }

    private void addCheckbox(String option) {
        String text = GrappleMod.proxy.localize(this.customization.getName(option));
        String desc = GrappleMod.proxy.localize(this.customization.getDescription(option));
        CheckboxWidget checkbox = new CheckboxWidget(10 + this.guiLeft,
                                                     posy + this.guiTop,
                                                     50,
                                                     20,
                                                     new LiteralText(text),
                                                     customization.getBoolean(option)) {
            public void onPress() {
                super.onPress();
                if (options.containsKey(this)) {
                    updateOption(this);
                }
            }
        };
        posy += 20;
        this.buttons.add(checkbox);
        options.put(checkbox, option);
        tooltips.put(checkbox, desc);
    }

    private void addSlider(String option) {
        double d = customization.getDouble(option);
        d = Math.floor(d * 10 + 0.5) / 10;

        double max = customization.getMax(option, this.getLimits());
        double min = customization.getMin(option, this.getLimits());

        String text = GrappleMod.proxy.localize(this.customization.getName(option));
        SliderWidget slider = new Slider(10 + this.guiLeft, posy + this.guiTop, this.xSize - 20, 20, d, min, max);

        slider.setMessage(new LiteralText(text + ": " + Double.toString(d)));
//        slider.precision = 1;

        posy += 25;
        this.buttons.add(slider);
        options.put(slider, option);

        String desc = GrappleMod.proxy.localize(this.customization.getDescription(option));
        tooltips.put(slider, desc);
    }

    private void showCategoryScreen(GrappleMod.UpgradeCategories category) {
        this.buttons.add(new ButtonWidget(this.guiLeft + 10,
                                          this.guiTop + this.ySize - 20 - 10,
                                          50,
                                          20,
                                          new LiteralText(GrappleMod.proxy.localize("grapplemodifier.back.desc")),
                                          this::button3));
        this.category = category;
        this.allowed = true;

        switch (category) {
        case ROPE:
            addSlider("maxlen");
            addCheckbox("phaserope");
            addCheckbox("sticky");
            addCheckbox("climbkey");
            break;
        case THROW:
            addSlider("hookgravity");
            addSlider("throwspeed");
            addCheckbox("reelin");
            addSlider("verticalthrowangle");
            addSlider("sneakingverticalthrowangle");
            addCheckbox("detachonkeyrelease");
            break;
        case MOTOR:
            addCheckbox("motor");
            addSlider("motormaxspeed");
            addSlider("motoracceleration");
            addCheckbox("motorwhencrouching");
            addCheckbox("motorwhennotcrouching");
            addCheckbox("smartmotor");
            addCheckbox("motordampener");
            addCheckbox("pullbackwards");
            break;
        case SWING:
            addSlider("playermovementmult");
            break;
        case STAFF:
            addCheckbox("enderstaff");
            break;
        case FORCEFIELD:
            addCheckbox("repel");
            addSlider("repelforce");
            break;
        case MAGNET:
            addCheckbox("attract");
            addSlider("attractradius");
            break;
        case DOUBLE:
            addCheckbox("doublehook");
            addCheckbox("smartdoublemotor");
            addSlider("angle");
            addSlider("sneakingangle");
            addCheckbox("oneropepull");
            break;
        case ROCKET:
            addCheckbox("rocket");
            addSlider("rocket_force");
            addSlider("rocket_active_time");
            addSlider("rocket_refuel_ratio");
            addSlider("rocket_vertical_angle");
            break;
        default:
            break;
        }

        this.updateEnabled();
    }

    ButtonWidget buttonPressed = null;

    @Override
    public void onClose() {
        this.updateOptions();
        this.blockEntity.setCustomizationClient(customization);

        super.onClose();
    }

    private void updateOptions() {
        for (DrawableHelper b : this.options.keySet()) {
            this.updateOption(b);
        }
    }

    private void updateOption(DrawableHelper b) {
        if (b instanceof CheckboxWidget) {
            boolean checked = ((CheckboxWidget) b).isChecked();
            String option = options.get(b);
            customization.setBoolean(option, checked);
        } else if (b instanceof SliderWidget) {
            double d = ((Slider) b).getValue();
            d = Math.floor(d * 10 + 0.5) / 10;
            String option = options.get(b);
            customization.setDouble(option, d);
        }
        this.updateEnabled();
    }

    private void updateEnabled() {
        for (AbstractButtonWidget b : this.options.keySet()) {
            String option = this.options.get(b);
            boolean enabled = true;

            String desc = GrappleMod.proxy.localize(this.customization.getDescription(option));

            if (this.customization.isoptionvalid(option)) {
            } else {
                desc = GrappleMod.proxy.localize("grapplemodifier.incompatability.desc") + "\n" + desc;
                enabled = false;
            }

            int level = this.customization.optionEnabled(option);
            if (this.getLimits() < level) {
                if (level == 1) {
                    desc = GrappleMod.proxy.localize("grapplemodifier.limits.desc") + "\n" + desc;
                } else {
                    desc = GrappleMod.proxy.localize("grapplemodifier.locked.desc") + "\n" + desc;
                }
                enabled = false;
            }

            b.active = enabled;

            tooltips.put(b, desc);
        }
    }

    private int getLimits() {
        if (this.blockEntity.isUnlocked(GrappleMod.UpgradeCategories.LIMITS) || player.isCreative()) {
            return 1;
        }
        return 0;
    }

    private void button1(ButtonWidget b) {
        ((ClientPlayerEntity) player).closeScreen();
    }

    private void button2(ButtonWidget b) {
        this.customization = new GrappleCustomization();
    }

    private void button3(ButtonWidget b) {
        showingHelpScreen = false;
        this.updateOptions();
        clearScreen();
        mainScreen();
    }

    private void button4(ButtonWidget b) {
        showingHelpScreen = true;
        clearScreen();
        helpScreen();
    }

    private void button99(ButtonWidget b) {
        GrappleMod.UpgradeCategories category = GrappleMod.UpgradeCategories.values()[this.buttons.indexOf(b)];

        clearScreen();

        boolean unlocked = this.blockEntity.isUnlocked(category) || MinecraftClient.getInstance().player.isCreative();

        if (unlocked) {
            showCategoryScreen(category);
        } else {
            notAllowedScreen(category);
        }
    }

    @Environment(EnvType.CLIENT)
    protected void drawBackground(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {

        TextRenderer fontRenderer = client.textRenderer;
        if (fontRenderer == null) {
            return;
        }

        // background
        MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(matrices, guiLeft, guiTop, 0, 0, xSize, ySize);

        RenderSystem.translatef(guiLeft, guiTop, 0.0F);

        if (this.category != null) {
            if (!this.allowed) {
                fontRenderer.draw(matrices,
                                  GrappleMod.proxy.localize("grapplemodifier.unlock1.desc"),
                                  10,
                                  10,
                                  Color.darkGray.getRGB());
                fontRenderer.draw(matrices, this.category.description, 10, 25, Color.darkGray.getRGB());
                fontRenderer.draw(matrices,
                                  GrappleMod.proxy.localize("grapplemodifier.unlock2.desc"),
                                  10,
                                  40,
                                  Color.darkGray.getRGB());
                fontRenderer.draw(matrices,
                                  GrappleMod.proxy.localize("grapplemodifier.unlock3.desc"),
                                  10,
                                  55,
                                  Color.darkGray.getRGB());
                fontRenderer.draw(matrices, new ItemStack(this.category.item).getName(), 10, 70, Color.darkGray.getRGB());
                fontRenderer.draw(matrices,
                                  GrappleMod.proxy.localize("grapplemodifier.unlock4.desc"),
                                  10,
                                  85,
                                  Color.darkGray.getRGB());
            } else {

            }
        } else {
            if (showingHelpScreen) {
                String helptext = GrappleMod.proxy.localize("grapplemodifier.help.desc");
                int linenum = 0;
                for (String line : helptext.split(Pattern.quote("\\n"))) {
                    fontRenderer.draw(matrices, line, 10, 10 + 15 * linenum, Color.darkGray.getRGB());
                    linenum++;
                }
            } else {
                fontRenderer.draw(matrices,
                                  GrappleMod.proxy.localize("grapplemodifier.apply.desc"),
                                  10,
                                  this.ySize - 20 - 10 - 10,
                                  Color.darkGray.getRGB());
            }
        }

        RenderSystem.translatef(-guiLeft, -guiTop, 0.0F);

        RenderSystem.disableRescaleNormal();
        RenderSystem.disableLighting();
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();

        RenderSystem.enableLighting();
        RenderSystem.enableDepthTest();
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableLighting();

        for (AbstractButtonWidget b : this.tooltips.keySet()) {
            if (mouseX >= b.x && mouseY >= b.y && mouseX <= b.x + b.getWidth() && mouseY <= b.y + b.getHeight()) {
                this.drawStringWithShadow(matrices, fontRenderer, this.tooltips.get(b), mouseX, mouseY, 0); // TODO 0
            }
        }
    }

    // https://github.com/shedaniel/cloth-config/blob/665a4a56e2dfcc117637a46ca7a11681a34eb6bb/src/main/java/me/shedaniel/clothconfig2/gui/entries/IntegerSliderEntry.java
    private static class Slider extends SliderWidget {
        double min;

        double max;

        protected Slider(int int_1, int int_2, int int_3, int int_4, double double_1, double min, double max) {
            super(int_1, int_2, int_3, int_4, NarratorManager.EMPTY, double_1);
            this.min = min;
            this.max = max;
        }

        @Override
        public void updateMessage() {
        }

        @Override
        protected void applyValue() {
            this.value = min + Math.abs(max - min) * value;
        }

        public double getValue() {
            return value;
        }
    }
}
