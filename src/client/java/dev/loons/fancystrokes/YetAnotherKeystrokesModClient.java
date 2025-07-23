package dev.loons.fancystrokes;

import dev.loons.fancystrokes.config.ProfileData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.text.Text;
import java.util.ArrayList;

/**
 * The main client-side initializer for the FancyStrokes mod.
 * This class handles the initialization of configurations, stroke management,
 * and UI components upon the client's mod's startup.
 */
public class YetAnotherKeystrokesModClient implements ClientModInitializer {
	public static FancyStrokesConfig CONFIG;
	public static ArrayList<StrokesStructure> PROFILES = new ArrayList<>();
	public static StrokesStructure STROKES_STRUCTURE;
	//private String ACTIVE_PROFILE = "Default";

	/**
	 * Called when the client-side mod is initialized.
	 * This method loads existing strokes or initializes default ones,
	 * and sets up the mod's UI and controllers.
	 */
	@Override
	public void onInitializeClient() {
		STROKES_STRUCTURE = new StrokesStructure();
		CONFIG = FancyStrokesConfig.getInstance();
		SoundLibrary.initialize();

		if (CONFIG.getSavedProfiles().isEmpty()) {
			System.out.println("No saved profiles found, initializing default profile.");
			STROKES_STRUCTURE = new StrokesStructure(); // Create a new structure for the default profile
			STROKES_STRUCTURE.setProfileName("Default");
			STROKES_STRUCTURE.initializeDefaultStrokes();
			STROKES_STRUCTURE.setActive(); // Set default as active
			PROFILES.add(STROKES_STRUCTURE); // Add to the list of profiles
			saveProfilesToConfig(); // Save the newly created default profile
		} else {
			System.out.println("Loading profiles from config file.");
			boolean foundActive = false;
			for (ProfileData data : CONFIG.getSavedProfiles()) {
				StrokesStructure loadedStructure = data.toStrokesStructure();
				PROFILES.add(loadedStructure);
				if (loadedStructure.getActive()) {
					foundActive = true;
					STROKES_STRUCTURE = loadedStructure; // Set the globally accessible STROKES_STRUCTURE to the active one
				}
			}
			if (!foundActive && !PROFILES.isEmpty()) {
				PROFILES.get(0).setActive();
				STROKES_STRUCTURE = PROFILES.get(0); // Ensure STROKES_STRUCTURE points to an active one
				System.out.println("No active profile found in config, setting first profile '" + STROKES_STRUCTURE.getProfileName() + "' as active.");
			} else if (PROFILES.isEmpty()) {
				System.err.println("PROFILES list is empty after loading, creating a new default profile.");
				STROKES_STRUCTURE = new StrokesStructure();
				STROKES_STRUCTURE.setProfileName("Default");
				STROKES_STRUCTURE.initializeDefaultStrokes();
				STROKES_STRUCTURE.setActive();
				PROFILES.add(STROKES_STRUCTURE);
			}
		}

		StrokeOptions menuScreen = new StrokeOptions(Text.literal("FancyStrokes Options"), STROKES_STRUCTURE, PROFILES);
		StrokesView strokesView = new StrokesView(STROKES_STRUCTURE, PROFILES);
		StrokesController strokesController = new StrokesController(strokesView, STROKES_STRUCTURE, menuScreen, PROFILES);

		strokesView.renderOverlay();
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			System.out.println("Client stopping, saving profiles...");
			saveProfilesToConfig();
		});
	}

	/**
	 * Initializes or resets the keystroke structure to its default settings.
	 */
	public void initializeDefaultStrokes() {
		STROKES_STRUCTURE.clearAll();
		STROKES_STRUCTURE.initializeDefaultStrokes();
		saveProfilesToConfig();
	}

	/**
	 * Saves the current keystroke configuration to the mod's configuration file.
	 */
	public static void saveProfilesToConfig() {
		ArrayList<ProfileData> dataToSave = new ArrayList<>();
		for (StrokesStructure profile : PROFILES) {
			dataToSave.add(new ProfileData(profile));
		}
		CONFIG.setSavedProfiles(dataToSave);
		CONFIG.save();
		System.out.println("Profiles config saved.");
	}
}