package dev.loons.fancystrokes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
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
    private int fieldWidth;
    private int fieldHeight;
    private int labelOffset;
    private int currentElementY;

    private TextFieldWidget rgbRedField;
    private TextFieldWidget rgbGreenField;
    private TextFieldWidget rgbBlueField;

    private ButtonWidget inputTypeToggleButton;
    private List<ButtonWidget> inputTypeOptionButtons = new ArrayList<>();
    private boolean showInputTypeDropdown = false;

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

        this.fieldWidth = 50;
        this.fieldHeight = 20;
        this.labelOffset = -40;
        int rgbFieldsStartX = this.panelStartX + (this.panelWidth - (fieldWidth * 3 + 20)) / 2;

        rgbRedField = new TextFieldWidget(this.textRenderer, rgbFieldsStartX, currentElementY, fieldWidth, fieldHeight, Text.empty());
        rgbRedField.setText(String.valueOf(ColorHelper.Argb.getRed(targetStroke.getColor())));
        rgbRedField.setChangedListener(text -> updateStrokeColor());
        this.addDrawableChild(rgbRedField);
        rgbFieldsStartX += fieldWidth + 10;

        rgbGreenField = new TextFieldWidget(this.textRenderer, rgbFieldsStartX, currentElementY, fieldWidth, fieldHeight, Text.empty());
        rgbGreenField.setText(String.valueOf(ColorHelper.Argb.getGreen(targetStroke.getColor())));
        rgbGreenField.setChangedListener(text -> updateStrokeColor());
        this.addDrawableChild(rgbGreenField);
        rgbFieldsStartX += fieldWidth + 10;

        rgbBlueField = new TextFieldWidget(this.textRenderer, rgbFieldsStartX, currentElementY, fieldWidth, fieldHeight, Text.empty());
        rgbBlueField.setText(String.valueOf(ColorHelper.Argb.getBlue(targetStroke.getColor())));
        rgbBlueField.setChangedListener(text -> updateStrokeColor());
        this.addDrawableChild(rgbBlueField);

        currentElementY += fieldHeight + 15;

        this.inputTypeToggleButton = ButtonWidget.builder(Text.literal("Input Type: " + targetStroke.getInputType().name()), (button) -> {
            this.showInputTypeDropdown = !this.showInputTypeDropdown; // Umschalten der Sichtbarkeit
            updateInputTypeButtonsVisibility(); // Aktualisiere die Sichtbarkeit der Buttons
        }).dimensions(this.panelStartX + (this.panelWidth - 150) / 2, currentElementY, 150, 20).build();
        this.addDrawableChild(inputTypeToggleButton);
        currentElementY += 25;

        // --- Buttons für InputType-Auswahl (das "Dropdown") ---
        for (Strokes.InputType type : Strokes.InputType.values()) {
            ButtonWidget typeButton = ButtonWidget.builder(Text.of(type.name()), (button) -> {
                targetStroke.setInputType(type); // Setze den InputType
                this.showInputTypeDropdown = false; // Dropdown schließen
                updateInputTypeButtonsVisibility(); // Sichtbarkeit aktualisieren
                // Aktualisiere den Text des Toggle-Buttons
                inputTypeToggleButton.setMessage(Text.literal("Input Type: " + type.name()));
            }).dimensions(this.panelStartX + (this.panelWidth - 150) / 2, currentElementY, 150, 20).build();
            typeButton.visible = false; // Standardmäßig unsichtbar
            this.addDrawableChild(typeButton);
            inputTypeOptionButtons.add(typeButton); // Füge sie zu unserer Liste hinzu
            currentElementY += 25;
        }
        updateInputTypeButtonsVisibility(); // Initial die Sichtbarkeit setzen

        this.addDrawableChild(ButtonWidget.builder(Text.of("Visible"), (button) -> {
                //
        }).dimensions(this.panelStartX + (this.panelWidth - 150) / 2, 250, 150, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Done"), (button) -> {
            // Optional: Hier könnten Änderungen am Stroke dauerhaft gespeichert werden,
            // falls sie nicht schon durch die Listener angewendet wurden.
            this.close(); // Schließt diesen Screen und kehrt zum parentScreen zurück
        }).dimensions(this.width / 2 - 75, this.height - 30, 150, 20).build());
    }

    // Hilfsmethode, um die Sichtbarkeit der InputType-Buttons zu aktualisieren
    private void updateInputTypeButtonsVisibility() {
        for (ButtonWidget button : inputTypeOptionButtons) {
            button.visible = this.showInputTypeDropdown;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context); // Hintergrund des Screens zeichnen

        // Zeichne den Panel-Hintergrund für das Bearbeitungsmenü
        context.fill(panelStartX, panelStartY, panelStartX + panelWidth, panelStartY + panelHeight, 0xA0000000); // Dunkler Hintergrund
        context.drawBorder(panelStartX, panelStartY, panelWidth, panelHeight, 0xFFFFFFFF); // Weißer Rand

        super.render(context, mouseX, mouseY, delta); // Rendert alle Buttons und Textfelder, die geaddet wurden


        int rgbLabelsStartX = this.panelStartX + (this.panelWidth - (fieldWidth * 3 + 20)) / 2;
        int rgbLabelsY = this.panelStartY + 20 + 5; // Y-Position der Textfelder + Offset für Label

        context.drawText(this.textRenderer, Text.literal("R:").asOrderedText(), rgbLabelsStartX + labelOffset, rgbLabelsY, 0xFFFFFFFF, true);
        rgbLabelsStartX += fieldWidth + 10;
        context.drawText(this.textRenderer, Text.literal("G:").asOrderedText(), rgbLabelsStartX + labelOffset, rgbLabelsY, 0xFFFFFFFF, true);
        rgbLabelsStartX += fieldWidth + 10;
        context.drawText(this.textRenderer, Text.literal("B:").asOrderedText(), rgbLabelsStartX + labelOffset, rgbLabelsY, 0xFFFFFFFF, true);

        // Titel des Screens zeichnen
        context.drawText(this.textRenderer, this.title, this.width / 2 - this.textRenderer.getWidth(this.title) / 2, 20, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, "Editing: " + targetStroke.getMessage().getString(), this.width / 2 - this.textRenderer.getWidth("Editing: " + targetStroke.getMessage().getString()) / 2, 40, 0xFFFFFF00, true);
        targetStroke.render(context, mouseX, mouseY, delta);
    }

    // Hilfsmethode zum Aktualisieren der Farbe des Strokes aus den Textfeldern
    private void updateStrokeColor() {
        try {
            int r = Integer.parseInt(rgbRedField.getText());
            int g = Integer.parseInt(rgbGreenField.getText());
            int b = Integer.parseInt(rgbBlueField.getText());

            // Werte auf 0-255 begrenzen
            r = Math.max(0, Math.min(255, r));
            g = Math.max(0, Math.min(255, g));
            b = Math.max(0, Math.min(255, b));

            // Alphawert des Strokes beibehalten, nur RGB ändern
            int currentColor = targetStroke.getColor();
            int newColor = ColorHelper.Argb.getArgb(ColorHelper.Argb.getAlpha(currentColor), r, g, b);
            targetStroke.setColor(newColor);
        } catch (NumberFormatException e) {
            // Ignorieren oder Fehlermeldung anzeigen, wenn keine gültige Zahl eingegeben wurde
            // console.warn("Invalid color input: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        // Hier könntest du speichern, falls die Änderungen nicht sofort angewendet werden
        // saveStrokeSettings(targetStroke);

        // Zum vorherigen Screen zurückkehren
        MinecraftClient.getInstance().setScreen(this.parentScreen);
    }

    // Zusätzliche Event-Methoden, um sicherzustellen, dass die Widgets im Screen Events erhalten
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Wenn das Dropdown offen ist und ein Klick außerhalb der Buttons erfolgt, schließe das Dropdown
        if (showInputTypeDropdown) {
            boolean clickedOnDropdownButton = false;
            for (ButtonWidget buttonWidget : inputTypeOptionButtons) {
                if (buttonWidget.isMouseOver(mouseX, mouseY)) {
                    clickedOnDropdownButton = true;
                    break;
                }
            }
            if (!clickedOnDropdownButton && !inputTypeToggleButton.isMouseOver(mouseX, mouseY)) {
                showInputTypeDropdown = false;
                updateInputTypeButtonsVisibility();
                // Optional: return true, wenn der Klick zum Schließen des Dropdowns dienen soll
            }
        }
        return super.mouseClicked(mouseX, mouseY, button); // Wichtig: Events an die Widgets weiterleiten
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
        // Wichtig: TextFieldWidgets brauchen keyPressed für Texteingabe
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        // Optional: ESC zum Schließen
        if (keyCode == 256) { // 256 ist der Keycode für ESCAPE
            this.close();
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        // Wichtig: TextFieldWidgets brauchen charTyped für Texteingabe
        return super.charTyped(chr, modifiers);
    }
}