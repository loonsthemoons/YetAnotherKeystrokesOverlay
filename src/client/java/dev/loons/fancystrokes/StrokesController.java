package dev.loons.fancystrokes;


import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;

public class StrokesController {
    private StrokesView strokesView;
    private StrokesStructure structure;
    private KeyBinding keyBinding;
    private StrokeOptions menuScreen;

    public StrokesController(StrokesView strokesView, StrokesStructure structure, StrokeOptions menuScreen){
        this.strokesView = strokesView;
        this.structure = structure;
        this.menuScreen = menuScreen;
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.fancyStrokes.Options", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_R, // The keycode of the key
                "category.fancyStrokes.Keybinds" // The translation key of the keybinding's category.
        ));
        buildController();
    }

    private void buildController(){
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            assert minecraftClient.player != null;
            ArrayList<Strokes> strokesToRender = structure.getStrokes();
            for(Strokes strokes : strokesToRender){
                switch(strokes.getInputType()){
                    case FORWARD -> strokes.update(minecraftClient.options.forwardKey.isPressed());
                    case LEFT -> strokes.update(minecraftClient.options.leftKey.isPressed());
                    case BACK -> strokes.update(minecraftClient.options.backKey.isPressed());
                    case RIGHT -> strokes.update(minecraftClient.options.rightKey.isPressed());
                    case ATTACK -> strokes.update(minecraftClient.options.attackKey.isPressed());
                    case USE -> strokes.update(minecraftClient.options.useKey.isPressed());
                    case SNEAK -> strokes.update(minecraftClient.options.sneakKey.isPressed());
                    case SPRINT -> strokes.update(minecraftClient.options.sprintKey.isPressed());
                    case JUMP -> strokes.update(minecraftClient.options.jumpKey.isPressed());
                }
            }
            // Make the custom Bind "R" open the FancyStrokes Menu
            while (keyBinding.wasPressed()) {
                    menuScreen.openScreen();
            }
        });
    }

}
