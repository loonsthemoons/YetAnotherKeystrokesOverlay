package dev.loons.fancystrokes;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundLibrary {
    private SoundLibrary(){
        // private empty constructor to avoid accidental instantiation
    }
    public static final SoundEvent KEYBOARD_LINEAR_CLICK = registerSound("keyboard.linear_click");
    public static final SoundEvent KEYBOARD_TACTILE_CLICK = registerSound("keyboard.tactile_click");
    public static final SoundEvent KEYBOARD_CLICKY_CLICK = registerSound("keyboard.clicky_click");

    // actual registration of all the custom SoundEvents
    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.of(YetAnotherKeystrokesMod.MOD_ID, id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

    public static void initialize() {
        YetAnotherKeystrokesMod.LOGGER.info("Registering Keystroke Sounds for " + YetAnotherKeystrokesMod.MOD_ID);
        KEYBOARD_LINEAR_CLICK.getId();
        KEYBOARD_TACTILE_CLICK.getId();
        KEYBOARD_CLICKY_CLICK.getId();
    }
}
