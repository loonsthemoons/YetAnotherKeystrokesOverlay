package dev.loons.fancystrokes;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class Strokes extends ClickableWidget {
    private Vec3d position;
    private int color;
    private int pressedColor = 0xFFFF0000;
    private int outlineColor;
    private int pressedOutlienColor;
    private int roundness;
    private boolean isVisible = true;
    private InputType inputType;
    private boolean isSelected = false;

    public enum InputType {
        FORWARD, BACK, LEFT, RIGHT, ATTACK, USE, SNEAK, SPRINT, JUMP, NULL
    }

    public Strokes(Vec3d position, int color, int width, int height, InputType inputType){
        super((int) position.x, (int) position.y, width, height, Text.empty());
        this.position = position;
        this.color = color;
        this.inputType = inputType;

    }

    public Vec3d getPosition(){return position;}
    public int getColor(){return color;}
    public InputType getInputType(){return inputType;}
    public void setPosition(Vec3d position){
        this.position = position;
        this.setX((int) position.x);
        this.setY((int) position.y);
    }
    public void setColor(int color){this.color = color;}
    public int getPressedColor() {return pressedColor;}
    public void setPressedColor(int pressedColor) {this.pressedColor = pressedColor;}
    public int getOutlineColor() {return outlineColor;}
    public void setOutlineColor(int outlineColor) {this.outlineColor = outlineColor;}
    public int getPressedOutlienColor() {return pressedOutlienColor;}
    public void setPressedOutlienColor(int pressedOutlienColor) {this.pressedOutlienColor = pressedOutlienColor;}
    public int getRoundness() {return roundness;}
    public void setRoundness(int roundness) {this.roundness = roundness;}
    public void setWidth(int width){this.width = width;}
    public void setInputType(InputType type) {this.inputType = type;}
    public boolean isVisible(){return isVisible;}
    public void setVisible(boolean isVisible){this.isVisible = isVisible;}
    public boolean isHovered(int mouseX, int mouseY){return this.active && this.visible && mouseX >= this.getX() && mouseX < this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY < this.getY() + this.getHeight();}
    public boolean isSelected() {return isSelected;}
    public void setSelected(boolean selected) {isSelected = selected;}

        @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if(!this.isVisible){
        } else {
            int drawX = this.getX();
            int drawY = this.getY();
            int drawWidth = this.getWidth();
            int drawHeight = this.getHeight();
            int color = this.color;

            context.fill(drawX, drawY, drawX + drawWidth, drawY + drawHeight, color);
            if(this.isHovered(mouseX, mouseY)){
                context.drawBorder(drawX, drawY, drawWidth, drawHeight, 0xFFFFFFFF);
            }
            if(this.isSelected){
                context.drawBorder(drawX, drawY, drawWidth, drawHeight, 0xFFFFFFFF);
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

    public void update(int color){
        this.color = color;
    }
}