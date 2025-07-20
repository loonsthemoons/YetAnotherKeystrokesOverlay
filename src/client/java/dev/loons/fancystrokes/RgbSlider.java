package dev.loons.fancystrokes;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public abstract class RgbSlider extends SliderWidget { // Make it abstract as applyValue is abstract
    private final String componentName; // "R", "G", or "B"

    public RgbSlider(int x, int y, int width, int height, String componentName, double progress) {
        // progress is the initial value between 0.0 and 1.0
        super(x, y, width, height, Text.literal(componentName), progress);
        this.componentName = componentName;
        // Call updateMessage initially to set the correct text
        this.updateMessage();
    }

    // This method is called by the SliderWidget internally when the value changes
    @Override
    protected void updateMessage() {
        // 'this.value' is accessible here because we are inside the SliderWidget's subclass
        this.setMessage(Text.literal(componentName + ": " + (int) (this.value * 255)));
    }

    public double getCurrentValue() {
        return this.value;
    }
}