package dev.loons.fancystrokes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.loons.fancystrokes.config.ProfileData;
import dev.loons.fancystrokes.config.StrokeData;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the configuration for the YAKO mod
 */
public class FancyStrokesConfig {
    public boolean enableFancyStrokes = true;
    public int globalRoundness = 8;
    public List<ProfileData> savedProfiles = new ArrayList<>();

    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("Yet-Another-Keystrokes-Mod.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static FancyStrokesConfig instance;

    /**
     * Returns the singleton instance of FancyStrokesConfig.
     * If no instance exists, a new one is created and loaded.
     *
     * @return The singleton instance of FancyStrokesConfig.
     */
    public static FancyStrokesConfig getInstance() {
        if (instance == null) {
            instance = new FancyStrokesConfig();
            instance.load();
        }
        return instance;
    }

    /**
     * Private constructor to enforce the singleton pattern.
     */
    private FancyStrokesConfig() {}

    /**
     * Loads the configuration from the JSON file. If the file does not exist,
     * default values are used.
     */
    public void load() {
        if (!CONFIG_PATH.toFile().exists()) {
            System.out.println("No config file found, using defaults for Yet-Another-Keystroke-Mod.");
            return;
        }

        try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
            JsonObject configJson = GSON.fromJson(reader, JsonObject.class);

            if (configJson == null) {
                System.err.println("Config file is empty or malformed JSON. Using defaults.");
                return;
            }

            if (configJson.has("enableFancyStrokes")) {
                this.enableFancyStrokes = configJson.get("enableFancyStrokes").getAsBoolean();
            }
            if (configJson.has("globalRoundness")) {
                this.globalRoundness = configJson.get("globalRoundness").getAsInt();
            }

            JsonElement savedProfilesElement = configJson.get("savedProfiles");
            if (savedProfilesElement != null && savedProfilesElement.isJsonArray()) {
                JsonArray profilesArray = savedProfilesElement.getAsJsonArray();
                this.savedProfiles.clear();
                for (JsonElement element : profilesArray) {
                    if (element != null && element.isJsonObject()) {
                        ProfileData profileData = new ProfileData();
                        try {
                            profileData.load(element);
                            this.savedProfiles.add(profileData);
                        } catch (Exception e) {
                            System.err.println("Error loading individual profile data: " + e.getMessage() + ". Skipping profile: " + element.toString());
                        }
                    } else {
                        System.err.println("Skipping malformed profile element: " + (element != null ? element.toString() : "null"));
                    }
                }
            } else if (configJson.has("savedStrokes")) {
                System.out.println("Found legacy 'savedStrokes' format. Converting to 'Default' profile.");
                JsonElement savedStrokesElement = configJson.get("savedStrokes");
                if (savedStrokesElement != null && savedStrokesElement.isJsonArray()) {
                    JsonArray strokesArray = savedStrokesElement.getAsJsonArray();
                    ProfileData defaultProfileData = new ProfileData();
                    defaultProfileData.profileName = "Default";
                    defaultProfileData.isActiveProfile = true; // Assume the legacy save was the active one
                    for (JsonElement element : strokesArray) {
                        if (element != null && element.isJsonObject()) {
                            StrokeData strokeData = new StrokeData();
                            try {
                                strokeData.load(element);
                                defaultProfileData.strokes.add(strokeData);
                            } catch (Exception e) {
                                System.err.println("Error loading legacy stroke data: " + e.getMessage() + ". Skipping stroke: " + element.toString());
                            }
                        }
                    }
                    this.savedProfiles.add(defaultProfileData);
                } else {
                    System.err.println("Legacy 'savedStrokes' found but not a valid array. Skipping conversion.");
                }
            } else {
                this.savedProfiles = new ArrayList<>();
                System.out.println("No saved profiles or legacy strokes found. Initializing with empty profile list.");
            }

        } catch (IOException e) {
            System.err.println("Failed to load Yet-Another-Keystroke-Mod config: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("General error during config loading: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void save() {
        JsonObject configJson = new JsonObject();

        configJson.addProperty("enableFancyStrokes", this.enableFancyStrokes);
        configJson.addProperty("globalRoundness", this.globalRoundness);

        JsonArray profilesArray = new JsonArray();
        for (ProfileData profileData : this.savedProfiles) {
            profilesArray.add(profileData.getSerializedForm());
        }
        configJson.add("savedProfiles", profilesArray);

        try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(configJson, writer);
        } catch (IOException e) {
            System.err.println("Failed to save Yet-Another-Keystroke-Mod config: " + e.getMessage());
        }
    }

    public boolean isEnabled() { return enableFancyStrokes; }
    public int getGlobalRoundness() { return globalRoundness; }
    public List<ProfileData> getSavedProfiles() { return savedProfiles; }

    public void setSavedProfiles(ArrayList<ProfileData> newProfiles) {
        this.savedProfiles = newProfiles;
    }
}