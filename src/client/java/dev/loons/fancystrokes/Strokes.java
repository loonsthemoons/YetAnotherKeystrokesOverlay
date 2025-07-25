package dev.loons.fancystrokes;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

/**
 * Represents a customizable keystroke display widget.
 * This class extends {@link ClickableWidget} to allow for interactive elements
 * and handles rendering of the keystroke with various customizable properties
 * like color, size, position, and text.
 */
public class Strokes extends ClickableWidget {
    private Vec3d position;
    private int color;
    private int pressedColor;
    private int outlineColor;
    private int pressedOutlineColor;
    private int textColor = 0xFFFFFFFF;
    private int pressedTextColor = 0xFFFFFFFF;
    private int roundness;
    private boolean outlineStatus = false;
    private boolean isVisible = true;
    private InputType inputType;
    private boolean isSelected = false;
    private boolean isPressed = false;
    private boolean previousPress=false;
    private boolean showKeybindText = true;
    private String keystrokeText;
    private boolean letteringOption=false;
    private boolean keypressSound=false;
    private String soundProfile="linear";
    private float volume = 1.0f;

    /**
     * Defines the types of input actions that a stroke can represent.
     */
    public enum InputType {
        FORWARD, BACK, LEFT, RIGHT, ATTACK, USE, SNEAK, SPRINT, JUMP, NULL
    }

    /**
     * Constructs a new Strokes object.
     *
     * @param position The 3D vector representing the top-left corner position of the stroke.
     * @param color The default background color of the stroke.
     * @param pressedColor The background color of the stroke when it is pressed.
     * @param outlineColor The default outline color of the stroke.
     * @param pressedOutlineColor The outline color of the stroke when it is pressed.
     * @param width The width of the stroke.
     * @param height The height of the stroke.
     * @param inputType The {@link InputType} associated with this stroke.
     * @param roundness The roundness of the corners for the stroke's rectangle. (from 0 to 15)
     */
    public Strokes(Vec3d position, int color, int pressedColor, int outlineColor, int pressedOutlineColor, int textColor, int pressedTextColor, int width, int height, InputType inputType, int roundness){
        super((int) position.x, (int) position.y, width, height, Text.empty());
        this.position = position;
        this.color = color;
        this.pressedColor = pressedColor;
        this.outlineColor = outlineColor;
        this.pressedOutlineColor = pressedOutlineColor;
        this.textColor = textColor;
        this.pressedTextColor = pressedTextColor;
        this.width = width;
        this.height = height;
        this.inputType = inputType;
        this.roundness = roundness;
    }


    public String getKeyTextForInputType() {
        if(!letteringOption){
            return switch (this.inputType) {
                case FORWARD -> "W";
                case BACK -> "S";
                case LEFT -> "A";
                case RIGHT -> "D";
                case JUMP -> "---";
                case ATTACK -> "LMB";
                case USE -> "RMB";
                case SNEAK -> "Shift";
                case SPRINT -> "Ctrl";
                default -> "";
            };
        } else {
            return switch (this.inputType) {
                case FORWARD -> "↑";
                case BACK -> "↓";
                case LEFT -> "←";
                case RIGHT -> "→";
                case JUMP -> "───";
                case ATTACK -> "<|";
                case USE -> "|>";
                case SNEAK -> "▂▂";
                case SPRINT -> "▶▶";
                default -> "";
            };
        }
    }

    public void setLetteringOption(boolean letteringOption){
        this.letteringOption=letteringOption;
    }

