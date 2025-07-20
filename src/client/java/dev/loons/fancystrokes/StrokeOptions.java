package dev.loons.fancystrokes;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.toast.SystemToast;
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

    public void openScreen(){
        MinecraftClient.getInstance().setScreen(
                new StrokeOptions(Text.empty(),structure)
        );
    }

    @Override
    protected void init() {
        for(Strokes strokes : strokesArrayList){
            this.addDrawableChild(strokes);
        }

        ButtonWidget mouseWidget = ButtonWidget.builder(Text.of("Mouse Strokes"), (btn) -> {
            // Button for removing / adding Mouse-Strokes
            if(structure.getSpecificStroke(4).isVisible() || structure.getSpecificStroke(5).isVisible()){
                assert this.client != null;
                this.client.getToastManager().add(
                        SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE, Text.of("Stroke Status"), Text.of("Mouse strokes disabled"))
                );
                structure.getStrokeByInputType(InputType.ATTACK).setVisible(false);
                structure.getStrokeByInputType(InputType.USE).setVisible(false);
            } else {
                assert this.client != null;
                this.client.getToastManager().add(
                        SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE, Text.of("Stroke Status"), Text.of("Mouse strokes enabled"))
                );
                structure.getStrokeByInputType(InputType.ATTACK).setVisible(true);
                structure.getStrokeByInputType(InputType.USE).setVisible(true);
            }
        }).dimensions(600, 40, 120, 20).build();

        // Widget for closing the menu
        ButtonWidget closeWidget = ButtonWidget.builder(Text.of("Close"),(btn) -> {
            this.close();
                }).dimensions(600,70,120,20).build();

        // Widget for creating a new Stroke
        ButtonWidget createWidget = ButtonWidget.builder(Text.of("Create Stroke"),(btn -> {
            structure.createStroke(InputType.NULL);
            strokesArrayList.add(structure.getLast());
            this.addDrawableChild(structure.getLast());
        })).dimensions(600,100,120,20).build();

        this.addDrawableChild(mouseWidget);
        this.addDrawableChild(closeWidget);
        this.addDrawableChild(createWidget);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawText(this.textRenderer, "Fancy-Strokes", 40, 40 - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean superClicked = super.mouseClicked(mouseX, mouseY, button);

        if(button==0){
            for(Strokes strokes : strokesArrayList){
                if(strokes.isVisible() && strokes.isHovered()){
                    this.currentStroke = strokes;
                    this.mouseOffsetX = mouseX - strokes.getX();
                    this.mouseOffsetY = mouseY - strokes.getY();
                    MinecraftClient.getInstance().player.sendMessage(Text.of("Dragging"));
                    return true;
                }
            }
        }

        if(!superClicked && button ==1){
            for (Strokes strokes : strokesArrayList){
                if(strokes.isVisible() && strokes.isHovered()){
                    MinecraftClient.getInstance().player.sendMessage(Text.of("Open Menu"));
                    MinecraftClient.getInstance().setScreen(new StrokeEditScreen(Text.empty(),strokes, this));
                    return true;
                }
            }
        }
        return superClicked;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.currentStroke != null && button == 0) {
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
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(currentStroke!=null){
            currentStroke=null;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        super.close();
    }
}