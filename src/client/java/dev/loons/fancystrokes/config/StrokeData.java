package dev.loons.fancystrokes.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.loons.fancystrokes.Strokes;
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
    public boolean showKeybindText;
    public boolean outlineStatus;

    /**
     * Default constructor for creating an empty StrokeData object.
     * Useful for deserialization purposes.
     */
    public StrokeData() {}

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
        this.showKeybindText = stroke.isShowKeybindText();
        this.outlineStatus = stroke.getOutlineStatus();
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
        newStroke.setShowKeybindText(this.showKeybindText);
        newStroke.setOutlineStatus(this.outlineStatus);
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
        json.addProperty("showKeybindText", this.showKeybindText);
        json.addProperty("outlineStatus", this.outlineStatus);
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
        JsonObject json = jsonElement.getAsJsonObject();
        this.inputType = Strokes.InputType.valueOf(json.get("inputType").getAsString());
        this.posX = json.get("posX").getAsDouble();
        this.posY = json.get("posY").getAsDouble();
        this.posZ = json.get("posZ").getAsDouble();
        this.color = json.get("color").getAsInt();
        this.pressedColor = json.get("pressedColor").getAsInt();
        this.outlineColor = json.get("outlineColor").getAsInt();
        this.pressedOutlineColor = json.get("pressedOutlineColor").getAsInt();
        this.textColor = json.get("textColor").getAsInt();
        this.pressedTextColor = json.get("pressedTextColor").getAsInt();
        this.width = json.get("width").getAsInt();
        this.height = json.get("height").getAsInt();
        this.roundness = json.get("roundness").getAsInt();
        this.showKeybindText = json.get("showKeybindText").getAsBoolean();
        this.outlineStatus = json.get("outlineStatus").getAsBoolean();
    }
}