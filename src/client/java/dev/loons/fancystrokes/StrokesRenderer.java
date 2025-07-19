package dev.loons.fancystrokes;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Util;

public class StrokesRenderer {
    public void render(DrawContext context, Strokes strokes){
        int color = strokes.getColor();
        //int color = 0xFFFF0000; // Red
        //int targetColor = 0xFF00FF00; // Green
        int width = strokes.getWidth();
        int height = strokes.getHeight();
        int x = (int) strokes.getPosition().x;
        int y = (int) strokes.getPosition().y;
        double currentTime = Util.getMeasuringTimeMs() / 1000.0;
        context.fill(x,y,x+width,y+height,0,color);
    }

}
