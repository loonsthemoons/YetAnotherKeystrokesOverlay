package dev.loons.fancystrokes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;

/**
 * A class to manage sound inputs and play them with the according volume
 */
public class StrokeSounds {
    public static void playSound(String profile, float volume){
        MinecraftClient client = MinecraftClient.getInstance();
        SoundEvent soundToPlay = null;

        switch (profile) {
            case "linear":
                soundToPlay = SoundLibrary.KEYBOARD_LINEAR_CLICK;
                volume = volume * 1.1f;
                break;
            case "tactile":
                soundToPlay = SoundLibrary.KEYBOARD_TACTILE_CLICK;
                volume=volume * 0.4f;
                break;
            case "clicky":
                soundToPlay = SoundLibrary.KEYBOARD_CLICKY_CLICK;
                volume= volume * 0.2f;
                break;
            default:
                volume = 1.0f;
                return;
        }
        if (soundToPlay != null) {
            //client.player.sendMessage(Text.literal("Sound played + Volume " + volume)); // debug
            client.player.playSound(soundToPlay, SoundCategory.MASTER, volume, 1.0f);
        }
    }
}
