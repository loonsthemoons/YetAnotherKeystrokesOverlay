package dev.loons.fancystrokes;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import static dev.loons.fancystrokes.Strokes.InputType;

public class StrokeOptions extends Screen {
    private StrokesStructure structure;
    private ArrayList<Strokes> strokesArrayList;
    private double mouseOffsetX;
    private double mouseOffsetY;
    private Strokes currentStroke = null;
    private static final int snapThreshhold = 5;
    private boolean isSelecting = false;
    private double selectPositionX;
    private double selectPositionY;
    private ArrayList<Strokes> selectedStrokes = new ArrayList<>();

    public StrokeOptions(Text title, StrokesStructure structure) {
        super(title);
        this.structure = structure;
        this.strokesArrayList = new ArrayList<>();
        this.strokesArrayList.addAll(structure.getStrokes());
    }

    private int snapHorizontal(int currentPos, int currentSize, int otherPos, int otherSize) {
        if (Math.abs(currentPos - otherPos) < snapThreshhold) return otherPos;
        if (Math.abs(currentPos - (otherPos + otherSize)) < snapThreshhold) return otherPos + otherSize;
        if (Math.abs((currentPos + currentSize) - otherPos) < snapThreshhold) return otherPos - currentSize;
        if (Math.abs((currentPos + currentSize) - (otherPos + otherSize)) < snapThreshhold) return otherPos + otherSize - currentSize;
        if (Math.abs((currentPos + currentSize / 2) - (otherPos + otherSize / 2)) < snapThreshhold) return (otherPos + otherSize / 2) - currentSize / 2;
        return currentPos;
    }

    private int snapVertical(int currentPos, int currentSize, int otherPos, int otherSize) {
        if (Math.abs(currentPos - otherPos) < snapThreshhold) return otherPos;
        if (Math.abs(currentPos - (otherPos + otherSize)) < snapThreshhold) return otherPos + otherSize;
        if (Math.abs((currentPos + currentSize) - otherPos) < snapThreshhold) return otherPos - currentSize;
        if (Math.abs((currentPos + currentSize) - (otherPos + otherSize)) < snapThreshhold) return otherPos + otherSize - currentSize;
        if (Math.abs((currentPos + currentSize / 2) - (otherPos + otherSize / 2)) < snapThreshhold) return (otherPos + otherSize / 2) - currentSize / 2;
        return currentPos;
    }

    private void selectAreaStroke(double posX1, double posY1, double posX2, double posY2){
        int selectionLeft = (int) Math.min(posX1, posX2);
        int selectionTop = (int) Math.min(posY1, posY2);
        int selectionRight = (int) Math.max(posX1, posX2);
        int selectionBottom = (int) Math.max(posY1, posY2);
        for(Strokes strokes : strokesArrayList){
            if(
                    strokes.getX() < selectionRight &&
                    strokes.getX() + strokes.getWidth() > selectionLeft &&
                    strokes.getY() < selectionBottom &&
                    strokes.getY() + strokes.getHeight() > selectionTop){
                strokes.setSelected(true);
                selectedStrokes.add(strokes);
            }
        }
    }

    public void openScreen(){
        MinecraftClient.getInstance().setScreen(
                new StrokeOptions(Text.empty(),structure)
        );
    }

