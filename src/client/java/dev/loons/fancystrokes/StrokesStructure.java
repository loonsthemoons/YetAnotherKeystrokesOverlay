package dev.loons.fancystrokes;

import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import java.util.ArrayList;
import static dev.loons.fancystrokes.Strokes.InputType;

public class StrokesStructure {
    private ArrayList<Strokes> strokes = new ArrayList<>();

    public StrokesStructure(){
        // Initializer
    }

    public void initializeDefaultStrokes(){
        strokes.add(new Strokes(new Vec3d(60,50,0), ColorHelper.Argb.getArgb(255,255,0,0),20,20, InputType.FORWARD));
        strokes.add(new Strokes(new Vec3d(30,80,0), ColorHelper.Argb.getArgb(255,255,0,0),20,20, InputType.LEFT));
        strokes.add(new Strokes(new Vec3d(60,80,0), ColorHelper.Argb.getArgb(255,255,0,0),20,20, InputType.BACK));
        strokes.add(new Strokes(new Vec3d(90,80,0), ColorHelper.Argb.getArgb(255,255,0,0),20,20, InputType.RIGHT));
        strokes.add(new Strokes(new Vec3d(30,110,0), ColorHelper.Argb.getArgb(255,255,0,0),30,10, InputType.ATTACK));
        strokes.add(new Strokes(new Vec3d(80,110,0), ColorHelper.Argb.getArgb(255,255,0,0),30,10, InputType.USE));
    }

    public ArrayList<Strokes> getStrokes() {
        return strokes;
    }
    public Strokes getSpecificStroke(int x){return strokes.get(x);}
    public Strokes getStrokeByInputType(InputType inputType){
        for(Strokes s : strokes){
            if(s.getInputType() == inputType){
                return s;
            }
        }
        return null;
    }
    public void addStroke(Strokes stroke){
        this.strokes.add(stroke);
    }
    public void removeStroke(Strokes stroke){
        this.strokes.remove(stroke);
    }

}
