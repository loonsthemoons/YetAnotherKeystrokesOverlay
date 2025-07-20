package dev.loons.fancystrokes;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

/**
 * An abstract slider widget specifically designed for adjusting RGB color components.
 * This slider displays the component name (R, G, or B) and its current value (0-255).
 * Subclasses must implement the {@code applyValue()} method to define how the color
 * change affects the target object.
 */
public abstract class RgbSlider extends SliderWidget {
    private final String componentName; // "R", "G", or "B"

    /**
     * Constructs a new RgbSlider.
     *
     * @param x The X-coordinate of the slider.
     * @param y The Y-coordinate of the slider.
     * @param width The width of the slider.
     * @param height The height of the slider.
     * @param componentName The name of the RGB component this slider controls (e.g., "R", "G", "B").
     * @param progress The initial value of the slider, normalized between 0.0 and 1.0.
     */
    public RgbSlider(int x, int y, int width, int height, String componentName, double progress) {
        // progress is the initial value between 0.0 and 1.0
        super(x, y, width, height, Text.literal(componentName), progress);
        this.componentName = componentName;
        // Call updateMessage initially to set the correct text
        this.updateMessage();
    }

    /**
     * Updates the displayed message of the slider to show the component name
     * and its current integer value (0-255). This method is called internally
     * by the {@link SliderWidget} when the slider's value changes.
     */
    @Override
    protected void updateMessage() {
        // 'this.value' is accessible here because we are inside the SliderWidget's subclass
        this.setMessage(Text.literal(componentName + ": " + (int) (this.value * 255)));
    }

    public double getCurrentValue() {
        return this.value;
    }
}