    @Override
    protected void init() {
        super.init();
        for(Strokes strokes : strokesArrayList){
            this.addDrawableChild(strokes);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawText(this.textRenderer, "Fancy-Strokes", 40, 40 - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);

        if(isSelecting){
            int rectX1 = (int) Math.min(this.selectPositionX, mouseX);
            int rectY1 = (int) Math.min(this.selectPositionY, mouseY);
            int rectX2 = (int) Math.max(this.selectPositionX, mouseX);
            int rectY2 = (int) Math.max(this.selectPositionY, mouseY);

            // Transparente Füllung (optional, aber hilfreich)
            context.fill(rectX1, rectY1, rectX2, rectY2, 0x4000FF00); // Beispiel: grüner, transparenter Füllbereich (AARRGGBB)

            // Rand des Rechtecks
            context.drawBorder(rectX1, rectY1, rectX2 - rectX1, rectY2 - rectY1, 0xFF00FF00); // Beispiel: grüner Rand
        }
    }

    private Strokes findStrokeByHover(){
        for(Strokes strokes : strokesArrayList){
            if(strokes.isVisible() && strokes.isHovered()){
                return strokes;
            }
        }
        return null;
    }

    private void clearSelection(){
        for (Strokes strokes :  selectedStrokes){
            strokes.setSelected(false);
        }
        isSelecting=false;
        selectedStrokes.clear();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean superClicked = super.mouseClicked(mouseX, mouseY, button);

        // Left Mouse click (Dragging)
        if(button==0){
            Strokes clickedStroke = findStrokeByHover();
            if(clickedStroke != null){
                // Case 1, clicked on Stroke in selected area
                if(selectedStrokes.contains(clickedStroke)){
                    this.currentStroke = clickedStroke;
                    this.mouseOffsetX = mouseX - clickedStroke.getX();
                    this.mouseOffsetY = mouseY - clickedStroke.getY();
                    return true;
                } else {
                    // Case 2, clicked on a single stroke
                    clearSelection();
                    this.currentStroke = clickedStroke;
                    this.mouseOffsetX = mouseX - clickedStroke.getX();
                    this.mouseOffsetY = mouseY - clickedStroke.getY();
                    return true;
                }
            } else {
                // Case 3, did not click on a stroke
                clearSelection();
                isSelecting = true;
                selectPositionX = mouseX;
                selectPositionY = mouseY;
                return true;
            }
        }

        // Right Mouse click (Opens Menu for a stroke)
        if(!superClicked && button ==1){
            for (Strokes strokes : strokesArrayList){
                if(strokes.isVisible() && strokes.isHovered()){
                    MinecraftClient.getInstance().setScreen(new StrokeEditScreen(Text.empty(),strokes, this, structure));
                    return true;
                }
            }
        }

        // Middle Mouse click (Creates or Deletes Stroke)
        if(!superClicked && button ==2){
            for (Strokes strokes : strokesArrayList){
                if(strokes.isVisible() && strokes.isHovered()){
                    structure.removeStroke(strokes);
                    strokesArrayList.remove(strokes);
                    this.remove(strokes);
                    return true;
                }
            }
            structure.createStroke(InputType.NULL);
            structure.getLast().setPosition((int)mouseX, (int)mouseY);
            strokesArrayList.add(structure.getLast());
            this.addDrawableChild(structure.getLast());
            return true;
        }
        return superClicked;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.currentStroke != null && button == 0 && !isSelecting && selectedStrokes.isEmpty()) {
            // Case 1, moving a single stroke
            int newX = (int) (mouseX - this.mouseOffsetX);
            int newY = (int) (mouseY - this.mouseOffsetY);

            for (Strokes otherStroke : strokesArrayList) {
                if (otherStroke == this.currentStroke || !otherStroke.isVisible()) {
                    continue;
                }
                newX = snapHorizontal(newX, this.currentStroke.getWidth(), otherStroke.getX(), otherStroke.getWidth());
                newY = snapVertical(newY, this.currentStroke.getHeight(), otherStroke.getY(), otherStroke.getHeight());
            }

            newX = Math.max(0, Math.min(newX, this.width - this.currentStroke.getWidth()));
            newY = Math.max(0, Math.min(newY, this.height - this.currentStroke.getHeight()));

            this.currentStroke.setPosition(new Vec3d(newX, newY, 0));
            return true;
        } else if (button == 0 && this.currentStroke != null && !selectedStrokes.isEmpty() && selectedStrokes.contains(currentStroke)){
            // Case 2, moving a group
            int newX = (int) (mouseX - this.mouseOffsetX);
            int newY = (int) (mouseY - this.mouseOffsetY);
            newX = Math.max(0, Math.min(newX, this.width - this.currentStroke.getWidth()));
            newY = Math.max(0, Math.min(newY, this.height - this.currentStroke.getHeight()));
            double deltaMoveX = newX - this.currentStroke.getX();
            double deltaMoveY = newY - this.currentStroke.getY();
            for(Strokes strokes : selectedStrokes){
                if (strokes == this.currentStroke) {
                    strokes.setPosition(new Vec3d(newX, newY, 0));
                } else {
                    int newStrokeX = (int) (strokes.getX() + deltaMoveX);
                    int newStrokeY = (int) (strokes.getY() + deltaMoveY);

                    newStrokeX = Math.max(0, Math.min(newStrokeX, this.width - strokes.getWidth()));
                    newStrokeY = Math.max(0, Math.min(newStrokeY, this.height - strokes.getHeight()));

                    strokes.setPosition(new Vec3d(newStrokeX, newStrokeY, 0));
                }
            }
            return true;
        } else if(button == 0 && this.isSelecting){
            // Case 2, selecting an area
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(currentStroke!=null){
            currentStroke=null;
            return true;
        } else if (this.isSelecting && button==0){
            isSelecting =false;
            selectAreaStroke(this.selectPositionX, this.selectPositionY, mouseX, mouseY);
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        super.close();
    }
}