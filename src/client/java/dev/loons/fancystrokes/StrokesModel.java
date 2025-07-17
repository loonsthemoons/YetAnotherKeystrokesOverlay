package dev.loons.fancystrokes;

import net.minecraft.util.math.Vec3d;

public class StrokesModel {
    private Vec3d position;
    private int color;
    private int width;
    private int height;

    public StrokesModel(Vec3d position, int color, int width, int height){
        this.position = position;
        this.color = color;
        this.width = width;
        this.height = height;
    }

    public Vec3d getPosition(){return position;}
    public int getColor(){return color;}
    public int getWidth(){return width;}
    public int getHeight(){return height;}

    public void setPosition(Vec3d position){this.position = position;}
    public void setColor(int color){this.color = color;}
    public void setWidth(int width){this.width = width;}
    public void setHeight(int height){this.height = height;}

    public void update(int color){
        this.color = color;
    }
}
