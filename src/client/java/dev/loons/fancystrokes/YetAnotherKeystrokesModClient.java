package dev.loons.fancystrokes;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;

public class YetAnotherKeystrokesModClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		StrokesStructure structure = new StrokesStructure();
		structure.initializeDefaultStrokes();

		// Creates the menu screen for FancyStrokes
		StrokeOptions menuScreen = new StrokeOptions(Text.literal("FancyStrokes Options"), structure);

		// Creates strokesView to build the GUI elements
		StrokesView strokesView = new StrokesView(structure);
		// Creates InputController to register WASD input
		StrokesController strokesController = new StrokesController(strokesView, structure, menuScreen);
		strokesView.renderOverlay();
	}
}