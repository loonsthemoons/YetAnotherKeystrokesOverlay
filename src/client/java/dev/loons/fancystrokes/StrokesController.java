package dev.loons.fancystrokes;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;

/**
 * Manages the control logic for the FancyStrokes mod, handling key presses
 * and updating the state of individual keystroke displays. It also manages
 * the keybinding for opening the mod's options menu.
 */
public class StrokesController {
    private StrokesView strokesView;
    private StrokesStructure structure;
    private KeyBinding keyBinding;
    private KeyBinding disableKeystrokes;
    private StrokeOptions menuScreen;
    private ArrayList<StrokesStructure> profiles;

    /**
     * Constructs a new StrokesController.
     *
     * @param strokesView The view component responsible for rendering strokes.
     * @param structure The data structure holding all defined strokes.
     * @param menuScreen The options screen for the FancyStrokes mod.
     */
    public StrokesController(StrokesView strokesView, StrokesStructure structure, StrokeOptions menuScreen, ArrayList<StrokesStructure> profiles){
        this.strokesView = strokesView;
        this.structure = structure;
        structure.setActive();
        this.profiles = profiles;
        this.menuScreen = menuScreen;
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Open Keystrokes Settings", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM,                  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_R,                        // The keycode of the key
                "Yet Another Keystrokes Mod"            // The translation key of the keybinding's category.
        ));
        disableKeystrokes = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Disable Keystrokes",       // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM,                  // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_K,                        // The keycode of the key
                "Yet Another Keystrokes Mod"            // The translation key of the keybinding's category.
        ));
        structure.setControlKey(keyBinding);
        buildController();

    }

    /**
     * Sets up the client tick event listener to update stroke states
     * based on player input and to handle the custom keybinding for
     * opening the FancyStrokes options menu.
     */
    private void buildController(){
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            assert minecraftClient.player != null;
//            ArrayList<Strokes> strokesToRender = structure.getStrokes();
            ArrayList<Strokes> strokesToRender = strokesView.findActiveStructure(profiles).getStrokes();

            for(Strokes strokes : strokesToRender){
                switch(strokes.getInputType()){
                    case FORWARD -> strokes.update(minecraftClient.options.forwardKey.isPressed());
                    case LEFT -> strokes.update(minecraftClient.options.leftKey.isPressed());
                    case BACK -> strokes.update(minecraftClient.options.backKey.isPressed());
                    case RIGHT -> strokes.update(minecraftClient.options.rightKey.isPressed());
                    case ATTACK -> strokes.update(minecraftClient.options.attackKey.isPressed());
                    case USE -> strokes.update(minecraftClient.options.useKey.isPressed());
                    case SNEAK -> strokes.update(minecraftClient.options.sneakKey.isPressed());
                    case SPRINT -> strokes.update(minecraftClient.options.sprintKey.isPressed());
                    case JUMP -> strokes.update(minecraftClient.options.jumpKey.isPressed());
                }
            }
            while (keyBinding.wasPressed()) {
                if(structure.getKeystrokesStatus())
                {menuScreen.openScreen();}
            }

            while (disableKeystrokes.wasPressed()){
                structure.disableKeystrokes();
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("keystrokes").executes(context -> {
                context.getSource().sendFeedback(Text.literal("currently active profile: " + strokesView.findActiveStructure(profiles)));
                return 1;
            }).then(ClientCommandManager.literal("list")
                            .executes(context -> {
                        context.getSource().sendFeedback(Text.literal("current stored profiles are: " + profiles.toString()));
                        return 1;
            })).then(ClientCommandManager.literal("create")
                            .then(ClientCommandManager.argument("profileName", StringArgumentType.word())
                            .executes(context ->{
                String profileName = StringArgumentType.getString(context, "profileName");

                for(StrokesStructure s : profiles){
                    if(s.getProfileName().equalsIgnoreCase(profileName)){
                        context.getSource().sendFeedback(Text.literal("failed to create profile"));
                        return 1;
                    }
                }
                StrokesStructure strokesStructure = new StrokesStructure();
                strokesStructure.setProfileName(profileName);
                strokesStructure.initializeDefaultStrokes();
                profiles.add(strokesStructure);

                context.getSource().sendFeedback(Text.literal("new profile created: " + profileName));
                return 1;
            }))).then(ClientCommandManager.literal("remove")
                    .then(ClientCommandManager.argument("profile", StringArgumentType.word())
                            .executes(context ->{
                                if(profiles.size()==1){
                                    context.getSource().sendFeedback(Text.literal("failed to remove profile: "));
                                } else {
                                    String profile = StringArgumentType.getString(context, "profile");
                                    StrokesStructure s = strokesView.findActiveStructure(profiles);
                                    if(s!=null){
                                        profiles.remove(s);
                                        context.getSource().sendFeedback(Text.literal("profile removed: " + profile));
                                    } else {
                                        context.getSource().sendFeedback(Text.literal("failed to remove profile: "));
                                    }
                                }

                                return 1;
                            })
                    )).then(ClientCommandManager.literal("set").then(ClientCommandManager.argument("profile", StringArgumentType.word())
                            .executes(context ->{
                                String profile = StringArgumentType.getString(context, "profile");
                                StrokesStructure targetStructure = null;
                                for(StrokesStructure s : profiles){
                                    if(s.getProfileName().equalsIgnoreCase(profile)){
                                        targetStructure = s;
                                    }
                                }

                                if(targetStructure!=null){
                                    deactiveLastActive();
                                    targetStructure.setActive();
                                    context.getSource().sendFeedback(Text.literal("profile set: " + profile));
                                } else {
                                    context.getSource().sendFeedback(Text.literal("failed to set profile"));
                                }
                                return 1;
                            }))).then(ClientCommandManager.literal("help").executes(context ->{
                                context.getSource().sendFeedback(Text.literal("Help command options: \n list \n create \n remove \n set \n lettering \n settings"));
                                return 1;
                    }).then(ClientCommandManager.literal("list").executes(context ->{
                                context.getSource().sendFeedback(Text.literal("/keystrokes list \n shows list of all profiles"));
                                return 1;
                                    })
                            ).then(ClientCommandManager.literal("create").executes(context -> {
                                context.getSource().sendFeedback(Text.literal("/keystrokes create <yourProfileName> \n creates a new profile \n can't be a duplicate name"));
                                return 1;
                                    })
                            ).then(ClientCommandManager.literal("remove").executes(context -> {
                                        context.getSource().sendFeedback(Text.literal("/keystrokes remove <profileToRemove> \n removes an existing profile \n can't remove last profile"));
                                        return 1;
                                    })
                            ).then(ClientCommandManager.literal("set").executes(context -> {
                                        context.getSource().sendFeedback(Text.literal("/keystrokes set <profileName> \n sets a profile as active"));
                                        return 1;
                                    })
                            ).then(ClientCommandManager.literal("lettering").executes(context -> {
                                        context.getSource().sendFeedback(Text.literal("/keystrokes lettering \n changes the default Keystroke lettering to an alternate one \n is not being saved in saved profiles"));
                                        return 1;
                                    })
                            ).then(ClientCommandManager.literal("settings").executes(context -> {
                                        context.getSource().sendFeedback(Text.literal("Yet Another Keystrokes Mod Settings \n Default keybind to open keystrokes menu is R\n pressing the button a Keystroke opens its menu \n Left mouse click moves keystrokes or creates a selection area \n Middle mouse button creates or deletes a keystroke \n Right click resizes a keystroke"));
                                        return 1;
                                    })
                            )

                    ).then(ClientCommandManager.literal("lettering").executes(context -> {
                        strokesView.findActiveStructure(profiles).letteringOption();
                        context.getSource().sendFeedback(Text.literal("Default lettering switched"));
                        return 1;
                    }))

            ); // end of Profiles Command
        }); // end of event handler
    }
    public void deactiveLastActive(){
        for(StrokesStructure s : profiles){
            if(s.getActive()){
                s.setInactive();
            }
        }
    }
}
