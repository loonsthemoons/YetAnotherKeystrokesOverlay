package dev.loons.fancystrokes.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.loons.fancystrokes.Strokes;
import net.minecraft.util.math.Vec3d;

public class StrokeData {
    public Strokes.InputType inputType;
    public double posX, posY, posZ;
    public int color;
    public int pressedColor;
    public int outlineColor;
    public int pressedOutlineColor;
    public int width;
    public int height;
    public int roundness;
    public boolean showKeybindText;
    public boolean outlineStatus;

    public StrokeData() {}
    public StrokeData(Strokes stroke) {
        this.inputType = stroke.getInputType();
        this.posX = stroke.getPosition().x;
        this.posY = stroke.getPosition().y;
        this.posZ = stroke.getPosition().z;
        this.color = stroke.getColor();
        this.pressedColor = stroke.getPressedColor();
        this.outlineColor = stroke.getOutlineColor();
        this.pressedOutlineColor = stroke.getPressedOutlineColor();
        this.width = stroke.getWidth();
        this.height = stroke.getHeight();
        this.roundness = stroke.getRoundness();
        this.showKeybindText = stroke.isShowKeybindText();
        this.outlineStatus = stroke.getOutlineStatus();
    }

    public Strokes toStroke() {
        Strokes newStroke = new Strokes(
                new Vec3d(posX, posY, posZ),
                color,
                pressedColor,
                outlineColor,
                pressedOutlineColor,
                width,
                height,
                inputType,
                roundness
        );
        newStroke.setShowKeybindText(this.showKeybindText);
        newStroke.setOutlineStatus(this.outlineStatus);
        return newStroke;
    }

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
        json.addProperty("width", this.width);
        json.addProperty("height", this.height);
        json.addProperty("roundness", this.roundness);
        json.addProperty("showKeybindText", this.showKeybindText);
        json.addProperty("outlineStatus", this.outlineStatus);
        return json;
    }

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
        this.width = json.get("width").getAsInt();
        this.height = json.get("height").getAsInt();
        this.roundness = json.get("roundness").getAsInt();
        this.showKeybindText = json.get("showKeybindText").getAsBoolean();
        this.outlineStatus = json.get("outlineStatus").getAsBoolean();
    }
}