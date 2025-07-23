package dev.loons.fancystrokes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class StrokeSounds {
    public static void playSound(String profile){
        MinecraftClient client = MinecraftClient.getInstance();
        SoundEvent soundToPlay = null;

        switch (profile) {
            case "linear":
                soundToPlay = SoundLibrary.KEYBOARD_LINEAR_CLICK;
                break;
            case "tactile":
                soundToPlay = SoundLibrary.KEYBOARD_TACTILE_CLICK;
                break;
            case "clicky":
                soundToPlay = SoundLibrary.KEYBOARD_CLICKY_CLICK;
                break;
            default:
                // Fallback or no sound if profile not found
                return;
        }
        if (soundToPlay != null) {
            client.player.playSound(soundToPlay, 1.0f, 1.0f);
        }
    }
}
