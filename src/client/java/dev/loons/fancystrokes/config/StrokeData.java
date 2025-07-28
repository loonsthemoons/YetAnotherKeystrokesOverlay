package dev.loons.fancystrokes.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.loons.fancystrokes.Strokes;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;

/**
 * A data class representing the configurable properties of a single {@link Strokes} object.
 * This class is used for serializing and deserializing stroke data to and from a configuration file,
 * allowing the mod to save and load user-defined stroke appearances and positions.
 */
public class StrokeData {
    public Strokes.InputType inputType;
    public double posX, posY, posZ;
    public int color;
    public int pressedColor;
    public int outlineColor;
    public int pressedOutlineColor;
    public int textColor;
    public int pressedTextColor;
    public int width;
    public int height;
    public int roundness;
    public String keystrokeText;
    public String pressedKeystrokeText;

    /**
     * Default constructor for creating an empty StrokeData object.
     * Useful for deserialization purposes.
     */
    public StrokeData() {
        this.inputType = Strokes.InputType.NULL;
        this.posX = 0;
        this.posY = 0;
        this.posZ = 0;
        this.color = ColorHelper.Argb.getArgb(255, 60, 60, 60); // Dark Gray
        this.pressedColor = ColorHelper.Argb.getArgb(255, 150, 200, 255); // Light Blue
        this.outlineColor = ColorHelper.Argb.getArgb(255, 100, 100, 100); // Medium Gray
        this.pressedOutlineColor = ColorHelper.Argb.getArgb(255, 100, 150, 200); // Slightly darker blue
        this.textColor = ColorHelper.Argb.getArgb(255, 230, 230, 230); // Off-White
        this.pressedTextColor = ColorHelper.Argb.getArgb(255, 255, 255, 255); // Pure White
        this.width = 30;
        this.height = 30;
        this.roundness = 4;
        this.keystrokeText = "";
        this.pressedKeystrokeText = "";
    }

    /**
     * Constructs a StrokeData object from an existing {@link Strokes} instance.
     * This captures all relevant properties of the stroke for serialization.
     *
     * @param stroke The {@link Strokes} object from which to extract data.
     */
    public StrokeData(Strokes stroke) {
        this.inputType = stroke.getInputType();
        this.posX = stroke.getPosition().x;
        this.posY = stroke.getPosition().y;
        this.posZ = stroke.getPosition().z;
        this.color = stroke.getColor();
        this.pressedColor = stroke.getPressedColor();
        this.outlineColor = stroke.getOutlineColor();
        this.pressedOutlineColor = stroke.getPressedOutlineColor();
        this.textColor = stroke.getTextColor();
        this.pressedTextColor = stroke.getPressedTextColor();
        this.width = stroke.getWidth();
        this.height = stroke.getHeight();
        this.roundness = stroke.getRoundness();
        this.keystrokeText = stroke.getKeystrokeText();
        if(this.keystrokeText==null) this.keystrokeText=stroke.getKeyTextForInputType();
        this.pressedKeystrokeText = stroke.getPressedKeystrokeText();
        if(this.pressedKeystrokeText==null) this.pressedKeystrokeText=stroke.getKeyTextForInputType();
    }

    /**
     * Converts this StrokeData object back into a {@link Strokes} instance.
     * This method reconstructs a new {@link Strokes} object with the stored properties.
     *
     * @return A new {@link Strokes} object initialized with this data.
     */
    public Strokes toStroke() {
        Strokes newStroke = new Strokes(
                new Vec3d(posX, posY, posZ),
                color,
                pressedColor,
                outlineColor,
                pressedOutlineColor,
                textColor,
                pressedTextColor,
                width,
                height,
                inputType,
                roundness
        );
        newStroke.setKeystrokeText(this.keystrokeText);
        newStroke.setPressedKeystrokeText(this.pressedKeystrokeText);
        return newStroke;
    }

