package dev.loons.fancystrokes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;

public class StrokeSounds {
    public static void playSound(String profile){
        MinecraftClient client = MinecraftClient.getInstance();
        SoundEvent soundToPlay = null;
        float volume = 1.0f;

        switch (profile) {
            case "linear":
                soundToPlay = SoundLibrary.KEYBOARD_LINEAR_CLICK;
                volume=1.1f;
                break;
            case "tactile":
                soundToPlay = SoundLibrary.KEYBOARD_TACTILE_CLICK;
                volume=0.4f;
                break;
            case "clicky":
                soundToPlay = SoundLibrary.KEYBOARD_CLICKY_CLICK;
                volume=0.2f;
                break;
            default:
                volume = 1.0f;
                return;
        }
        if (soundToPlay != null) {
            // client.player.sendMessage(Text.literal("Sound played")); // debug
            client.player.playSound(soundToPlay, volume, 1.0f);
        }
    }
}
