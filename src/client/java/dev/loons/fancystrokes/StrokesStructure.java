package dev.loons.fancystrokes;

import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import java.util.ArrayList;
import static dev.loons.fancystrokes.Strokes.InputType;

public class StrokesStructure {
    private ArrayList<Strokes> strokes = new ArrayList<>();
    private int defaultColor = ColorHelper.Argb.getArgb(255, 255, 0, 0); // Red
    private int defaultPressedColor = ColorHelper.Argb.getArgb(255, 0, 255, 0); // Green
    private int defaultOutlineColor = ColorHelper.Argb.getArgb(255, 255, 255, 255); // White
    private int defaultPressedOutlineColor = ColorHelper.Argb.getArgb(255, 255, 255, 0); // Gelb (für Hover/Selected + Pressed)
    private int defaultRoundness = 0; // Keine Rundung standardmäßig
    private int defaultWidth = 20;
    private int defaultHeight = 20;

    public StrokesStructure(){
        // Initializer
    }

    public void initializeDefaultStrokes(){
        strokes.add(new Strokes(
                new Vec3d(60,50,0),
                defaultColor,
                defaultPressedColor,
                defaultOutlineColor,
                defaultPressedOutlineColor,
                defaultWidth,
                defaultHeight,
                InputType.FORWARD,
                defaultRoundness
        ));
        strokes.add(new Strokes(
                new Vec3d(30,80,0),
                defaultColor,
                defaultPressedColor,
                defaultOutlineColor,
                defaultPressedOutlineColor,
                defaultWidth,
                defaultHeight,
                InputType.LEFT,
                defaultRoundness
        ));
        strokes.add(new Strokes(
                new Vec3d(60,80,0),
                defaultColor,
                defaultPressedColor,
                defaultOutlineColor,
                defaultPressedOutlineColor,
                defaultWidth,
                defaultHeight,
                InputType.BACK,
                defaultRoundness
        ));
        strokes.add(new Strokes(
                new Vec3d(90,80,0),
                defaultColor,
                defaultPressedColor,
                defaultOutlineColor,
                defaultPressedOutlineColor,
                defaultWidth,
                defaultHeight,
                InputType.RIGHT,
                defaultRoundness
        ));
        strokes.add(new Strokes(
                new Vec3d(30,110,0),
                defaultColor,
                defaultPressedColor,
                defaultOutlineColor,
                defaultPressedOutlineColor,
                30, // Breite geändert
                10, // Höhe geändert
                InputType.ATTACK,
                defaultRoundness
        ));
        strokes.add(new Strokes(
                new Vec3d(80,110,0),
                defaultColor,
                defaultPressedColor,
                defaultOutlineColor,
                defaultPressedOutlineColor,
                30, // Breite geändert
                10, // Höhe geändert
                InputType.USE,
                defaultRoundness
        ));
        // Fügen Sie hier weitere Strokes hinzu, falls nötig
    }

    public void createStroke(InputType inputType){
        strokes.add(new Strokes(
                new Vec3d(200,200,0), // Feste Position für den neuen Stroke
                defaultColor,
                defaultPressedColor,
                defaultOutlineColor,
                defaultPressedOutlineColor,
                defaultWidth,
                defaultHeight,
                inputType,
                defaultRoundness
        ));
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
    public Strokes getLast(){
        return strokes.get(strokes.size()-1);
    }

}
