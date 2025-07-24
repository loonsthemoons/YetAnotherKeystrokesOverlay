package dev.loons.fancystrokes.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.loons.fancystrokes.StrokesStructure;
import dev.loons.fancystrokes.Strokes; // Import Strokes if StrokesStructure needs to convert it

import java.util.ArrayList;
import java.util.List;

/**
 * A data class representing the configurable properties of a single StrokesStructure (profile).
 * This class is used for serializing and deserializing profile data, including all its strokes,
 * to and from a configuration file.
 */
public class ProfileData {
    public String profileName;
    public boolean isActiveProfile;
    public List<StrokeData> strokes;
    public boolean keypressSound;
    public String soundProfile;
    public float volume;

    /**
     * Default constructor for creating an empty ProfileData object.
     * Useful for deserialization purposes.
     */
    public ProfileData() {
        this.strokes = new ArrayList<>();
    }

    /**
     * Constructs a ProfileData object from an existing {@link StrokesStructure} instance.
     * This captures all relevant properties of the profile for serialization.
     *
     * @param structure The {@link StrokesStructure} object from which to extract data.
     */
    public ProfileData(StrokesStructure structure) {
        this.profileName = structure.getProfileName();
        this.isActiveProfile = structure.getActive();
        this.keypressSound = structure.getKeypressSound();
        this.soundProfile = structure.getSoundProfile();
        this.volume = structure.getVolume();
        this.strokes = new ArrayList<>();
        for (Strokes stroke : structure.getStrokes()) {
            this.strokes.add(new StrokeData(stroke));
        }
    }

    /**
     * Converts this ProfileData object back into a {@link StrokesStructure} instance.
     * This method reconstructs a new {@link StrokesStructure} object with the stored properties.
     *
     * @return A new {@link StrokesStructure} object initialized with this data.
     */
    public StrokesStructure toStrokesStructure() {
        StrokesStructure newStructure = new StrokesStructure();
        newStructure.setProfileName(this.profileName);
        if (this.isActiveProfile) { // Only set active if it was active in the config
            newStructure.setActive();
        } else {
            newStructure.setInactive(); // Ensure it's explicitly inactive if not marked active
        }
        newStructure.setSoundProfile(this.soundProfile);
        newStructure.setKeypressSound(this.keypressSound);
        newStructure.setVolume(this.volume);
        for (StrokeData strokeData : this.strokes) {
            newStructure.addStroke(strokeData.toStroke());
        }
        return newStructure;
    }

    /**
     * Serializes the ProfileData object into a {@link JsonElement} (specifically, a {@link JsonObject}).
     * This method is used to prepare the profile's data for saving to a JSON configuration file.
     *
     * @return A {@link JsonElement} containing the serialized profile data.
     */
    public JsonElement getSerializedForm() {
        JsonObject json = new JsonObject();
        json.addProperty("profileName", this.profileName);
        json.addProperty("isActiveProfile", this.isActiveProfile);
        json.addProperty("keypressSound", this.keypressSound);
        json.addProperty("soundProfile", this.soundProfile);
        json.addProperty("volume", this.volume);

        JsonArray strokesArray = new JsonArray();
        for (StrokeData strokeData : this.strokes) {
            strokesArray.add(strokeData.getSerializedForm());
        }
        json.add("strokes", strokesArray); // Note: Renamed from "savedStrokes" to "strokes" for clarity within a profile
        return json;
    }

    /**
     * Loads the properties of this ProfileData object from a given {@link JsonElement}.
     * This method is used to deserialize profile data from a JSON configuration file.
     *
     * @param jsonElement The {@link JsonElement} (expected to be a {@link JsonObject})
     * containing the serialized profile data.
     */
    public void load(JsonElement jsonElement) {
        if (jsonElement == null || !jsonElement.isJsonObject()) {
            System.err.println("Invalid JsonElement for ProfileData load: " + (jsonElement != null ? jsonElement.toString() : "null"));
            this.profileName = "Corrupted Profile";
            this.isActiveProfile = false;
            this.strokes = new ArrayList<>();
            return;
        }

        JsonObject json = jsonElement.getAsJsonObject();

        this.profileName = json.has("profileName") ? json.get("profileName").getAsString() : "Unnamed Profile";
        this.isActiveProfile = json.has("isActiveProfile") && json.get("isActiveProfile").getAsBoolean();
        this.keypressSound = json.has("keypressSound") && json.get("keypressSound").getAsBoolean();
        this.soundProfile = json.has("soundProfile") ? json.get("soundProfile").getAsString() : "linear";
        this.volume = json.has("volume") ? json.get("volume").getAsFloat() : 1.0f;

        JsonElement strokesElement = json.get("strokes");
        if (strokesElement != null && strokesElement.isJsonArray()) {
            JsonArray strokesArray = strokesElement.getAsJsonArray();
            this.strokes.clear();
            for (JsonElement element : strokesArray) {
                if (element != null && element.isJsonObject()) {
                    StrokeData strokeData = new StrokeData();
                    try {
                        strokeData.load(element);
                        this.strokes.add(strokeData);
                    } catch (Exception e) {
                        System.err.println("Error loading individual stroke data for profile '" + profileName + "': " + e.getMessage() + ". Skipping stroke: " + element.toString());
                    }
                } else {
                    System.err.println("Skipping malformed stroke element in profile '" + profileName + "': " + (element != null ? element.toString() : "null"));
                }
            }
        } else {
            this.strokes = new ArrayList<>();
            System.err.println("Profile '" + profileName + "' has no 'strokes' array or it's malformed. Initializing with empty strokes.");
        }
    }
}
