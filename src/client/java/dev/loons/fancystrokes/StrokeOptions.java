package dev.loons.fancystrokes;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;


import static dev.loons.fancystrokes.Strokes.InputType;

/**
 * A screen for configuring the position and properties of individual keystroke displays.
 * Users can drag and drop strokes, select multiple strokes, and access individual stroke settings.
 */
public class StrokeOptions extends Screen {
    private StrokesStructure structure;
    private ArrayList<Strokes> strokesArrayList;
    private ArrayList<StrokesStructure> profiles;
    private double mouseOffsetX;
    private double mouseOffsetY;
    private Strokes currentStroke = null;
    private static final int snapThreshhold = 5;
    private boolean isSelecting = false;
    private double selectPositionX;
    private double selectPositionY;
    private boolean isResizing = false;
    private int initialWidth;
    private int initialHeight;
    private java.util.Map<Strokes, Vector2f> initialGroupSizes;
    private ArrayList<Strokes> selectedStrokes = new ArrayList<>();
    private KeyBinding controlKey;
    private ButtonWidget popupDone;

    /**
     * Constructs a new StrokeOptions screen.
     *
     * @param title The title of the screen.
     * @param structure The {@link StrokesStructure} containing the strokes to be managed.
     */
    public StrokeOptions(Text title, StrokesStructure structure, ArrayList<StrokesStructure> profiles) {
        super(title);
        this.structure = structure;
        this.strokesArrayList = new ArrayList<>();
        this.strokesArrayList.addAll(structure.getStrokes());
        this.profiles = profiles;
        this.controlKey = structure.getControlKey();
    }

    /**
     * Snaps the current position horizontally to an alignment point with another stroke.
     * This method checks for snapping to left edge, right edge, and center.
     *
     * @param currentPos The current X position of the stroke.
     * @param currentSize The width of the current stroke.
     * @param otherPos The X position of the other stroke to snap to.
     * @param otherSize The width of the other stroke.
     * @return The snapped X position, or the original currentPos if no snap occurs.
     */
    private int snapHorizontal(int currentPos, int currentSize, int otherPos, int otherSize) {
        if (Math.abs(currentPos - otherPos) < snapThreshhold) return otherPos;
        if (Math.abs(currentPos - (otherPos + otherSize)) < snapThreshhold) return otherPos + otherSize;
        if (Math.abs((currentPos + currentSize) - otherPos) < snapThreshhold) return otherPos - currentSize;
        if (Math.abs((currentPos + currentSize) - (otherPos + otherSize)) < snapThreshhold) return otherPos + otherSize - currentSize;
        if (Math.abs((currentPos + currentSize / 2) - (otherPos + otherSize / 2)) < snapThreshhold) return (otherPos + otherSize / 2) - currentSize / 2;
        return currentPos;
    }

    /**
     * Snaps the current position vertically to an alignment point with another stroke.
     * This method checks for snapping to top edge, bottom edge, and center.
     *
     * @param currentPos The current Y position of the stroke.
     * @param currentSize The height of the current stroke.
     * @param otherPos The Y position of the other stroke to snap to.
     * @param otherSize The height of the other stroke.
     * @return The snapped Y position, or the original currentPos if no snap occurs.
     */
    private int snapVertical(int currentPos, int currentSize, int otherPos, int otherSize) {
        if (Math.abs(currentPos - otherPos) < snapThreshhold) return otherPos;
        if (Math.abs(currentPos - (otherPos + otherSize)) < snapThreshhold) return otherPos + otherSize;
        if (Math.abs((currentPos + currentSize) - otherPos) < snapThreshhold) return otherPos - currentSize;
        if (Math.abs((currentPos + currentSize) - (otherPos + otherSize)) < snapThreshhold) return otherPos + otherSize - currentSize;
        if (Math.abs((currentPos + currentSize / 2) - (otherPos + otherSize / 2)) < snapThreshhold) return (otherPos + otherSize / 2) - currentSize / 2;
        return currentPos;
    }

