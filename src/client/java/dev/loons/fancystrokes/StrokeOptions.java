package dev.loons.fancystrokes;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import java.util.ArrayList;
import static dev.loons.fancystrokes.Strokes.InputType;

public class StrokeOptions extends Screen {
    private StrokesStructure structure;
    private ArrayList<Strokes> strokesArrayList;
    public StrokeOptions(Text title, StrokesStructure structure) {
        super(title);
        this.structure = structure;
        this.strokesArrayList = new ArrayList<>();
        this.strokesArrayList.addAll(structure.getStrokes());
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
        }).dimensions(200, 40, 120, 20).build();

        ButtonWidget closeWidget = ButtonWidget.builder(Text.of("Close"),(btn) -> {
            this.close();
                }).dimensions(200,70,120,20).build();
        this.addDrawableChild(mouseWidget);
        this.addDrawableChild(closeWidget);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawText(this.textRenderer, "Fancy-Strokes", 40, 40 - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        super.close();
    }
}