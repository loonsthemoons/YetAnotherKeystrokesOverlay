package dev.loons.fancystrokes;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Util;

public class StrokesRenderer {
    int width = 20;
    int height = 20;

    public void render(DrawContext context, StrokesModel strokesModel){
        int color = strokesModel.getColor();
        //int color = 0xFFFF0000; // Red
        //int targetColor = 0xFF00FF00; // Green
        int x = (int) strokesModel.getPosition().x;
        int y = (int) strokesModel.getPosition().y;
        double currentTime = Util.getMeasuringTimeMs() / 1000.0;
        context.fill(x,y,x+width,y+height,0,color);
    }

}
