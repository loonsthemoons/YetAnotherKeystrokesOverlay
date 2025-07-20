package dev.loons.fancystrokes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

public class StrokeEditScreen extends Screen {
    private final Strokes targetStroke;
    private final Screen parentScreen;
    private final StrokesStructure structure;

    // --- NEUE FELDER FÜR DIE DREI PANELS ---
    private int fillColorPanelX, fillColorPanelY, fillColorPanelWidth, fillColorPanelHeight; // Ehemals unpressedColorPanel, jetzt für beide Füllfarben
    private int outlinePanelX, outlinePanelY, outlinePanelWidth, outlinePanelHeight;
    private int generalPanelX, generalPanelY, generalPanelWidth, generalPanelHeight;

    // Allgemeine Widget-Parameter
    private int sliderWidth = 150;
    private int fieldHeight = 20;

    // RGB Sliders für Unpressed Color
    private RgbSlider unpressedRedSlider;
    private RgbSlider unpressedGreenSlider;
    private RgbSlider unpressedBlueSlider;

    // RGB Sliders für Pressed Color
    private RgbSlider pressedRedSlider;
    private RgbSlider pressedGreenSlider;
    private RgbSlider pressedBlueSlider;

    // RGB Sliders für Outline-Farbe
    private RgbSlider outlineRedSlider;
    private RgbSlider outlineGreenSlider;
    private RgbSlider outlineBlueSlider;

    // RGB Sliders für Pressed-Outline-Farbe
    private RgbSlider pressedOutlineRedSlider;
    private RgbSlider pressedOutlineGreenSlider;
    private RgbSlider pressedOutlineBlueSlider;

    // General Settings Widgets
    private ButtonWidget applyGlobalButton;
    private ButtonWidget inputTypeCycleButton;
    private SliderWidget roundnessSlider;
    private ButtonWidget outlinesButton;

    // Done Button
    private ButtonWidget doneButton;


    public StrokeEditScreen(Text title, Strokes strokeToEdit, Screen parent, StrokesStructure structure) {
        super(title);
        this.targetStroke = strokeToEdit;
        this.parentScreen = parent;
        this.targetStroke.setSelected(true);
        this.structure = structure;
    }

    @Override
    protected void init() {
        super.init();

        // --- Panel-Dimensionen und Positionen berechnen ---
        int panelCount = 3; // Jetzt wieder 3 Panels
        int panelSpacing = 20; // Abstand zwischen den Boxen
        int panelHeight = 2 * (fieldHeight + 5) * 3 + 20 + 20 + 30; // 2 Sets von 3 Sliderrn + Abstände + Padding
        int titleOffset = 20; // Platz für den Titel über der Box

        // Gesamte benötigte Breite und Start-X für die Zentrierung
        int totalWidthNeeded = (sliderWidth + 50) * panelCount + (panelSpacing * (panelCount - 1));
        int startX = (this.width - totalWidthNeeded) / 2;
        int panelStartYOffset = this.height / 2 - (panelHeight + titleOffset) / 2; // Y-Startpunkt für die Panels inkl. Titel


        // Fill Color Panel (enthält Unpressed und Pressed Color Slider)
        fillColorPanelX = startX;
        fillColorPanelY = panelStartYOffset + titleOffset; // Panel beginnt unter dem Titel
        fillColorPanelWidth = sliderWidth + 50;
        fillColorPanelHeight = panelHeight;
        addFillColorSettings(fillColorPanelX, fillColorPanelY, fillColorPanelWidth, fillColorPanelHeight);

        // Outline-Farben-Panel
        outlinePanelX = fillColorPanelX + fillColorPanelWidth + panelSpacing;
        outlinePanelY = panelStartYOffset + titleOffset;
        outlinePanelWidth = sliderWidth + 50;
        outlinePanelHeight = panelHeight;
        addOutlineColorSettings(outlinePanelX, outlinePanelY, outlinePanelWidth, outlinePanelHeight);

        // General Settings Panel
        generalPanelX = outlinePanelX + outlinePanelWidth + panelSpacing;
        generalPanelY = panelStartYOffset + titleOffset;
        generalPanelWidth = sliderWidth + 50;
        generalPanelHeight = panelHeight;
        addGeneralSettings(generalPanelX, generalPanelY, generalPanelWidth, generalPanelHeight);


        // Done Button (unten mittig, wie zuvor)
        doneButton = ButtonWidget.builder(Text.literal("Done"), (button) -> {
            this.close();
        }).dimensions(this.width / 2 - 75, this.height - 30, 150, 20).build();
        this.addDrawableChild(doneButton);
    }

