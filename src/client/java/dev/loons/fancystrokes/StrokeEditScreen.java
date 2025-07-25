package dev.loons.fancystrokes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

/**
 * A screen for editing the properties of a single {@link Strokes} object.
 * This screen provides sliders for color adjustments, toggles for outlines and text,
 * a slider for roundness, and buttons for global settings or resetting configurations.
 */
public class StrokeEditScreen extends Screen {
    private final Strokes targetStroke;
    private final Screen parentScreen;
    private final StrokesStructure structure;

    private int fillColorPanelX, fillColorPanelY, fillColorPanelWidth, fillColorPanelHeight;
    private int outlinePanelX, outlinePanelY, outlinePanelWidth, outlinePanelHeight;
    private int textPanelX, textPanelY, textPanelWidth, textPanelHeight;
    private int generalPanelX, generalPanelY, generalPanelWidth, generalPanelHeight;
    int panelSpacing;
    int buttonWidth;
    int buttonHeight;
    int pageButtonWidth;
    int pageButtonHeight;

    // General widget parameters
    private int fieldWidth = 150;
    private int fieldHeight = 20;

    // RGB Sliders for Unpressed Color
    private RgbSlider unpressedRedSlider;
    private RgbSlider unpressedGreenSlider;
    private RgbSlider unpressedBlueSlider;

    // RGB Sliders for Pressed Color
    private RgbSlider pressedRedSlider;
    private RgbSlider pressedGreenSlider;
    private RgbSlider pressedBlueSlider;

    // RGB Sliders for Outline Color
    private RgbSlider outlineRedSlider;
    private RgbSlider outlineGreenSlider;
    private RgbSlider outlineBlueSlider;

    // RGB Sliders for Pressed-Outline Color
    private RgbSlider pressedOutlineRedSlider;
    private RgbSlider pressedOutlineGreenSlider;
    private RgbSlider pressedOutlineBlueSlider;

    // RGB Sliders for Text Color
    private RgbSlider textRedSlider;
    private RgbSlider textGreenSlider;
    private RgbSlider textBlueSlider;

    // RGB Sliders for Pressed-Text Color
    private RgbSlider pressedTextRedSlider;
    private RgbSlider pressedTextGreenSlider;
    private RgbSlider pressedTextBlueSlider;

    // Text Widgets for color menus
    private TextLabelWidget unpressedColorText;
    private TextLabelWidget pressedColorText;
    private TextLabelWidget unpressedOutlineText;
    private TextLabelWidget pressedOutlineText;
    private TextLabelWidget unpressedTextText;
    private TextLabelWidget pressedTextText;

    // General Settings Widgets
    private ButtonWidget applyGlobalButton;
    private ButtonWidget resetButton;
    private ButtonWidget inputTypeCycleButton;
    private SliderWidget roundnessSlider;
    private ButtonWidget outlinesButton;
    private ButtonWidget textButton;
    private RgbSlider transparencySlider;
    private TextFieldWidget displayTextInput;

    // Done Button
    private ButtonWidget doneButton;
    private ButtonWidget rightPageButton;
    private ButtonWidget leftPageButton;
    private boolean page1=true;

    /**
     * Constructs a new StrokeEditScreen.
     *
     * @param title The title of the screen.
     * @param strokeToEdit The {@link Strokes} object to be edited.
     * @param parent The parent screen to return to when this screen is closed.
     * @param structure The {@link StrokesStructure} managing all strokes.
     */
    public StrokeEditScreen(Text title, Strokes strokeToEdit, Screen parent, StrokesStructure structure) {
        super(title);
        this.targetStroke = strokeToEdit;
        this.parentScreen = parent;
        this.targetStroke.setSelected(true);
        this.structure = structure;
    }

    /**
     * Initializes the screen elements. This method is called when the screen is opened.
     * It calculates panel dimensions, positions, and adds all necessary widgets (sliders, buttons) to the screen.
     */
    @Override
    protected void init() {
        super.init();
        setPage1();
        // Done Button
        doneButton = ButtonWidget.builder(Text.literal("Done"), (button) -> {
            this.close();
        }).dimensions(this.width / 2 - 75, this.height - 30, 150, 20).build();
        this.addDrawableChild(doneButton);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
    }

