package dev.loons.fancystrokes;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Manages the rendering of keystroke displays on the HUD.
 * It registers a callback to render all visible strokes maintained by the {@link StrokesStructure}.
 */
public class StrokesView {
    private final StrokesStructure structure;
    private final ArrayList<StrokesStructure> profiles;

    /**
     * Constructs a new StrokesView with the given {@link StrokesStructure}.
     *
     * @param structure The {@link StrokesStructure} containing the strokes to be rendered.
     */
    public StrokesView(StrokesStructure structure, ArrayList<StrokesStructure> profiles){
        this.structure = structure;
        this.profiles = profiles;
    }

    /**
     * Registers a {@link HudRenderCallback} to draw all active and visible strokes.
     * Each visible stroke in the {@link StrokesStructure} will be rendered on the HUD.
     */
    public void renderOverlay(){
        HudRenderCallback.EVENT.register((drawContext, tickDelta) ->{
            ArrayList<Strokes> strokesToRender = findActiveStructure(profiles).getStrokes();
            for(Strokes strokes : strokesToRender){
                if(strokes.isVisible()){
                    strokes.render(drawContext, 0, 0, tickDelta);
                }
            }
        });
    }

    public StrokesStructure findActiveStructure(ArrayList<StrokesStructure> profiles){
        for (StrokesStructure s : profiles){
            if (s.getActive()){
                return s;
            }
        }
        return null;
    }
}