    // --- Hilfsmethoden zum Hinzufügen der Widgets für jedes Panel ---
    private void addFillColorSettings(int panelX, int panelY, int panelW, int panelH) {
        int currentY = panelY + 5; // Start Y innerhalb des Panels (nach dem Titel-Abstand)
        int sliderStartX = panelX + (panelW - sliderWidth) / 2; // Slider zentriert im Panel

        // Titel für Unpressed Color innerhalb der Box
        this.addDrawableChild(new TextLabelWidget(sliderStartX, currentY, sliderWidth, Text.literal("Unpressed Color")));
        currentY += fieldHeight + 5;

        // Unpressed Color Sliders
        unpressedRedSlider = new RgbSlider(sliderStartX, currentY, sliderWidth, fieldHeight, "R", ColorHelper.Argb.getRed(targetStroke.getColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshUnpressedColorFromSliders();
            }
        };
        this.addDrawableChild(unpressedRedSlider);
        currentY += fieldHeight + 5;

        unpressedGreenSlider = new RgbSlider(sliderStartX, currentY, sliderWidth, fieldHeight, "G", ColorHelper.Argb.getGreen(targetStroke.getColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshUnpressedColorFromSliders();
            }
        };
        this.addDrawableChild(unpressedGreenSlider);
        currentY += fieldHeight + 5;

        unpressedBlueSlider = new RgbSlider(sliderStartX, currentY, sliderWidth, fieldHeight, "B", ColorHelper.Argb.getBlue(targetStroke.getColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshUnpressedColorFromSliders();
            }
        };
        this.addDrawableChild(unpressedBlueSlider);
        currentY += fieldHeight + 15; // Abstand zwischen Unpressed und Pressed Color Sliders

        // Titel für Pressed Color innerhalb der Box
        this.addDrawableChild(new TextLabelWidget(sliderStartX, currentY, sliderWidth, Text.literal("Pressed Color")));
        currentY += fieldHeight + 5;

        // Pressed Color Sliders
        pressedRedSlider = new RgbSlider(sliderStartX, currentY, sliderWidth, fieldHeight, "R", ColorHelper.Argb.getRed(targetStroke.getPressedColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshPressedColorFromSliders();
            }
        };
        this.addDrawableChild(pressedRedSlider);
        currentY += fieldHeight + 5;

        pressedGreenSlider = new RgbSlider(sliderStartX, currentY, sliderWidth, fieldHeight, "G", ColorHelper.Argb.getGreen(targetStroke.getPressedColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshPressedColorFromSliders();
            }
        };
        this.addDrawableChild(pressedGreenSlider);
        currentY += fieldHeight + 5;

        pressedBlueSlider = new RgbSlider(sliderStartX, currentY, sliderWidth, fieldHeight, "B", ColorHelper.Argb.getBlue(targetStroke.getPressedColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshPressedColorFromSliders();
            }
        };
        this.addDrawableChild(pressedBlueSlider);
    }