    private void calculateDynamicDimensions() {
        int screenW = this.width;
        int screenH = this.height;

        double uiWidthPercentage = 0.9;
        int effectiveUIWidth = (int) (screenW * uiWidthPercentage);

        int maxFieldWidth=200;
        int maxFieldHeight=20;

        int panelCount = 3;
        int minPanelWidth = 100;

        panelSpacing = (int) (screenW * 0.02);
        if (panelSpacing < 10) panelSpacing = 10;

        generalPanelWidth = (effectiveUIWidth - (panelSpacing * (panelCount - 1))) / panelCount;
        if (generalPanelWidth < minPanelWidth) generalPanelWidth = minPanelWidth;
        if (generalPanelWidth > maxFieldWidth) generalPanelWidth = maxFieldWidth;

        fillColorPanelWidth = generalPanelWidth;
        outlinePanelWidth = generalPanelWidth;
        textPanelWidth = generalPanelWidth;

        fieldHeight = (int) (screenH * 0.06);
        if (fieldHeight < 15) fieldHeight = 15;
        if (fieldHeight > maxFieldHeight) fieldHeight = maxFieldHeight;

        fieldWidth = generalPanelWidth - (int)(generalPanelWidth * 0.12);
        if (fieldWidth < 80) fieldWidth = 80;
        if (fieldWidth > maxFieldWidth) fieldWidth = maxFieldWidth;

        generalPanelHeight = (fieldHeight + 5) * 8 + 20;
        fillColorPanelHeight = generalPanelHeight;
        outlinePanelHeight = generalPanelHeight;
        textPanelHeight = generalPanelHeight;

        int totalWidthNeeded = (generalPanelWidth) * panelCount + (panelSpacing * (panelCount - 1));
        int startX = (screenW - totalWidthNeeded) / 2;
        int verticalOffsetForCentering = (screenH - generalPanelHeight) / 2;

        generalPanelX = startX;
        generalPanelY = verticalOffsetForCentering;

        fillColorPanelX = startX;
        fillColorPanelY = verticalOffsetForCentering;

        outlinePanelX = fillColorPanelX + fillColorPanelWidth + panelSpacing;
        outlinePanelY = verticalOffsetForCentering;

        textPanelX = outlinePanelX + outlinePanelWidth + panelSpacing;
        textPanelY = verticalOffsetForCentering;

        buttonWidth = (int) (screenW * 0.1);
        if (buttonWidth < 100) buttonWidth = 100;
        buttonHeight = (int) (screenH * 0.04);
        if (buttonHeight < 20) buttonHeight = 20;

        pageButtonWidth = (int) (screenW * 0.03);
        if (pageButtonWidth < 20) pageButtonWidth = 20;
        pageButtonHeight = buttonHeight;
    }

    // general settings and statistic
    public void setPage1(){
        // Calculate panel dimensions and positions
        calculateDynamicDimensions();
        addGeneralSettings(generalPanelX, generalPanelY, generalPanelWidth, generalPanelHeight);

        // Page Switch Button
        rightPageButton = ButtonWidget.builder(Text.literal(">"), (button -> {
            setPage2();
            removeGeneralSettings();
            page1=false;
            remove(rightPageButton);
        })).dimensions(this.width / 2 + 80, this.height - 30, 20, 20).build();
        this.addDrawableChild(rightPageButton);
    }

    // page for color settings
    public void setPage2(){
        // Calculate panel dimensions and positions
        calculateDynamicDimensions();

        // Fill Color Panel
        addFillColorSettings(fillColorPanelX, fillColorPanelY, fillColorPanelWidth, fillColorPanelHeight);
        addOutlineColorSettings(outlinePanelX, outlinePanelY, outlinePanelWidth, outlinePanelHeight);
        addTextColorSettings(textPanelX, textPanelY, textPanelWidth, textPanelHeight);

        // Page Switch Button
        leftPageButton = ButtonWidget.builder(Text.literal("<"), (button -> {
            setPage1();
            page1=true;
            removeColorSettings();
            remove(leftPageButton);
        })).dimensions(this.width / 2 - 100, this.height - 30, 20, 20).build();
        this.addDrawableChild(leftPageButton);
    }

