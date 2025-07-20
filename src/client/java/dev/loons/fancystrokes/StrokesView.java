package dev.loons.fancystrokes;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import java.util.ArrayList;

public class StrokesView {
    private final StrokesStructure structure;

    public StrokesView(StrokesStructure structure){
        this.structure = structure;
    }

    public void renderOverlay(){
        HudRenderCallback.EVENT.register((drawContext, tickDelta) ->{
            ArrayList<Strokes> strokesToRender = structure.getStrokes();
            for(Strokes strokes : strokesToRender){
                if(strokes.isVisible()){
                    strokes.render(drawContext, 0, 0, tickDelta);
                }
            }
        });
    }
}