    private void addOutlineColorSettings(int panelX, int panelY, int panelW, int panelH) {
        int currentY = panelY + 5;
        int sliderStartX = panelX + (panelW - sliderWidth) / 2;

        // Titel für Unpressed Outline Color innerhalb der Box
        this.addDrawableChild(new TextLabelWidget(sliderStartX, currentY, sliderWidth, Text.literal("Unpressed Outline Color")));
        currentY += fieldHeight + 5;

        outlineRedSlider = new RgbSlider(sliderStartX, currentY, sliderWidth, fieldHeight, "R", ColorHelper.Argb.getRed(targetStroke.getOutlineColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshOutlineColorFromSliders();
            }
        };
        this.addDrawableChild(outlineRedSlider);
        currentY += fieldHeight + 5;

        outlineGreenSlider = new RgbSlider(sliderStartX, currentY, sliderWidth, fieldHeight, "G", ColorHelper.Argb.getGreen(targetStroke.getOutlineColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshOutlineColorFromSliders();
            }
        };
        this.addDrawableChild(outlineGreenSlider);
        currentY += fieldHeight + 5;

        outlineBlueSlider = new RgbSlider(sliderStartX, currentY, sliderWidth, fieldHeight, "B", ColorHelper.Argb.getBlue(targetStroke.getOutlineColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshOutlineColorFromSliders();
            }
        };
        this.addDrawableChild(outlineBlueSlider);

        currentY += fieldHeight + 15; // Abstand zwischen Unpressed und Pressed Outline Color Sliders

        // Titel für Pressed Outline Color innerhalb der Box
        this.addDrawableChild(new TextLabelWidget(sliderStartX, currentY, sliderWidth, Text.literal("Pressed Outline Color")));
        currentY += fieldHeight + 5;

        pressedOutlineRedSlider = new RgbSlider(sliderStartX, currentY, sliderWidth, fieldHeight, "R", ColorHelper.Argb.getRed(targetStroke.getPressedOutlineColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshPressedOutlineColorFromSliders();
            }
        };
        this.addDrawableChild(pressedOutlineRedSlider);
        currentY += fieldHeight + 5;

        pressedOutlineGreenSlider = new RgbSlider(sliderStartX, currentY, sliderWidth, fieldHeight, "G", ColorHelper.Argb.getGreen(targetStroke.getPressedOutlineColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshPressedOutlineColorFromSliders();
            }
        };
        this.addDrawableChild(pressedOutlineGreenSlider);
        currentY += fieldHeight + 5;

        pressedOutlineBlueSlider = new RgbSlider(sliderStartX, currentY, sliderWidth, fieldHeight, "B", ColorHelper.Argb.getBlue(targetStroke.getPressedOutlineColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshPressedOutlineColorFromSliders();
            }
        };
        this.addDrawableChild(pressedOutlineBlueSlider);
    }

    private void addGeneralSettings(int panelX, int panelY, int panelW, int panelH) {
        int currentY = panelY + 5;
        int elementStartX = panelX + (panelW - sliderWidth) / 2; // Elemente zentriert im Panel

        // Apply Global Button
        applyGlobalButton = ButtonWidget.builder(Text.of("Apply settings globally"), (button) -> {
            this.client.getToastManager().add(
                    SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE, Text.of("Settings!"), Text.of("Config applied globally."))
            );

            for(Strokes strokes : structure.getStrokes()){
                strokes.setColor(targetStroke.getColor());
                strokes.setPressedColor(targetStroke.getPressedColor());
                strokes.setOutlineStatus(targetStroke.getOutlineStatus());
                strokes.setOutlineColor(targetStroke.getOutlineColor());
                strokes.setPressedOutlineColor(targetStroke.getPressedOutlineColor());
                strokes.setRoundness(targetStroke.getRoundness());
            }

        }).dimensions(elementStartX, currentY, sliderWidth, fieldHeight).build();
        this.addDrawableChild(applyGlobalButton);
        currentY += fieldHeight + 5;

        // InputType Cycle Button
        inputTypeCycleButton = ButtonWidget.builder(Text.literal("Input Type: " + targetStroke.getInputType().name()), (button) -> {
            cycleInputType(1);
        }).dimensions(elementStartX, currentY, sliderWidth, fieldHeight).build();
        this.addDrawableChild(inputTypeCycleButton);
        currentY += fieldHeight + 5;