    /**
     * Serializes the StrokeData object into a {@link JsonElement} (specifically, a {@link JsonObject}).
     * This method is used to prepare the stroke's data for saving to a JSON configuration file.
     *
     * @return A {@link JsonElement} containing the serialized stroke data.
     */
    public JsonElement getSerializedForm() {
        JsonObject json = new JsonObject();
        json.addProperty("inputType", this.inputType.name());
        json.addProperty("posX", this.posX);
        json.addProperty("posY", this.posY);
        json.addProperty("posZ", this.posZ);
        json.addProperty("color", this.color);
        json.addProperty("pressedColor", this.pressedColor);
        json.addProperty("outlineColor", this.outlineColor);
        json.addProperty("pressedOutlineColor", this.pressedOutlineColor);
        json.addProperty("textColor", this.textColor);
        json.addProperty("pressedTextColor", this.pressedTextColor);
        json.addProperty("width", this.width);
        json.addProperty("height", this.height);
        json.addProperty("roundness", this.roundness);
        json.addProperty("keystrokeText", this.keystrokeText);
        json.addProperty("pressedKeystrokeText", this.pressedKeystrokeText);
        return json;
    }

    /**
     * Loads the properties of this StrokeData object from a given {@link JsonElement}.
     * This method is used to deserialize stroke data from a JSON configuration file.
     *
     * @param jsonElement The {@link JsonElement} (expected to be a {@link JsonObject})
     * containing the serialized stroke data.
     * @throws IllegalArgumentException if the {@code inputType} string does not match any
     * {@link Strokes.InputType} enum constant.
     * @throws ClassCastException if the JSON elements are not of the expected type (e.g., getting an int as a String).
     * @throws NullPointerException if a required JSON property is missing.
     */
    public void load(JsonElement jsonElement) {
        if(jsonElement.isJsonNull() || !jsonElement.isJsonObject()){
            System.err.println("Invalid JsonElement for StrokeData load. Initializing with default values.");
            return;
        }

        JsonObject json = jsonElement.getAsJsonObject();

        try {
            this.inputType = json.has("inputType") ? Strokes.InputType.valueOf(json.get("inputType").getAsString()) : Strokes.InputType.NULL;
        } catch (IllegalArgumentException e) {
            System.err.println("Unknown InputType in saved data: " + json.get("inputType").getAsString() + ". Setting to NULL. Error: " + e.getMessage());
            this.inputType = Strokes.InputType.NULL;
        }

        this.inputType = Strokes.InputType.valueOf(json.get("inputType").getAsString());
        this.posX = json.has("posX") ? json.get("posX").getAsDouble() : this.posX;
        this.posY = json.has("posY") ? json.get("posY").getAsDouble() : this.posY;
        this.posZ = json.has("posZ") ? json.get("posZ").getAsDouble() : this.posZ;
        this.color = json.has("color") ? json.get("color").getAsInt() : this.color;
        this.pressedColor = json.has("pressedColor") ? json.get("pressedColor").getAsInt() : this.pressedColor;
        this.outlineColor = json.has("outlineColor") ? json.get("outlineColor").getAsInt() : this.outlineColor;
        this.pressedOutlineColor = json.has("pressedOutlineColor") ? json.get("pressedOutlineColor").getAsInt() : this.pressedOutlineColor;
        this.textColor = json.has("textColor") ? json.get("textColor").getAsInt() : this.textColor;
        this.pressedTextColor = json.has("pressedTextColor") ? json.get("pressedTextColor").getAsInt() : this.pressedTextColor;
        this.width = json.has("width") ? json.get("width").getAsInt() : this.width;
        this.height = json.has("height") ? json.get("height").getAsInt() : this.height;
        this.roundness = json.has("roundness") ? json.get("roundness").getAsInt() : this.roundness;
        this.keystrokeText = json.has("keystrokeText") ? json.get("keystrokeText").getAsString() : this.keystrokeText;
        this.pressedKeystrokeText = json.has("pressedKeystrokeText") ? json.get("pressedKeystrokeText").getAsString() : this.pressedKeystrokeText;
    }
}