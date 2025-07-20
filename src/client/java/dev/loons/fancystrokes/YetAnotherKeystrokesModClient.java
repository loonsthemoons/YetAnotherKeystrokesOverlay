package dev.loons.fancystrokes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.text.Text;
import java.util.ArrayList;

public class YetAnotherKeystrokesModClient implements ClientModInitializer {
	public static FancyStrokesConfig CONFIG;
	public static final ArrayList<Strokes> ACTIVE_STROKES = new ArrayList<>();
	public static StrokesStructure STROKES_STRUCTURE;

	@Override
	public void onInitializeClient() {
		STROKES_STRUCTURE = new StrokesStructure();
		CONFIG = FancyStrokesConfig.getInstance();

		if (CONFIG.getSavedStrokes().isEmpty()) {
			System.out.println("No saved strokes found, initializing default strokes.");
			STROKES_STRUCTURE.initializeDefaultStrokes(); // Füllt die EINE STROKES_STRUCTURE mit Defaults
			saveStrokesToConfig(); // Speichert diese Defaults sofort
		} else {
			System.out.println("Loading strokes from config file.");
			for (dev.loons.fancystrokes.config.StrokeData data : CONFIG.getSavedStrokes()) {
				Strokes stroke = data.toStroke();
				stroke.setShowKeybindText(data.showKeybindText);
				stroke.setOutlineStatus(data.outlineStatus);
				STROKES_STRUCTURE.addStroke(stroke); // Füllt die EINE STROKES_STRUCTURE mit geladenen Strokes
			}
		}
		ACTIVE_STROKES.clear();
		ACTIVE_STROKES.addAll(STROKES_STRUCTURE.getStrokes());


		StrokeOptions menuScreen = new StrokeOptions(Text.literal("FancyStrokes Options"), STROKES_STRUCTURE);
		StrokesView strokesView = new StrokesView(STROKES_STRUCTURE);
		StrokesController strokesController = new StrokesController(strokesView, STROKES_STRUCTURE, menuScreen);

		strokesView.renderOverlay();
	}

	public void initializeDefaultStrokes() {
		STROKES_STRUCTURE.clearAll();
		STROKES_STRUCTURE.initializeDefaultStrokes();
	}

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