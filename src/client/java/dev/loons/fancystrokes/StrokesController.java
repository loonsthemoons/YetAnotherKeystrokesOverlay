package dev.loons.fancystrokes;


import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.text.Text;

public class StrokesController {
    StrokesView strokesView = new StrokesView();
    StrokesStructure structure = new StrokesStructure();

    public StrokesController(StrokesView strokesView, StrokesStructure structure){
        this.strokesView = strokesView;
        this.structure = structure;
        strokesView.setStructure(structure);
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
                //minecraftClient.player.sendMessage(Text.literal("Left Click"));
                structure.getStrokes().get(4).update(0xFF00FF00);
            } else {
                structure.getStrokes().get(4).update(0xFFFF0000);
            }

            if(minecraftClient.options.useKey.isPressed()){
                structure.getStrokes().get(5).update(0xFF00FF00);
            } else {
                structure.getStrokes().get(5).update(0xFFFF0000);
            }

        });
    }

}