    /**
     * Helper method to add widgets for the Fill Color Panel.
     * This includes sliders for unpressed and pressed fill colors (RGB components).
     *
     * @param panelX The X-coordinate of the panel.
     * @param panelY The Y-coordinate of the panel.
     * @param panelW The width of the panel.
     * @param panelH The height of the panel.
     */
    private void addFillColorSettings(int panelX, int panelY, int panelW, int panelH) {
        int currentY = panelY + 5;
        int sliderStartX = panelX + (panelW - fieldWidth) / 2;

        // Title for Unpressed Color within the box
        unpressedColorText = new TextLabelWidget(sliderStartX, currentY, fieldWidth, Text.literal("Unpressed Color"));
        this.addDrawableChild(unpressedColorText);
        currentY += fieldHeight + 5;

        // Unpressed Color Sliders
        unpressedRedSlider = new RgbSlider(sliderStartX, currentY, fieldWidth, fieldHeight, "R", ColorHelper.Argb.getRed(targetStroke.getColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshUnpressedColorFromSliders();
            }
        };
        this.addDrawableChild(unpressedRedSlider);
        currentY += fieldHeight + 5;

        unpressedGreenSlider = new RgbSlider(sliderStartX, currentY, fieldWidth, fieldHeight, "G", ColorHelper.Argb.getGreen(targetStroke.getColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshUnpressedColorFromSliders();
            }
        };
        this.addDrawableChild(unpressedGreenSlider);
        currentY += fieldHeight + 5;

        unpressedBlueSlider = new RgbSlider(sliderStartX, currentY, fieldWidth, fieldHeight, "B", ColorHelper.Argb.getBlue(targetStroke.getColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshUnpressedColorFromSliders();
            }
        };
        this.addDrawableChild(unpressedBlueSlider);
        currentY += fieldHeight + 15;

        // Title for Pressed Color within the box
        pressedColorText = new TextLabelWidget(sliderStartX, currentY, fieldWidth, Text.literal("Pressed Color"));
        this.addDrawableChild(pressedColorText);
        currentY += fieldHeight + 5;

        // Pressed Color Sliders
        pressedRedSlider = new RgbSlider(sliderStartX, currentY, fieldWidth, fieldHeight, "R", ColorHelper.Argb.getRed(targetStroke.getPressedColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshPressedColorFromSliders();
            }
        };
        this.addDrawableChild(pressedRedSlider);
        currentY += fieldHeight + 5;

        pressedGreenSlider = new RgbSlider(sliderStartX, currentY, fieldWidth, fieldHeight, "G", ColorHelper.Argb.getGreen(targetStroke.getPressedColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshPressedColorFromSliders();
            }
        };
        this.addDrawableChild(pressedGreenSlider);
        currentY += fieldHeight + 5;

        pressedBlueSlider = new RgbSlider(sliderStartX, currentY, fieldWidth, fieldHeight, "B", ColorHelper.Argb.getBlue(targetStroke.getPressedColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshPressedColorFromSliders();
            }
        };
        this.addDrawableChild(pressedBlueSlider);
    }