    /**
     * Selects strokes within a given rectangular area.
     *
     * @param posX1 The X-coordinate of the first corner of the selection area.
     * @param posY1 The Y-coordinate of the first corner of the selection area.
     * @param posX2 The X-coordinate of the second corner of the selection area.
     * @param posY2 The Y-coordinate of the second corner of the selection area.
     */
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

    /**
     * Opens a new instance of the StrokeOptions screen.
     */
    public void openScreen(){
        MinecraftClient.getInstance().setScreen(
                new StrokeOptions(Text.empty(),structure, profiles)
        );
    }

    public void setKeyBinding(KeyBinding keyBinding) {this.controlKey=keyBinding;}

    /**
     * Initializes the screen elements. This method is called when the screen is opened.
     * It clears existing children and adds all strokes from the structure as drawable children.
     */
    @Override
    protected void init() {
        super.init();

        this.clearChildren();
        this.strokesArrayList.clear();
        structure = findActiveStructure(profiles);
        this.strokesArrayList.addAll(structure.getStrokes());
        for(Strokes strokes : strokesArrayList){
            this.addDrawableChild(strokes);
        }

        if(!structure.isDidPopupShow()){
            popupDone = ButtonWidget.builder(Text.literal("Understood"), (button) -> {
                this.remove(button);
                for(StrokesStructure s : profiles){
                    s.setDidPopupShow(true);
                }
            }).dimensions(((this.width - 80)/2), (this.height/2)+80, 80, 20).build();
            this.addDrawableChild(popupDone);
        }
    }

    /**
     * Renders the StrokeOptions screen, including the background, title, and selection rectangle (if active).
     *
     * @param context The drawing context.
     * @param mouseX The X-coordinate of the mouse.
     * @param mouseY The Y-coordinate of the mouse.
     * @param delta The partial tick delta.
     */
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawText(this.textRenderer, "YAKO - Yet Another Keystrokes Overlay", 40, 40 - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);

        if(!structure.isDidPopupShow()){
            // Calculate panel dimensions and positions
            int panelHeight = 200;
            int panelWidth = 400;
            int titleOffset = 20;

            int startX = (this.width - panelWidth) / 2;
            int panelStartYOffset = this.height / 2 - (panelHeight + titleOffset) / 2;

            renderPopupWindow(context, startX, panelStartYOffset+titleOffset, panelWidth, panelHeight, Text.literal("Welcome Message!"));
        }

