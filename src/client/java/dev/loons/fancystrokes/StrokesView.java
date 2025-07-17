package dev.loons.fancystrokes;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;


import java.awt.*;
import java.util.ArrayList;

public class StrokesView {
    private StrokesRenderer keyboardStroke = new StrokesRenderer();
    private StrokesStructure structure;

    public StrokesView(){
    }

    public void renderOverlay(){
        MinecraftClient client = MinecraftClient.getInstance();

        HudRenderCallback.EVENT.register((drawContext, tickDelta) ->{
            ArrayList<StrokesModel> strokesToRender = structure.getStrokes();
            for(StrokesModel strokesModel : strokesToRender){
                keyboardStroke.render(drawContext, strokesModel);
            }
        });
    }

    public void setStructure(StrokesStructure structure) {
        this.structure = structure;
    }

    public StrokesStructure getStructure() {
        return structure;
    }
}