    /**
     * Helper method to add widgets for the Outline Color Panel.
     * This includes sliders for unpressed and pressed outline colors (RGB components).
     *
     * @param panelX The X-coordinate of the panel.
     * @param panelY The Y-coordinate of the panel.
     * @param panelW The width of the panel.
     * @param panelH The height of the panel.
     */
    private void addOutlineColorSettings(int panelX, int panelY, int panelW, int panelH) {
        int currentY = panelY + 5;
        int sliderStartX = panelX + (panelW - fieldWidth) / 2;

        // Title for Unpressed Outline Color within the box
        unpressedOutlineText = new TextLabelWidget(sliderStartX, currentY, fieldWidth, Text.literal("Unpressed Outline Color"));
        this.addDrawableChild(unpressedOutlineText);
        currentY += fieldHeight + 5;

        outlineRedSlider = new RgbSlider(sliderStartX, currentY, fieldWidth, fieldHeight, "R", ColorHelper.Argb.getRed(targetStroke.getOutlineColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshOutlineColorFromSliders();
            }
        };
        this.addDrawableChild(outlineRedSlider);
        currentY += fieldHeight + 5;

        outlineGreenSlider = new RgbSlider(sliderStartX, currentY, fieldWidth, fieldHeight, "G", ColorHelper.Argb.getGreen(targetStroke.getOutlineColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshOutlineColorFromSliders();
            }
        };
        this.addDrawableChild(outlineGreenSlider);
        currentY += fieldHeight + 5;

        outlineBlueSlider = new RgbSlider(sliderStartX, currentY, fieldWidth, fieldHeight, "B", ColorHelper.Argb.getBlue(targetStroke.getOutlineColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshOutlineColorFromSliders();
            }
        };
        this.addDrawableChild(outlineBlueSlider);

        currentY += fieldHeight + 15;

        // Title for Pressed Outline Color within the box
        pressedOutlineText = new TextLabelWidget(sliderStartX, currentY, fieldWidth, Text.literal("Pressed Outline Color"));
        this.addDrawableChild(pressedOutlineText);
        currentY += fieldHeight + 5;

        pressedOutlineRedSlider = new RgbSlider(sliderStartX, currentY, fieldWidth, fieldHeight, "R", ColorHelper.Argb.getRed(targetStroke.getPressedOutlineColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshPressedOutlineColorFromSliders();
            }
        };
        this.addDrawableChild(pressedOutlineRedSlider);
        currentY += fieldHeight + 5;

        pressedOutlineGreenSlider = new RgbSlider(sliderStartX, currentY, fieldWidth, fieldHeight, "G", ColorHelper.Argb.getGreen(targetStroke.getPressedOutlineColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshPressedOutlineColorFromSliders();
            }
        };
        this.addDrawableChild(pressedOutlineGreenSlider);
        currentY += fieldHeight + 5;

        pressedOutlineBlueSlider = new RgbSlider(sliderStartX, currentY, fieldWidth, fieldHeight, "B", ColorHelper.Argb.getBlue(targetStroke.getPressedOutlineColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshPressedOutlineColorFromSliders();
            }
        };
        this.addDrawableChild(pressedOutlineBlueSlider);
    }

    private void addTextColorSettings(int panelX, int panelY, int panelW, int panelH) {
        int currentY = panelY + 5;
        int sliderStartX = panelX + (panelW - fieldWidth) / 2;

        // Title for Unpressed Text Color within the box
        unpressedTextText = new TextLabelWidget(sliderStartX, currentY, fieldWidth, Text.literal("Unpressed Text Color"));
        this.addDrawableChild(unpressedTextText);
        currentY += fieldHeight + 5;

        textRedSlider = new RgbSlider(sliderStartX, currentY, fieldWidth, fieldHeight, "R", ColorHelper.Argb.getRed(targetStroke.getTextColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshTextColorFromSliders();
            }
        };
        this.addDrawableChild(textRedSlider);
        currentY += fieldHeight + 5;

        textGreenSlider = new RgbSlider(sliderStartX, currentY, fieldWidth, fieldHeight, "G", ColorHelper.Argb.getGreen(targetStroke.getTextColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshTextColorFromSliders();
            }
        };
        this.addDrawableChild(textGreenSlider);
        currentY += fieldHeight + 5;

        textBlueSlider = new RgbSlider(sliderStartX, currentY, fieldWidth, fieldHeight, "B", ColorHelper.Argb.getBlue(targetStroke.getTextColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshTextColorFromSliders();
            }
        };
        this.addDrawableChild(textBlueSlider);

        currentY += fieldHeight + 15;

        // Title for Pressed Outline Color within the box
        pressedTextText = new TextLabelWidget(sliderStartX, currentY, fieldWidth, Text.literal("Pressed Text Color"));
        this.addDrawableChild(pressedTextText);
        currentY += fieldHeight + 5;

        pressedTextRedSlider = new RgbSlider(sliderStartX, currentY, fieldWidth, fieldHeight, "R", ColorHelper.Argb.getRed(targetStroke.getPressedTextColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshPressedTextColorFromSliders();
            }
        };
        this.addDrawableChild(pressedTextRedSlider);
        currentY += fieldHeight + 5;

        pressedTextGreenSlider = new RgbSlider(sliderStartX, currentY, fieldWidth, fieldHeight, "G", ColorHelper.Argb.getGreen(targetStroke.getPressedTextColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshPressedTextColorFromSliders();
            }
        };
        this.addDrawableChild(pressedTextGreenSlider);
        currentY += fieldHeight + 5;

        pressedTextBlueSlider = new RgbSlider(sliderStartX, currentY, fieldWidth, fieldHeight, "B", ColorHelper.Argb.getBlue(targetStroke.getPressedTextColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                refreshPressedTextColorFromSliders();
            }
        };
        this.addDrawableChild(pressedTextBlueSlider);
    }