    /**
     * Renders the stroke on the screen.
     * This method draws the rounded rectangle, its outline (if enabled),
     * a hover/selected outline, and the keybind text.
     *
     * @param context The drawing context.
     * @param mouseX The X-coordinate of the mouse.
     * @param mouseY The Y-coordinate of the mouse.
     * @param delta The partial tick delta.
     */
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if(!this.isVisible){
        } else {
            int drawX = this.getX();
            int drawY = this.getY();
            int drawWidth = this.getWidth();
            int drawHeight = this.getHeight();
            int fillColorToUse = isPressed ? this.pressedColor : this.color;
            int outlineColorToUse = isPressed ? this.pressedOutlineColor : this.outlineColor;
            int textColorToUse = isPressed ? this.pressedTextColor : this.textColor;

            FancyStrokesRenderer.drawRoundedRect(context, drawX, drawY, drawWidth, drawHeight, this.roundness, fillColorToUse);
            if (outlineStatus) {
                FancyStrokesRenderer.drawRoundedOutline(context, drawX, drawY, drawWidth, drawHeight, this.roundness, outlineColorToUse);
            }

            if (this.isHovered(mouseX, mouseY) || this.isSelected) {
                FancyStrokesRenderer.drawRoundedOutline(context, drawX - 1, drawY - 1, drawWidth + 2, drawHeight + 2, this.roundness + 1, 0xFFFFFFFF);
            }

            if (this.showKeybindText) {
                String textToShow = keystrokeText;

                if(keystrokeText==null){
                    textToShow = getKeyTextForInputType();
                } else if(textToShow.isBlank()){
                    textToShow = getKeyTextForInputType();
                }
                if (!textToShow.isEmpty()) {
                    TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                    double offsetY = (double) (drawHeight - textRenderer.fontHeight) / 2;
                    int centerX = (int) Math.round(drawX + (float) drawWidth / 2);
                    int centerY = drawY + (int) Math.round(offsetY);
                    context.drawCenteredTextWithShadow(textRenderer, Text.literal(textToShow), centerX, centerY, textColorToUse);
                }
            }

            super.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {}

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    public boolean isHovered() {
        return super.isHovered();
    }

    /**
     * Updates the pressed state of the stroke.
     * @param isPressed True if the associated input is currently pressed, false otherwise.
     */
    public void update(boolean isPressed){
        this.isPressed = isPressed;
        playKeystrokeSound();
        previousPress=isPressed;
    }

    public void playKeystrokeSound(){
        boolean isMouseStroke=(inputType==InputType.ATTACK || inputType==InputType.USE);
        if(isPressed && !previousPress && keypressSound && !isMouseStroke){
            StrokeSounds.playSound(soundProfile, volume);
        }
    }

    // Getter and Setters
    public Vec3d getPosition(){return position;}
    public int getColor(){return color;}
    public InputType getInputType(){return inputType;}
    public void setPosition(Vec3d position){
        this.position = position;
        this.setX((int) position.x);
        this.setY((int) position.y);}
    public void setColor(int color){this.color = color;}
    public int getPressedColor() {return pressedColor;}
    public void setPressedColor(int pressedColor) {this.pressedColor = pressedColor;}
    public int getOutlineColor() {return outlineColor;}
    public void setOutlineColor(int outlineColor) {this.outlineColor = outlineColor;}
    public int getPressedOutlineColor() {return pressedOutlineColor;}
    public void setPressedOutlineColor(int pressedOutlineColor) {this.pressedOutlineColor = pressedOutlineColor;}
    public int getRoundness() {return roundness;}
    public void setRoundness(int roundness) {this.roundness = roundness;}
    public void setWidth(int width){this.width = width;}
    public void setHeight(int height){this.height = height;}
    public void setInputType(InputType type) {this.inputType = type;}
    public boolean isVisible(){return isVisible;}
    public void setVisible(boolean isVisible){this.isVisible = isVisible;}
    public boolean isHovered(int mouseX, int mouseY){return this.active && this.visible && mouseX >= this.getX() && mouseX < this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY < this.getY() + this.getHeight();}
    public boolean isSelected() {return isSelected;}
    public void setSelected(boolean selected) {isSelected = selected;}
    public boolean getOutlineStatus() {return outlineStatus;}
    public void setOutlineStatus(boolean outlineStatus) {this.outlineStatus = outlineStatus;}
    public boolean isShowKeybindText() { return showKeybindText; }
    public void setShowKeybindText(boolean showKeybindText) { this.showKeybindText = showKeybindText; }
    public int getTextColor() { return textColor; }
    public void setTextColor(int textColor) { this.textColor = textColor; }
    public int getPressedTextColor(){return pressedTextColor;}
    public void setPressedTextColor(int pressedTextColor){this.pressedTextColor=pressedTextColor;}
    public String getKeystrokeText() {return keystrokeText;}
    public void setKeystrokeText(String keystrokeText) {this.keystrokeText = keystrokeText;}
    public void setKeypressSound(boolean keypressSound){this.keypressSound=keypressSound;}
    public void setSoundProfile(String soundProfile){this.soundProfile=soundProfile;}
    public void setVolume(float volume) {this.volume = volume;}
}