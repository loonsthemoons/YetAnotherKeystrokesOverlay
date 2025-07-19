package dev.loons.fancystrokes;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;



import java.util.ArrayList;

public class StrokesView {
    private StrokesRenderer keyboardStroke = new StrokesRenderer();
    private StrokesStructure structure;

    public StrokesView(StrokesStructure structure){
        this.structure = structure;
    }

    public void renderOverlay(){
        MinecraftClient client = MinecraftClient.getInstance();

        HudRenderCallback.EVENT.register((drawContext, tickDelta) ->{
            ArrayList<Strokes> strokesToRender = structure.getStrokes();
            for(Strokes strokes : strokesToRender){
                if(strokes.isVisible()){
                    keyboardStroke.render(drawContext, strokes);
                }
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
