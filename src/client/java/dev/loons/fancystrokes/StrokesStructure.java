package dev.loons.fancystrokes;

import java.util.ArrayList;

public class StrokesStructure {
    private ArrayList<StrokesModel> strokes = new ArrayList<>();

    public StrokesStructure(){
        // Initializer
    }

    public ArrayList<StrokesModel> getStrokes() {
        return strokes;
    }

    public StrokesModel getSpecificStroke(int x){return strokes.get(x);}

    public void addStroke(StrokesModel stroke){
        this.strokes.add(stroke);
    }

    public void removeStroke(StrokesModel stroke){
        this.strokes.remove(stroke);
    }
}
