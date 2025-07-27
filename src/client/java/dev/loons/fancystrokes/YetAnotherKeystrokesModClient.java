package dev.loons.fancystrokes;

import dev.loons.fancystrokes.config.ProfileData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.lwjgl.glfw.GLFW;

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
	private static StrokeOptions menuScreen;
	private static StrokesView strokesView;
	private static StrokesController strokesController;
	public static KeyBinding OPEN_SETTINGS_KEYBINDING;
	public static KeyBinding DISABLE_KEYSTROKES_KEYBINDING;

	/**
	 * Called when the client-side mod is initialized.
	 * This method loads existing strokes or initializes default ones,
	 * and sets up the mod's UI and controllers.
	 */
	@Override
	public void onInitializeClient() {
		CONFIG = FancyStrokesConfig.getInstance();
		OPEN_SETTINGS_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Open Keystrokes Settings", // The translation key of the keybinding's name
				InputUtil.Type.KEYSYM,                  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_R,                        // The keycode of the key
				"Yet Another Keystrokes Overlay"            // The translation key of the keybinding's category.
		));
		DISABLE_KEYSTROKES_KEYBINDING = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Disable Keystrokes",       // The translation key of the keybinding's name
				InputUtil.Type.KEYSYM,                  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_K,                        // The keycode of the key
				"Yet Another Keystrokes Overlay"            // The translation key of the keybinding's category.
		));
		SoundLibrary.initialize();
		initializeUI();
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			System.out.println("Client stopping, saving profiles...");
			saveProfilesToConfig();
		});
	}

	public static void initializeUI(){
		PROFILES.clear();
		STROKES_STRUCTURE = new StrokesStructure();

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
				STROKES_STRUCTURE = PROFILES.get(0);
				System.out.println("No active profile found in config, setting first profile '" + STROKES_STRUCTURE.getProfileName() + "' as active.");
			} else if (PROFILES.isEmpty()) {
				System.err.println("PROFILES list is empty after loading, creating a new default profile.");
				initializeDefaultStrokes();
			}
		}

		menuScreen = new StrokeOptions(Text.literal("YAKO - Yet Another Keystrokes Overlay"), STROKES_STRUCTURE, PROFILES);
		strokesView = new StrokesView(STROKES_STRUCTURE, PROFILES);
		strokesController = new StrokesController(strokesView, STROKES_STRUCTURE, menuScreen, PROFILES, OPEN_SETTINGS_KEYBINDING, DISABLE_KEYSTROKES_KEYBINDING);

		strokesView.renderOverlay();
	}

	public static void updateUI() {
		if (strokesController != null) {
			strokesController.setStrokesStructure(STROKES_STRUCTURE);
			strokesController.setProfiles(PROFILES);
			strokesController.setMenuScreen(menuScreen);
			strokesController.setKeyBinding(OPEN_SETTINGS_KEYBINDING);
			strokesController.setDisableKeystrokes(DISABLE_KEYSTROKES_KEYBINDING);
		}
		if (strokesView != null) {
			strokesView.setStrokesStructure(STROKES_STRUCTURE);
			strokesView.setProfiles(PROFILES);
		}
		if (menuScreen != null) {
			menuScreen = new StrokeOptions(Text.literal("YAKO - Yet Another Keystrokes Overlay"), STROKES_STRUCTURE, PROFILES);
			menuScreen.setKeyBinding(OPEN_SETTINGS_KEYBINDING);
		}
	}

	/**
	 * Initializes or resets the keystroke structure to its default settings.
	 */
	public static void initializeDefaultStrokes() {
		StrokesStructure default1 = new StrokesStructure();
		StrokesStructure default2 = new StrokesStructure();
		StrokesStructure default3 = new StrokesStructure();
		default1.setProfileName("Default1");
		default2.setProfileName("Default2");
		default3.setProfileName("Default3");
		default1.initializeDefaultStrokes(ColorHelper.Argb.getArgb(255, 60, 60, 60), ColorHelper.Argb.getArgb(255, 230, 230, 230),ColorHelper.Argb.getArgb(255, 255, 255, 255),ColorHelper.Argb.getArgb(255, 150, 200, 255),ColorHelper.Argb.getArgb(255, 255, 255, 255),ColorHelper.Argb.getArgb(255, 150, 200, 255),4);
		default2.initializeDefaultStrokes(ColorHelper.Argb.getArgb(255, 255, 200, 255),ColorHelper.Argb.getArgb(255, 220, 255, 220),ColorHelper.Argb.getArgb(255, 220, 240, 255),ColorHelper.Argb.getArgb(255, 150, 150, 150),ColorHelper.Argb.getArgb(255, 220, 255, 220),ColorHelper.Argb.getArgb(255, 255, 200, 255),8);
		default2.letteringOption(true);
		default2.keypressSound(true);
		default3.initializeDefaultStrokes(ColorHelper.Argb.getArgb(155, 0, 0, 0),ColorHelper.Argb.getArgb(155, 150, 50, 50),ColorHelper.Argb.getArgb(255, 100, 50, 50),ColorHelper.Argb.getArgb(255, 0, 0, 0),ColorHelper.Argb.getArgb(255, 255, 90, 90),ColorHelper.Argb.getArgb(255, 255, 255, 255),0);
		default3.soundProfile("tactile");
		default3.keypressSound(true);
		default1.setActive();
		STROKES_STRUCTURE = default1;
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

	public static void resetAllProfiles(){
		CONFIG.getSavedProfiles().clear();
		CONFIG.save();
		PROFILES.clear();
		initializeDefaultStrokes();
		STROKES_STRUCTURE.setControlKey(OPEN_SETTINGS_KEYBINDING);
		updateUI();
	}
}