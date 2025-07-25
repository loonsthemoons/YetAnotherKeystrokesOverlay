package dev.loons.fancystrokes;

import dev.loons.fancystrokes.config.ProfileData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;

/**
 * The main client-side initializer for YAKO.
 * This class handles the initialization of configurations, stroke management,
 * and UI components upon the client's mod's startup.
 */
public class YetAnotherKeystrokesModClient implements ClientModInitializer {
	public static FancyStrokesConfig CONFIG;
	public static ArrayList<StrokesStructure> PROFILES = new ArrayList<>();
	public static StrokesStructure STROKES_STRUCTURE;

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
			initializeDefaultStrokes();
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
				loadedStructure.soundProfile(data.soundProfile);
				loadedStructure.keypressSound(data.keypressSound);
				loadedStructure.setVolume(data.volume);
				loadedStructure.setDidPopupShow(data.didPopupShow);
				loadedStructure.letteringOption(data.letteringOption);
			}
			if (!foundActive && !PROFILES.isEmpty()) {
				PROFILES.get(0).setActive();
				STROKES_STRUCTURE = PROFILES.get(0); // Ensure STROKES_STRUCTURE points to an active one
				System.out.println("No active profile found in config, setting first profile '" + STROKES_STRUCTURE.getProfileName() + "' as active.");
			} else if (PROFILES.isEmpty()) {
				System.err.println("PROFILES list is empty after loading, creating a new default profile.");
				initializeDefaultStrokes();
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
		StrokesStructure default1 = new StrokesStructure();
		StrokesStructure default2 = new StrokesStructure();
		StrokesStructure default3 = new StrokesStructure();
		default1.setProfileName("Default1");
		default2.setProfileName("Default2");
		default3.setProfileName("Default3");
		default1.initializeDefaultStrokes(ColorHelper.Argb.getArgb(255, 60, 60, 60), ColorHelper.Argb.getArgb(255, 230, 230, 230),ColorHelper.Argb.getArgb(255, 255, 255, 255),ColorHelper.Argb.getArgb(255, 150, 200, 255),ColorHelper.Argb.getArgb(255, 255, 255, 255),ColorHelper.Argb.getArgb(255, 150, 200, 255),4);
		default1.setOutlineStatus(true);
		default2.initializeDefaultStrokes(ColorHelper.Argb.getArgb(255, 255, 200, 255),ColorHelper.Argb.getArgb(255, 220, 255, 220),ColorHelper.Argb.getArgb(255, 220, 240, 255),ColorHelper.Argb.getArgb(255, 150, 150, 150),ColorHelper.Argb.getArgb(255, 220, 255, 220),ColorHelper.Argb.getArgb(255, 255, 200, 255),8);
		default2.letteringOption(true);
		default2.keypressSound(true);
		default2.setOutlineStatus(true);
		default3.initializeDefaultStrokes(ColorHelper.Argb.getArgb(155, 0, 0, 0),ColorHelper.Argb.getArgb(155, 150, 50, 50),ColorHelper.Argb.getArgb(255, 100, 50, 50),ColorHelper.Argb.getArgb(255, 0, 0, 0),ColorHelper.Argb.getArgb(255, 255, 90, 90),ColorHelper.Argb.getArgb(255, 255, 255, 255),0);
		default3.setOutlineStatus(true);
		default3.soundProfile("tactile");
		default3.keypressSound(true);
		default1.setActive();
		PROFILES.add(default1);
		PROFILES.add(default2);
		PROFILES.add(default3);
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