        // Roundness Slider
        roundnessSlider = new SliderWidget(elementStartX, currentY, sliderWidth, fieldHeight, Text.literal("Roundness"), targetStroke.getRoundness() / 100.0D) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Roundness: " + (int) (this.value * 100)));
            }

            @Override
            protected void applyValue() {
                targetStroke.setRoundness((int) (this.value * 100));
            }
        };
        this.addDrawableChild(roundnessSlider);
        currentY += fieldHeight + 5;

        // Outlines Button
        outlinesButton = ButtonWidget.builder(Text.literal("Outlines: " + targetStroke.getOutlineStatus()), (button) -> {
            targetStroke.setOutlineStatus(!targetStroke.getOutlineStatus());
            outlinesButton.setMessage(Text.literal("Outlines: " + targetStroke.getOutlineStatus()));
        }).dimensions(elementStartX, currentY, sliderWidth, fieldHeight).build();
        this.addDrawableChild(outlinesButton);
        currentY += fieldHeight + 5;
    }


    // --- Hilfsmethoden zum Aktualisieren der Farben ---

    private void refreshUnpressedColorFromSliders() {
        int r = (int) (unpressedRedSlider.getCurrentValue() * 255);
        int g = (int) (unpressedGreenSlider.getCurrentValue() * 255);
        int b = (int) (unpressedBlueSlider.getCurrentValue() * 255);

        int currentColor = targetStroke.getColor();
        int newColor = ColorHelper.Argb.getArgb(ColorHelper.Argb.getAlpha(currentColor), r, g, b);
        targetStroke.setColor(newColor);
    }

    private void refreshPressedColorFromSliders() {
        int r = (int) (pressedRedSlider.getCurrentValue() * 255);
        int g = (int) (pressedGreenSlider.getCurrentValue() * 255);
        int b = (int) (pressedBlueSlider.getCurrentValue() * 255);

        int currentColor = targetStroke.getPressedColor();
        int newColor = ColorHelper.Argb.getArgb(ColorHelper.Argb.getAlpha(currentColor), r, g, b);
        targetStroke.setPressedColor(newColor);
    }

    private void refreshOutlineColorFromSliders() {
        int r = (int) (outlineRedSlider.getCurrentValue() * 255);
        int g = (int) (outlineGreenSlider.getCurrentValue() * 255);
        int b = (int) (outlineBlueSlider.getCurrentValue() * 255);

        int currentAlpha = ColorHelper.Argb.getAlpha(targetStroke.getOutlineColor());
        int newColor = ColorHelper.Argb.getArgb(currentAlpha, r, g, b);
        targetStroke.setOutlineColor(newColor);
    }

    private void refreshPressedOutlineColorFromSliders() {
        int r = (int) (pressedOutlineRedSlider.getCurrentValue() * 255);
        int g = (int) (pressedOutlineGreenSlider.getCurrentValue() * 255);
        int b = (int) (pressedOutlineBlueSlider.getCurrentValue() * 255);

        int currentAlpha = ColorHelper.Argb.getAlpha(targetStroke.getPressedOutlineColor());
        int newColor = ColorHelper.Argb.getArgb(currentAlpha, r, g, b);
        targetStroke.setPressedOutlineColor(newColor);
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

        // --- Render die Panel-Hintergründe und Titel ---
        renderPanelWithTitle(context, fillColorPanelX, fillColorPanelY, fillColorPanelWidth, fillColorPanelHeight, Text.literal("Fill Color")); // NEU: Fill Color für beide Füllfarben
        renderPanelWithTitle(context, outlinePanelX, outlinePanelY, outlinePanelWidth, outlinePanelHeight, Text.literal("Outlines"));
        renderPanelWithTitle(context, generalPanelX, generalPanelY, generalPanelWidth, generalPanelHeight, Text.literal("General Settings"));
        // ------------------------------------------------

        super.render(context, mouseX, mouseY, delta); // Rendert alle Widgets (inkl. Slider)

        // Titel des Screens zeichnen
        context.drawText(this.textRenderer, this.title, this.width / 2 - this.textRenderer.getWidth(this.title) / 2, 20, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, "Editing: " + targetStroke.getMessage().getString(), this.width / 2 - this.textRenderer.getWidth("Editing: " + targetStroke.getMessage().getString()) / 2, 40, 0xFFFFFF00, true);
    }

    private void renderPanelWithTitle(DrawContext context, int x, int y, int width, int height, Text title) {
        // Titel über der Box rendern
        int titleY = y - 15; // 15 Pixel über der Box
        context.drawText(this.textRenderer, title, x + width / 2 - this.textRenderer.getWidth(title) / 2, titleY, 0xFFFFFFFF, true);

        // Box rendern
        context.fill(x, y, x + width, y + height, 0xA0000000); // Dunkler Hintergrund
        context.drawBorder(x, y, width, height, 0xFFFFFFFF); // Weißer Rand
    }


    @Override
    public void close() {
        this.targetStroke.setSelected(false);
        MinecraftClient.getInstance().setScreen(this.parentScreen);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle clicks on inputTypeCycleButton
        if (inputTypeCycleButton.isMouseOver(mouseX, mouseY)) {
            if (button == 0) { // Linksklick
                cycleInputType(1); // Vorwärts
                return true;
            } else if (button == 1) { // Rechtsklick
                cycleInputType(-1); // Rückwärts
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
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

    private static class TextLabelWidget extends ButtonWidget {
        public TextLabelWidget(int x, int y, int width, Text message) {
            super(x, y, width, 20, message, (btn) -> {}, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
            this.active = false;
            this.visible = true;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            context.drawText(MinecraftClient.getInstance().textRenderer, this.getMessage(), this.getX() + this.getWidth() / 2 - MinecraftClient.getInstance().textRenderer.getWidth(this.getMessage()) / 2, this.getY() + (this.getHeight() - MinecraftClient.getInstance().textRenderer.fontHeight) / 2, 0xFFFFFFFF, true);
        }

        @Override
        protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        }
    }
}