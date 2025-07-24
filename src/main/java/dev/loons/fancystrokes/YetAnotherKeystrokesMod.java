package dev.loons.fancystrokes;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YetAnotherKeystrokesMod implements ModInitializer {
	public static final String MOD_ID = "yet-another-keystrokes-overlay";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
	}
}