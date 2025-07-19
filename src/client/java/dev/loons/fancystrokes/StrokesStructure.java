package dev.loons.fancystrokes;

import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class StrokesStructure {
    private ArrayList<Strokes> strokes = new ArrayList<>();

    public StrokesStructure(){
        // Initializer
    }

    public void initializeDefaultStrokes(){
        strokes.add(new Strokes(new Vec3d(60,50,0), ColorHelper.Argb.getArgb(255,255,0,0),20,20));
        strokes.add(new Strokes(new Vec3d(30,80,0), ColorHelper.Argb.getArgb(255,255,0,0),20,20));
        strokes.add(new Strokes(new Vec3d(60,80,0), ColorHelper.Argb.getArgb(255,255,0,0),20,20));
        strokes.add(new Strokes(new Vec3d(90,80,0), ColorHelper.Argb.getArgb(255,255,0,0),20,20));
        strokes.add(new Strokes(new Vec3d(30,110,0), ColorHelper.Argb.getArgb(255,255,0,0),30,10));
        strokes.add(new Strokes(new Vec3d(80,110,0), ColorHelper.Argb.getArgb(255,255,0,0),30,10));
    }

    public ArrayList<Strokes> getStrokes() {
        return strokes;
    }

    public Strokes getSpecificStroke(int x){return strokes.get(x);}
    public void addStroke(Strokes stroke){
        this.strokes.add(stroke);
    }
    public void removeStroke(Strokes stroke){
        this.strokes.remove(stroke);
    }

}
