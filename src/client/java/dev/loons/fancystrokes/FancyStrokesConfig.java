package dev.loons.fancystrokes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.loons.fancystrokes.config.StrokeData;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the configuration for the FancyStrokes mod
 */
public class FancyStrokesConfig {
    public boolean enableFancyStrokes = true;
    public int globalRoundness = 8;
    public List<StrokeData> savedStrokes = new ArrayList<>();

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

            if (configJson.has("enableFancyStrokes")) {
                this.enableFancyStrokes = configJson.get("enableFancyStrokes").getAsBoolean();
            }
            if (configJson.has("globalRoundness")) {
                this.globalRoundness = configJson.get("globalRoundness").getAsInt();
            }

            if (configJson.has("savedStrokes")) {
                JsonArray strokesArray = configJson.getAsJsonArray("savedStrokes");
                this.savedStrokes.clear();
                for (JsonElement element : strokesArray) {
                    StrokeData strokeData = new StrokeData();
                    strokeData.load(element);
                    this.savedStrokes.add(strokeData);
                }
            }

        } catch (IOException e) {
            System.err.println("Failed to load Yet-Another-Keystroke-Mod config: " + e.getMessage());
        }
    }

    public void save() {
        JsonObject configJson = new JsonObject();

        configJson.addProperty("enableFancyStrokes", this.enableFancyStrokes);
        configJson.addProperty("globalRoundness", this.globalRoundness);

        JsonArray strokesArray = new JsonArray();
        for (StrokeData strokeData : this.savedStrokes) {
            strokesArray.add(strokeData.getSerializedForm());
        }
        configJson.add("savedStrokes", strokesArray);

        try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
            GSON.toJson(configJson, writer);
        } catch (IOException e) {
            System.err.println("Failed to save Yet-Another-Keystroke-Mod config: " + e.getMessage());
        }
    }

    public boolean isEnabled() { return enableFancyStrokes; }
    public int getGlobalRoundness() { return globalRoundness; }
    public List<StrokeData> getSavedStrokes() { return savedStrokes; }

    public void setSavedStrokes(List<StrokeData> newStrokes) {
        this.savedStrokes = newStrokes;
    }
}