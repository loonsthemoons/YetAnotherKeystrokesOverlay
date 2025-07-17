package dev.loons.fancystrokes;


import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class StrokesController {
    StrokesView strokesView;
    StrokesStructure structure;
    private KeyBinding keyBinding;
    private StrokeOptions menuScreen;
    private boolean menuStatus = false;

    public StrokesController(StrokesView strokesView, StrokesStructure structure){
        this.strokesView = strokesView;
        this.structure = structure;
        strokesView.setStructure(structure);
        menuScreen = new StrokeOptions(Text.literal("FancyStrokes Options"));
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

            if(minecraftClient.options.forwardKey.isPressed()){
                structure.getStrokes().get(0).update(0xFF00FF00);
            } else {
                structure.getStrokes().get(0).update(0xFFFF0000);
            }

            if(minecraftClient.options.leftKey.isPressed()){
                structure.getStrokes().get(1).update(0xFF00FF00);
            } else {
                structure.getStrokes().get(1).update(0xFFFF0000);
            }

             if(minecraftClient.options.backKey.isPressed()){
                 structure.getStrokes().get(2).update(0xFF00FF00);
             } else {
                 structure.getStrokes().get(2).update(0xFFFF0000);
             }

            if(minecraftClient.options.rightKey.isPressed()){
                structure.getStrokes().get(3).update(0xFF00FF00);
            } else {
                structure.getStrokes().get(3).update(0xFFFF0000);
            }

            if(minecraftClient.options.attackKey.isPressed()){
                structure.getStrokes().get(4).update(0xFF00FF00);
            } else {
                structure.getStrokes().get(4).update(0xFFFF0000);
            }

            if(minecraftClient.options.useKey.isPressed()){
                structure.getStrokes().get(5).update(0xFF00FF00);
            } else {
                structure.getStrokes().get(5).update(0xFFFF0000);
            }
            while (keyBinding.wasPressed()) {
                if (menuStatus){
                    menuScreen.closeScreen(); // Kind of redundant at this point, keeping it for the sake of completion
                } else {
                    menuScreen.openScreen();
                }
            }
        });
    }

}
