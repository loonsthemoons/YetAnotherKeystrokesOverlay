package dev.loons.fancystrokes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.List;

public class StrokeEditScreen extends Screen {
    private final Strokes targetStroke;
    private final Screen parentScreen;

    private int panelStartX;
    private int panelStartY;
    private int panelWidth;
    private int panelHeight;
    private int fieldHeight;
    private int currentElementY;

    private RgbSlider rgbRedSlider;
    private RgbSlider rgbGreenSlider;
    private RgbSlider rgbBlueSlider;

    // inputTypeToggleButton wird jetzt zum "Durchklick"-Button
    private ButtonWidget inputTypeCycleButton;


    public StrokeEditScreen(Text title, Strokes strokeToEdit, Screen parent) {
        super(title);
        this.targetStroke = strokeToEdit;
        this.parentScreen = parent;
    }

    @Override
    protected void init() {
        super.init();

        this.panelWidth = 200;
        this.panelHeight = 250;
        this.panelStartX = this.width / 2 - this.panelWidth / 2;
        this.panelStartY = this.height / 2 - this.panelHeight / 2;
        this.currentElementY = this.panelStartY + 20;

        this.fieldHeight = 20;
        int sliderWidth = 150;
        int sliderStartX = this.panelStartX + (this.panelWidth - sliderWidth) / 2;

        // RGB-Slider initialisieren
        rgbRedSlider = new RgbSlider(sliderStartX, currentElementY, sliderWidth, fieldHeight, "R", ColorHelper.Argb.getRed(targetStroke.getColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshStrokeColorFromSliders();
            }
        };
        this.addDrawableChild(rgbRedSlider);
        currentElementY += fieldHeight + 5;

        rgbGreenSlider = new RgbSlider(sliderStartX, currentElementY, sliderWidth, fieldHeight, "G", ColorHelper.Argb.getGreen(targetStroke.getColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshStrokeColorFromSliders();
            }
        };
        this.addDrawableChild(rgbGreenSlider);
        currentElementY += fieldHeight + 5;

        rgbBlueSlider = new RgbSlider(sliderStartX, currentElementY, sliderWidth, fieldHeight, "B", ColorHelper.Argb.getBlue(targetStroke.getColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshStrokeColorFromSliders();
            }
        };
        this.addDrawableChild(rgbBlueSlider);
        currentElementY += fieldHeight + 15;

        this.inputTypeCycleButton = ButtonWidget.builder(Text.literal("Input Type: " + targetStroke.getInputType().name()), (button) -> {
            cycleInputType(1);
        }).dimensions(this.panelStartX + (this.panelWidth - 150) / 2, currentElementY, 150, 20).build();
        this.addDrawableChild(inputTypeCycleButton);
        currentElementY += 25;


        this.addDrawableChild(ButtonWidget.builder(Text.literal("Visible: " + targetStroke.isVisible()), (button) -> {
            targetStroke.setVisible(!targetStroke.isVisible());
            button.setMessage(Text.literal("Visible: " + targetStroke.isVisible()));
        }).dimensions(this.panelStartX + (this.panelWidth - 150) / 2, currentElementY, 150, 20).build());
        currentElementY += 25;


        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), (button) -> {
            this.close();
        }).dimensions(this.width / 2 - 75, this.height - 30, 150, 20).build());
    }

    private void refreshStrokeColorFromSliders() {
        int r = (int) (rgbRedSlider.getCurrentValue() * 255);
        int g = (int) (rgbGreenSlider.getCurrentValue() * 255);
        int b = (int) (rgbBlueSlider.getCurrentValue() * 255);

        int currentColor = targetStroke.getColor();
        int newColor = ColorHelper.Argb.getArgb(ColorHelper.Argb.getAlpha(currentColor), r, g, b);
        targetStroke.setColor(newColor);
    }

    private void cycleInputType(int direction) {
        Strokes.InputType[] types = Strokes.InputType.values();
        int currentIndex = targetStroke.getInputType().ordinal();
        int newIndex = (currentIndex + direction);

        if (newIndex < 0) {
            newIndex = types.length + newIndex;
        }
        newIndex %= types.length;

        Strokes.InputType newType = types[newIndex];
        targetStroke.setInputType(newType);
        inputTypeCycleButton.setMessage(Text.literal("Input Type: " + newType.name()));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        context.fill(panelStartX, panelStartY, panelStartX + panelWidth, panelStartY + panelHeight, 0xA0000000);
        context.drawBorder(panelStartX, panelStartY, panelWidth, panelHeight, 0xFFFFFFFF);

        super.render(context, mouseX, mouseY, delta);

        context.drawText(this.textRenderer, this.title, this.width / 2 - this.textRenderer.getWidth(this.title) / 2, 20, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, "Editing: " + targetStroke.getMessage().getString(), this.width / 2 - this.textRenderer.getWidth("Editing: " + targetStroke.getMessage().getString()) / 2, 40, 0xFFFFFF00, true);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(this.parentScreen);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (inputTypeCycleButton.isMouseOver(mouseX, mouseY)) {
            if (button == 0) {
                cycleInputType(1);
                return true;
            } else if (button == 1) {
                cycleInputType(-1);
                return true;
            }
        }
        return super.mouseClicked(mouseX,mouseY,button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 256) { // 256 is ESCAPE
            this.close();
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return super.charTyped(chr, modifiers);
    }
}