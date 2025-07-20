package dev.loons.fancystrokes;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.text.Text;
import java.util.ArrayList;

/**
 * The main client-side initializer for the FancyStrokes mod.
 * This class handles the initialization of configurations, stroke management,
 * and UI components upon the client's mod's startup.
 */
public class YetAnotherKeystrokesModClient implements ClientModInitializer {
	public static FancyStrokesConfig CONFIG;
	public static final ArrayList<Strokes> ACTIVE_STROKES = new ArrayList<>();
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

		if (CONFIG.getSavedStrokes().isEmpty()) {
			System.out.println("No saved strokes found, initializing default strokes.");
			STROKES_STRUCTURE.initializeDefaultStrokes();
			saveStrokesToConfig();
		} else {
			System.out.println("Loading strokes from config file.");
			for (dev.loons.fancystrokes.config.StrokeData data : CONFIG.getSavedStrokes()) {
				Strokes stroke = data.toStroke();
				stroke.setShowKeybindText(data.showKeybindText);
				stroke.setOutlineStatus(data.outlineStatus);
				STROKES_STRUCTURE.addStroke(stroke);
			}
		}
		ACTIVE_STROKES.clear();
		ACTIVE_STROKES.addAll(STROKES_STRUCTURE.getStrokes());


		StrokeOptions menuScreen = new StrokeOptions(Text.literal("FancyStrokes Options"), STROKES_STRUCTURE);
		StrokesView strokesView = new StrokesView(STROKES_STRUCTURE);
		StrokesController strokesController = new StrokesController(strokesView, STROKES_STRUCTURE, menuScreen);

		strokesView.renderOverlay();
	}

	/**
	 * Initializes or resets the keystroke structure to its default settings.
	 */
	public void initializeDefaultStrokes() {
		STROKES_STRUCTURE.clearAll();
		STROKES_STRUCTURE.initializeDefaultStrokes();
	}

	/**
	 * Saves the current keystroke configuration to the mod's configuration file.
	 */
	public static void saveStrokesToConfig() {
		ArrayList<dev.loons.fancystrokes.config.StrokeData> dataToSave = new ArrayList<>();
		for (Strokes stroke : STROKES_STRUCTURE.getStrokes()) {
			dataToSave.add(new dev.loons.fancystrokes.config.StrokeData(stroke));
		}
		CONFIG.setSavedStrokes(dataToSave);
		CONFIG.save();
		System.out.println("Strokes config saved.");
	}
}