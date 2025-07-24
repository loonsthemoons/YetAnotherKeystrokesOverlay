package dev.loons.fancystrokes;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Vec3d;
import java.util.ArrayList;
import static dev.loons.fancystrokes.Strokes.InputType;

/**
 * Manages the collection of {@link Strokes} objects, providing methods to initialize
 * default strokes, add/remove strokes, and retrieve specific strokes.
 */

public class StrokesStructure {
    private ArrayList<Strokes> strokes = new ArrayList<>();
    private final int defaultColor = ColorHelper.Argb.getArgb(255, 255, 0, 0);                  // Red
    private final int defaultPressedColor = ColorHelper.Argb.getArgb(255, 0, 255, 0);           // Green
    private final int defaultOutlineColor = ColorHelper.Argb.getArgb(255, 255, 255, 255);       // White
    private final int defaultPressedOutlineColor = ColorHelper.Argb.getArgb(255, 255, 255, 0);  // Yellow
    private final int defaultRoundness = 0;
    private final int defaultWidth = 20;
    private final int defaultHeight = 20;
    private KeyBinding controlKey;
    private boolean keystrokesStatus=true;
    private String profileName;
    private boolean isActiveProfile=false;
    private boolean letteringOption=false;
    private boolean keypressSound=false;
    private String soundProfile="linear";
    private float volume=1.0f;

    public StrokesStructure(){}

    /**
     * Initializes a set of default keystrokes for WASD, RMB & LMB
     * These strokes are added to the internal list.
     */

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
                30,
                10,
                InputType.ATTACK,
                defaultRoundness
        ));
        strokes.add(new Strokes(
                new Vec3d(80,110,0),
                defaultColor,
                defaultPressedColor,
                defaultOutlineColor,
                defaultPressedOutlineColor,
                30,
                10,
                InputType.USE,
                defaultRoundness
        ));
    }

    /**
     * Creates and adds a new stroke with default appearance properties
     * except for a specified input type.
     *
     * @param inputType The {@link InputType} for the new stroke.
     */
    public void createStroke(InputType inputType){
        strokes.add(new Strokes(
                new Vec3d(200,200,0),
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

    public void disableKeystrokes(){
        for(Strokes s : strokes){
            s.setVisible(!keystrokesStatus);
        }
        keystrokesStatus=!keystrokesStatus;
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

    public void clearAll(){
        this.strokes.clear();
    }

    public void setControlKey(KeyBinding controlKey){this.controlKey=controlKey;}
    public KeyBinding getControlKey(){return controlKey;}
    public boolean getKeystrokesStatus(){return keystrokesStatus;}
    public void setProfileName(String profileName){this.profileName=profileName;}
    public String getProfileName(){return profileName;}
    public void setActive(){isActiveProfile=true;}
    public void setInactive(){isActiveProfile=false;}
    public boolean getActive(){return isActiveProfile;}
    public boolean getLetteringOption(){return letteringOption;}
    public boolean getKeypressSound(){return keypressSound;}
    public void setKeypressSound(Boolean keypressSound){this.keypressSound=keypressSound;}
    public void setSoundProfile(String soundProfile){this.soundProfile=soundProfile;}
    public String getSoundProfile(){return soundProfile;}
    public float getVolume(){return volume;}
    public void setVolume(float volume) {this.volume = volume;}

    public void letteringOption(){
        letteringOption= !letteringOption;
        for(Strokes s : strokes){
            s.setLetteringOption(letteringOption);
        }
    }
    public void keypressSound(Boolean keypressSound){
        this.keypressSound=keypressSound;
        for(Strokes s : strokes){
            s.setKeypressSound(keypressSound);
        }
    }
    public void soundProfile(String soundProfile){
        this.soundProfile=soundProfile;
        for(Strokes s : strokes){
            s.setSoundProfile(soundProfile);
        }
    }
    public void volume(float volume){
        this.volume = volume;
        for(Strokes s : strokes){
            s.setVolume(volume);
        }
    }

    @Override
    public String toString() {
        return profileName;
    }
}
