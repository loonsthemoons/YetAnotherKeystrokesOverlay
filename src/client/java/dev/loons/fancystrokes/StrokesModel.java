package dev.loons.fancystrokes;

import net.minecraft.util.math.Vec3d;

public class StrokesModel {
    private Vec3d position;
    private int color;

    public StrokesModel(Vec3d position, int color){
        this.position = position;
        this.color = color;
    }

    public Vec3d getPosition(){return position;}
    public int getColor(){return color;}

    public void update(int color){
        this.color = color;
    }
}