        if(isSelecting){
            int rectX1 = (int) Math.min(this.selectPositionX, mouseX);
            int rectY1 = (int) Math.min(this.selectPositionY, mouseY);
            int rectX2 = (int) Math.max(this.selectPositionX, mouseX);
            int rectY2 = (int) Math.max(this.selectPositionY, mouseY);

            context.fill(rectX1, rectY1, rectX2, rectY2, 0x4000FF00);
            context.drawBorder(rectX1, rectY1, rectX2 - rectX1, rectY2 - rectY1, 0xFF00FF00);
        }
    }

    private void renderPopupWindow(DrawContext context, int x, int y, int width, int height, Text title) {
        int titleY = y - 15; // 15 Pixel über der Box
        context.drawText(this.textRenderer, title, x + width / 2 - this.textRenderer.getWidth(title) / 2, titleY, 0xFFFFFFFF, true);
        context.drawCenteredTextWithShadow(this.textRenderer, "Welcome to Yet Another Keystrokes Mod", (this.width/2), (this.height/2)-60, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, "To configure a keystroke, press the configure button [R] on a keystroke", (this.width/2), (this.height/2)-40, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, "Use your left mouse button to move keystrokes", (this.width/2), (this.height/2)-20, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, "Pressing the middle mouse button creates or deletes keystrokes", (this.width/2), (this.height/2), 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, "Using the right mouse button resizes keystrokes", (this.width/2), (this.height/2)+20, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(this.textRenderer, "For more configuration, use /yako help", (this.width/2), (this.height/2)+40, 0xFFFFFFFF);

        context.fill(x, y, x + width, y + height, 0xA0000000);
        context.drawBorder(x, y, width, height, 0xFFFFFFFF);
    }

    /**
     * Finds the first stroke that the mouse is currently hovering over.
     *
     * @return The {@link Strokes} object being hovered, or null if no stroke is hovered.
     */
    private Strokes findStrokeByHover(){
        for(Strokes strokes : strokesArrayList){
            if(strokes.isVisible() && strokes.isHovered()){
                return strokes;
            }
        }
        return null;
    }

    public StrokesStructure findActiveStructure(ArrayList<StrokesStructure> profiles){
        for (StrokesStructure s : profiles){
            if (s.getActive()){
                return s;
            }
        }
        return null;
    }

    /**
     * Clears the current selection of strokes and resets the selection state.
     */
    private void clearSelection(){
        for (Strokes strokes :  selectedStrokes){
            strokes.setSelected(false);
        }
        isSelecting=false;
        selectedStrokes.clear();
    }

    /**
     * Handles mouse click events on the screen.
     * This method manages single stroke dragging, multi-stroke dragging, selection area creation,
     * opening the stroke edit screen, and creating/deleting strokes.
     *
     * @param mouseX The X-coordinate of the mouse click.
     * @param mouseY The Y-coordinate of the mouse click.
     * @param button The mouse button that was clicked (0 for left, 1 for right, 2 for middle).
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean superClicked = super.mouseClicked(mouseX, mouseY, button);

        // Left Mouse click (Dragging)
        if(button==0){
            Strokes clickedStroke = findStrokeByHover();
            if(clickedStroke != null){
                this.currentStroke = clickedStroke;
                this.mouseOffsetX = mouseX - clickedStroke.getX();
                this.mouseOffsetY = mouseY - clickedStroke.getY();
                if(selectedStrokes.contains(clickedStroke)){
                    // Case 1, clicked on Stroke in selected area
                    return true;
                } else {
                    // Case 2, clicked on a single stroke
                    clearSelection();
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

        // Right Mouse click (Resizing a stroke)
        if(button ==1){
            Strokes clickedStroke = findStrokeByHover();
            if(clickedStroke != null){
                isResizing=true;
                this.currentStroke = clickedStroke;
                this.mouseOffsetX = mouseX;
                this.mouseOffsetY = mouseY;
                this.initialWidth = clickedStroke.getWidth();
                this.initialHeight = clickedStroke.getHeight();

                if(selectedStrokes.contains(clickedStroke)){
                    // Case 1, clicked on Stroke in selected area
                    initialGroupSizes = new HashMap<>();
                    Vector2f vector2f;
                    for(Strokes strokes : selectedStrokes){
                        vector2f = new Vector2f(strokes.getWidth(), strokes.getHeight());
                        initialGroupSizes.put(strokes, vector2f);
                    }
                    return true;
                } else {
                    // Case 2, clicked on a single stroke
                    return true;
                }
            } else {
                isSelecting =false;
                selectAreaStroke(this.selectPositionX, this.selectPositionY, mouseX, mouseY);
                clearSelection();
                return true;
            }
        }

        // Middle Mouse click (Creates or Deletes Stroke)
        if(!superClicked && button ==2){
            for (Strokes strokes : strokesArrayList){
                if(strokes.isVisible() && strokes.isHovered() && selectedStrokes.isEmpty()){
                    structure.removeStroke(strokes);
                    strokesArrayList.remove(strokes);
                    this.remove(strokes);
                    return true;
                } else if (strokes.isVisible() && strokes.isHovered() && selectedStrokes.contains(strokes)){
                    structure.removeStroke(strokes);
                    for(Strokes listStrokes : selectedStrokes){
                        structure.removeStroke(listStrokes);
                        strokesArrayList.remove(listStrokes);
                        this.remove(listStrokes);
                    }
                    selectedStrokes.clear();
                    return true;
                }
            }
            structure.createStroke(InputType.NULL);
            structure.getLast().setPosition((int)mouseX, (int)mouseY);
            strokesArrayList.add(structure.getLast());
            structure.getLast().setLetteringOption(structure.getLetteringOption());
            structure.getLast().setVolume(structure.getVolume());
            structure.getLast().setKeypressSound(structure.getKeypressSound());
            structure.getLast().setSoundProfile(structure.getSoundProfile());

            this.addDrawableChild(structure.getLast());
            return true;
        }
        return superClicked;
    }

    /**
     * Handles mouse drag events. This method is used for moving single strokes,
     * moving groups of selected strokes, and drawing the selection area.
     *
     * @param mouseX The current X-coordinate of the mouse.
     * @param mouseY The current Y-coordinate of the mouse.
     * @param button The mouse button being dragged.
     * @param deltaX The change in X-coordinate since the last drag event.
     * @param deltaY The change in Y-coordinate since the last drag event.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // Left Click (Moving & Selecting Strokes)
        if(button==0){
            if (this.currentStroke != null && !isSelecting && selectedStrokes.isEmpty()) {
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
            } else if (this.currentStroke != null && !selectedStrokes.isEmpty() && selectedStrokes.contains(currentStroke)){
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
            } else if(this.isSelecting){
                // Case 3, selecting an area
                return true;
            }
        }

        // Right Click (Resizing Strokes)
        if(button==1 && isResizing){
            double deltaMouseX = mouseX - this.mouseOffsetX;
            double deltaMouseY = mouseY - this.mouseOffsetY;

            if (this.currentStroke != null && !isSelecting && selectedStrokes.isEmpty()) {
                // Case 1, resizing a single stroke
                int newWidth = (int) (this.initialWidth + deltaMouseX);
                int newHeight = (int) (this.initialHeight + deltaMouseY);

//                for (Strokes otherStroke : strokesArrayList) {
//                    if (otherStroke == this.currentStroke || !otherStroke.isVisible()) {
//                        continue;
//                    }
//                    newWidth = snapHorizontal(newWidth, this.currentStroke.getWidth(), otherStroke.getX(), otherStroke.getWidth());
//                    newHeight = snapVertical(newHeight, this.currentStroke.getHeight(), otherStroke.getY(), otherStroke.getHeight());
//                }

                this.currentStroke.setWidth(Math.max(10, newWidth));
                this.currentStroke.setHeight(Math.max(10, newHeight));
                return true;
            } else if (this.currentStroke != null && !selectedStrokes.isEmpty() && initialGroupSizes != null){
                // Case 2, resizing a group
                for(Strokes strokes : selectedStrokes){
                    Vector2f initialSize = initialGroupSizes.get(strokes);
                    if(initialSize != null){
                        int newWidth = (int) (initialSize.getX() + deltaMouseX);
                        int newHeight = (int) (initialSize.getY() + deltaMouseY);
                        strokes.setWidth(Math.max(10, newWidth));
                        strokes.setHeight(Math.max(10, newWidth));
                    }
                }
                return true;
            } else if(this.isSelecting){
                // Case 3, clear selection
                clearSelection();
                return true;
            }
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    /**
     * Handles mouse release events. This method is used to finalize dragging
     * operations or complete a selection area.
     *
     * @param mouseX The X-coordinate of the mouse release.
     * @param mouseY The Y-coordinate of the mouse release.
     * @param button The mouse button that was released.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(button==1 && isResizing){
            isResizing=false;
            currentStroke=null;
            if(initialGroupSizes != null){
                initialGroupSizes.clear();
                initialGroupSizes = null;
            }
            return true;
        }

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

    /**
     * Closes the current screen. Before closing, it clears any active selection
     * and saves the updated stroke configurations to the mod's config file.
     */
    @Override
    public void close() {
        clearSelection();
        YetAnotherKeystrokesModClient.saveProfilesToConfig();
        super.close();
    }

    /**
     * Handles keyboard key press events. Specifically, it listens for the ESCAPE key
     * to close the screen.
     *
     * @param keyCode The key code of the pressed key.
     * @param scanCode The scan code of the pressed key.
     * @param modifiers Any modifiers (e.g., Shift, Ctrl, Alt) held down.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 256) { // 256 is ESCAPE
            this.close();
            return true;
        }
        // Open menu on a keystroke
        if(controlKey.matchesKey(keyCode, scanCode)){
            for (Strokes strokes : strokesArrayList){
                if(strokes.isVisible() && strokes.isHovered()){
                    MinecraftClient.getInstance().setScreen(new StrokeEditScreen(Text.empty(),strokes, this, structure));
                    return true;
                }
            }
        }
        return false;
    }
}