    private void removeColorSettings(){
        this.remove(unpressedColorText);
        this.remove(unpressedRedSlider);
        this.remove(unpressedBlueSlider);
        this.remove(unpressedGreenSlider);
        this.remove(pressedColorText);
        this.remove(pressedRedSlider);
        this.remove(pressedBlueSlider);
        this.remove(pressedGreenSlider);
        this.remove(unpressedOutlineText);
        this.remove(outlineRedSlider);
        this.remove(outlineBlueSlider);
        this.remove(outlineGreenSlider);
        this.remove(pressedOutlineText);
        this.remove(pressedOutlineRedSlider);
        this.remove(pressedOutlineBlueSlider);
        this.remove(pressedOutlineGreenSlider);
        this.remove(unpressedTextText);
        this.remove(textRedSlider);
        this.remove(textBlueSlider);
        this.remove(textGreenSlider);
        this.remove(pressedTextText);
        this.remove(pressedTextRedSlider);
        this.remove(pressedTextBlueSlider);
        this.remove(pressedTextGreenSlider);
    }

    /**
     * Helper method to add widgets for the General Settings Panel.
     * This includes buttons for applying settings globally, resetting configurations,
     * cycling input type, toggling outlines and text, and a slider for roundness.
     *
     * @param panelX The X-coordinate of the panel.
     * @param panelY The Y-coordinate of the panel.
     * @param panelW The width of the panel.
     * @param panelH The height of the panel.
     */
    private void addGeneralSettings(int panelX, int panelY, int panelW, int panelH) {
        int currentY = panelY + 10;
        int elementStartX = panelX + (panelW - fieldWidth) / 2;

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
                strokes.setShowKeybindText(targetStroke.isShowKeybindText());
                strokes.setTextColor(targetStroke.getTextColor());
                strokes.setPressedTextColor(targetStroke.getPressedTextColor());
            }
            YetAnotherKeystrokesModClient.saveProfilesToConfig();

        }).dimensions(elementStartX, currentY, fieldWidth, fieldHeight).build();
        this.addDrawableChild(applyGlobalButton);
        currentY += fieldHeight + 5;

        // Reset Button
        resetButton = ButtonWidget.builder(Text.of("Reset config"), (button) -> {
            this.client.getToastManager().add(
                    SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE, Text.of("Config reset!"), Text.of("Default config loaded."))
            );
            structure.clearAll();
            structure.newDefaultStrokes();
            this.init(); // Re-initialize the screen to reflect default strokes

        }).dimensions(elementStartX, currentY, fieldWidth, fieldHeight).build();
        this.addDrawableChild(resetButton);
        currentY += fieldHeight + 5;

        // InputType Cycle Button
        inputTypeCycleButton = ButtonWidget.builder(Text.literal("Input Type: " + targetStroke.getInputType().name()), (button) -> {
            cycleInputType(1);
        }).dimensions(elementStartX, currentY, fieldWidth, fieldHeight).build();
        this.addDrawableChild(inputTypeCycleButton);
        currentY += fieldHeight + 5;

        // Outlines Button
        outlinesButton = ButtonWidget.builder(Text.literal("Outlines: " + targetStroke.getOutlineStatus()), (button) -> {
            targetStroke.setOutlineStatus(!targetStroke.getOutlineStatus());
            outlinesButton.setMessage(Text.literal("Outlines: " + targetStroke.getOutlineStatus()));
        }).dimensions(elementStartX, currentY, fieldWidth, fieldHeight).build();
        this.addDrawableChild(outlinesButton);
        currentY += fieldHeight + 5;

        // Roundness Slider
        roundnessSlider = new SliderWidget(elementStartX, currentY, fieldWidth, fieldHeight, Text.literal("Roundness"), targetStroke.getRoundness() / 15.0D) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Roundness: " + (int) (this.value * 15.0D)));
            }

            @Override
            protected void applyValue() {
                targetStroke.setRoundness((int) (this.value * 15.0D));
            }
        };
        this.addDrawableChild(roundnessSlider);
        currentY += fieldHeight + 5;

        // Text Button
        textButton = ButtonWidget.builder(Text.literal("Text: " + targetStroke.isShowKeybindText()), (button) -> {
            targetStroke.setShowKeybindText(!targetStroke.isShowKeybindText());
            textButton.setMessage(Text.literal("Text: " + targetStroke.isShowKeybindText()));
        }).dimensions(elementStartX, currentY, fieldWidth, fieldHeight).build();
        this.addDrawableChild(textButton);
        currentY += fieldHeight + 5;

        // Transparency Slider Fill
        int sliderStartX = panelX + (panelW - fieldWidth) / 2;
        transparencySlider = new RgbSlider(sliderStartX, currentY, fieldWidth, fieldHeight, "Alpha", ColorHelper.Argb.getAlpha(targetStroke.getColor()) / 255.0D) {
            @Override
            protected void applyValue() {
                int newAlpha = (int) (this.value * 255);
                // Update Unpressed Fill Color Alpha
                int unpressedR = ColorHelper.Argb.getRed(targetStroke.getColor());
                int unpressedG = ColorHelper.Argb.getGreen(targetStroke.getColor());
                int unpressedB = ColorHelper.Argb.getBlue(targetStroke.getColor());
                targetStroke.setColor(ColorHelper.Argb.getArgb(newAlpha, unpressedR, unpressedG, unpressedB));

                // Update Pressed Fill Color Alpha
                int pressedR = ColorHelper.Argb.getRed(targetStroke.getPressedColor());
                int pressedG = ColorHelper.Argb.getGreen(targetStroke.getPressedColor());
                int pressedB = ColorHelper.Argb.getBlue(targetStroke.getPressedColor());
                targetStroke.setPressedColor(ColorHelper.Argb.getArgb(newAlpha, pressedR, pressedG, pressedB));            }
        };
        this.addDrawableChild(transparencySlider);
        currentY += fieldHeight + 5;

        displayTextInput = new TextFieldWidget(textRenderer, elementStartX, currentY, fieldWidth, fieldHeight, Text.literal("Stroke Button"));
        displayTextInput.setText(targetStroke.getKeystrokeText());
        displayTextInput.setChangedListener(targetStroke::setKeystrokeText);
        this.addDrawableChild(displayTextInput);
        currentY += fieldHeight + 5;
    }

    private void removeGeneralSettings(){
        this.remove(applyGlobalButton);
        this.remove(resetButton);
        this.remove(inputTypeCycleButton);
        this.remove(outlinesButton);
        this.remove(roundnessSlider);
        this.remove(textButton);
        this.remove(transparencySlider);
        this.remove(displayTextInput);
    }


    /**
     * Updates the target stroke's unpressed fill color based on the current values of the RGB sliders.
     */
    private void refreshUnpressedColorFromSliders() {
        int r = (int) (unpressedRedSlider.getCurrentValue() * 255);
        int g = (int) (unpressedGreenSlider.getCurrentValue() * 255);
        int b = (int) (unpressedBlueSlider.getCurrentValue() * 255);
        int a = ColorHelper.Argb.getAlpha(targetStroke.getColor());
        int newColor = ColorHelper.Argb.getArgb(a, r, g, b);
        targetStroke.setColor(newColor);
    }

    /**
     * Updates the target stroke's pressed fill color based on the current values of the RGB sliders.
     */
    private void refreshPressedColorFromSliders() {
        int r = (int) (pressedRedSlider.getCurrentValue() * 255);
        int g = (int) (pressedGreenSlider.getCurrentValue() * 255);
        int b = (int) (pressedBlueSlider.getCurrentValue() * 255);
        int a = ColorHelper.Argb.getAlpha(targetStroke.getColor());
        int newColor = ColorHelper.Argb.getArgb(a, r, g, b);
        targetStroke.setPressedColor(newColor);
    }

    /**
     * Updates the target stroke's unpressed outline color based on the current values of the RGB sliders.
     */
    private void refreshOutlineColorFromSliders() {
        int r = (int) (outlineRedSlider.getCurrentValue() * 255);
        int g = (int) (outlineGreenSlider.getCurrentValue() * 255);
        int b = (int) (outlineBlueSlider.getCurrentValue() * 255);

        int currentAlpha = ColorHelper.Argb.getAlpha(targetStroke.getOutlineColor());
        int newColor = ColorHelper.Argb.getArgb(currentAlpha, r, g, b);
        targetStroke.setOutlineColor(newColor);
    }

    /**
     * Updates the target stroke's pressed outline color based on the current values of the RGB sliders.
     */
    private void refreshPressedOutlineColorFromSliders() {
        int r = (int) (pressedOutlineRedSlider.getCurrentValue() * 255);
        int g = (int) (pressedOutlineGreenSlider.getCurrentValue() * 255);
        int b = (int) (pressedOutlineBlueSlider.getCurrentValue() * 255);

        int currentAlpha = ColorHelper.Argb.getAlpha(targetStroke.getPressedOutlineColor());
        int newColor = ColorHelper.Argb.getArgb(currentAlpha, r, g, b);
        targetStroke.setPressedOutlineColor(newColor);
    }

    private void refreshTextColorFromSliders() {
        int r = (int) (textRedSlider.getCurrentValue() * 255);
        int g = (int) (textGreenSlider.getCurrentValue() * 255);
        int b = (int) (textBlueSlider.getCurrentValue() * 255);

        int currentAlpha = 255;
        int newColor = ColorHelper.Argb.getArgb(currentAlpha, r, g, b);
        targetStroke.setTextColor(newColor);
    }

    private void refreshPressedTextColorFromSliders() {
        int r = (int) (pressedTextRedSlider.getCurrentValue() * 255);
        int g = (int) (pressedTextGreenSlider.getCurrentValue() * 255);
        int b = (int) (pressedTextBlueSlider.getCurrentValue() * 255);

        int currentAlpha = 255;
        int newColor = ColorHelper.Argb.getArgb(currentAlpha, r, g, b);
        targetStroke.setPressedTextColor(newColor);
    }

    /**
     * Cycles through the available {@link Strokes.InputType} values for the target stroke.
     *
     * @param direction The direction to cycle (1 for forward, -1 for backward).
     */
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

    /**
     * Renders the StrokeEditScreen, including panel backgrounds, titles, and all contained widgets.
     *
     * @param context The drawing context.
     * @param mouseX The X-coordinate of the mouse.
     * @param mouseY The Y-coordinate of the mouse.
     * @param delta The partial tick delta.
     */
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        if(page1){
            renderPanelWithTitle(context, generalPanelX, generalPanelY, generalPanelWidth, generalPanelHeight, Text.literal("General Settings"));
        } else {
            renderPanelWithTitle(context, fillColorPanelX, fillColorPanelY, fillColorPanelWidth, fillColorPanelHeight, Text.literal("Fill Color"));
            renderPanelWithTitle(context, outlinePanelX, outlinePanelY, outlinePanelWidth, outlinePanelHeight, Text.literal("Outline Color"));
            renderPanelWithTitle(context, textPanelX, textPanelY, textPanelWidth, textPanelHeight, Text.literal("Text Color"));
        }

        // Render preview strokes
        int drawX = (outlinePanelX+outlinePanelWidth/2);
        int drawY = (int) (this.height * 0.05);
        int drawWidth = 20;
        int drawHeight = 20;

        FancyStrokesRenderer.drawRoundedRect(context, drawX-60, drawY, drawWidth, drawHeight, targetStroke.getRoundness(), targetStroke.getColor());
        FancyStrokesRenderer.drawRoundedOutline(context, drawX-60, drawY, drawWidth, drawHeight, targetStroke.getRoundness(), targetStroke.getOutlineColor());
        if (targetStroke.isShowKeybindText()) {
            String textToShow = targetStroke.getKeystrokeText();

            if(textToShow==null){
                textToShow = targetStroke.getKeyTextForInputType();
            } else if(textToShow.isBlank()){
                textToShow = targetStroke.getKeyTextForInputType();
            }
            if (!textToShow.isEmpty()) {
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                double offsetY = (double) (drawHeight - textRenderer.fontHeight) / 2;
                int centerX = (int) Math.round(drawX-60 + (float) drawWidth / 2);
                int centerY = drawY + (int) Math.round(offsetY);
                context.drawCenteredTextWithShadow(textRenderer, Text.literal(textToShow), centerX, centerY, targetStroke.getTextColor());
            }
        }

        FancyStrokesRenderer.drawRoundedRect(context, drawX+40, drawY, drawWidth, drawHeight, targetStroke.getRoundness(), targetStroke.getPressedColor());
        FancyStrokesRenderer.drawRoundedOutline(context, drawX+40, drawY, drawWidth, drawHeight, targetStroke.getRoundness(), targetStroke.getPressedOutlineColor());
        if (targetStroke.isShowKeybindText()) {
            String textToShow = targetStroke.getKeystrokeText();

            if(textToShow==null){
                textToShow = targetStroke.getKeyTextForInputType();
            } else if(textToShow.isBlank()){
                textToShow = targetStroke.getKeyTextForInputType();
            }
            if (!textToShow.isEmpty()) {
                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                double offsetY = (double) (drawHeight - textRenderer.fontHeight) / 2;
                int centerX = (int) Math.round(drawX+40 + (float) drawWidth / 2);
                int centerY = drawY + (int) Math.round(offsetY);
                context.drawCenteredTextWithShadow(textRenderer, Text.literal(textToShow), centerX, centerY, targetStroke.getPressedTextColor());
            }
        }

        super.render(context, mouseX, mouseY, delta);

        context.drawText(this.textRenderer, this.title, this.width / 2 - this.textRenderer.getWidth(this.title) / 2, 20, 0xFFFFFFFF, true);
    }

    /**
     * Renders a panel background with a title above it.
     *
     * @param context The drawing context.
     * @param x The X-coordinate of the panel.
     * @param y The Y-coordinate of the panel.
     * @param width The width of the panel.
     * @param height The height of the panel.
     * @param title The title text for the panel.
     */
    private void renderPanelWithTitle(DrawContext context, int x, int y, int width, int height, Text title) {
        int titleY = y - 15; // 15 Pixel über der Box
        context.drawText(this.textRenderer, title, x + width / 2 - this.textRenderer.getWidth(title) / 2, titleY, 0xFFFFFFFF, true);

        context.fill(x, y, x + width, y + height, 0xA0000000);
        context.drawBorder(x, y, width, height, 0xFFFFFFFF);
    }

    /**
     * Closes the current screen. Before closing, it deselects the target stroke,
     * saves the updated stroke configurations, and returns to the parent screen.
     */
    @Override
    public void close() {
        this.targetStroke.setSelected(false);
        YetAnotherKeystrokesModClient.saveProfilesToConfig();
        MinecraftClient.getInstance().setScreen(this.parentScreen);
    }

    /**
     * Handles mouse click events on the screen.
     * This specifically handles left and right-clicks on the {@code inputTypeCycleButton}.
     *
     * @param mouseX The X-coordinate of the mouse click.
     * @param mouseY The Y-coordinate of the mouse click.
     * @param button The mouse button that was clicked (0 for left, 1 for right).
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle clicks on inputTypeCycleButton
        if (inputTypeCycleButton.isMouseOver(mouseX, mouseY)) {
            if (button == 0) { // Left click
                cycleInputType(1); // Cycle forward
                return true;
            } else if (button == 1) { // Right click
                cycleInputType(-1); // Cycle backward
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