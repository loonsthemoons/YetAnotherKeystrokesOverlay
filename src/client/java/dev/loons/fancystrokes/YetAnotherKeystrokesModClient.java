package dev.loons.fancystrokes;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;

public class YetAnotherKeystrokesModClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		StrokesStructure structure = new StrokesStructure();
		structure.addStroke(new StrokesModel(new Vec3d(60,50,0), ColorHelper.Argb.getArgb(255,255,0,0),20,20));
		structure.addStroke(new StrokesModel(new Vec3d(30,80,0), ColorHelper.Argb.getArgb(255,255,0,0),20,20));
		structure.addStroke(new StrokesModel(new Vec3d(60,80,0), ColorHelper.Argb.getArgb(255,255,0,0),20,20));
		structure.addStroke(new StrokesModel(new Vec3d(90,80,0), ColorHelper.Argb.getArgb(255,255,0,0),20,20));
		structure.addStroke(new StrokesModel(new Vec3d(30,110,0), ColorHelper.Argb.getArgb(255,255,0,0),30,10));
		structure.addStroke(new StrokesModel(new Vec3d(80,110,0), ColorHelper.Argb.getArgb(255,255,0,0),30,10));

		// Creates strokesView to build the GUI elements
		StrokesView strokesView = new StrokesView();
		// Creates InputController to register WASD input
		StrokesController strokesController = new StrokesController(strokesView, structure);
		strokesView.renderOverlay();
	}
}