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
                minecraftClient.player.sendMessage(Text.of("Forward"));
            }
            if(minecraftClient.options.leftKey.isPressed()){
                minecraftClient.player.sendMessage(Text.of("Left"));
            }
             if(minecraftClient.options.backKey.isPressed()){
                minecraftClient.player.sendMessage(Text.of("Back"));
            }
            if(minecraftClient.options.rightKey.isPressed()){
                minecraftClient.player.sendMessage(Text.of("Right"));
            }
        });
    }

}
