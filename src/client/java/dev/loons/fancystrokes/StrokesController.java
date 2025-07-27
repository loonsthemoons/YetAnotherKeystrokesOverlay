package dev.loons.fancystrokes;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the control logic for YAKO, handling key presses
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
    private boolean disabledStatus=false;
    private StrokesStatistics profileStatistics;
    private final Map<Strokes.InputType, Boolean> previousKeyState = new HashMap<>();

    /**
     * Constructs a new StrokesController.
     *
     * @param strokesView The view component responsible for rendering strokes.
     * @param structure The data structure holding all defined strokes.
     * @param menuScreen The options screen for the FancyStrokes mod.
     */
    public StrokesController(StrokesView strokesView, StrokesStructure structure, StrokeOptions menuScreen, ArrayList<StrokesStructure> profiles, KeyBinding keyBinding, KeyBinding disableKeystrokes){
        this.strokesView = strokesView;
        this.structure = structure;
        structure.setActive();
        this.profiles = profiles;
        this.menuScreen = menuScreen;
        this.keyBinding = keyBinding;
        this.disableKeystrokes = disableKeystrokes;
        this.profileStatistics = new StrokesStatistics(new HashMap<>(), 0L);
        for (StrokesStructure s : profiles){
            s.setProfileStatistics(profileStatistics);
        }
        for (Strokes.InputType type : Strokes.InputType.values()) {
            previousKeyState.put(type, false);
        }
        // this.profileStatistics = new StrokesStatistics(loadedKeyPressCounts, loadedTotalPresses);
        structure.setControlKey(keyBinding);
        buildController();

    }

    /**
     * Sets up the client tick event listener to update stroke states
     * based on player input and to handle the custom keybinding for
     * opening the YAKO options menu.
     */
    private void buildController(){
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            assert minecraftClient.player != null;
            StrokesStructure activeStructure = strokesView.findActiveStructure(profiles);
            ArrayList<Strokes> strokesToRender = activeStructure.getStrokes();

            for(Strokes strokes : strokesToRender){
                Strokes.InputType currentInputType = strokes.getInputType();
                boolean isCurrentlyPressed = false;

                switch(strokes.getInputType()){
                    case FORWARD -> isCurrentlyPressed = minecraftClient.options.forwardKey.isPressed();
                    case LEFT -> isCurrentlyPressed = minecraftClient.options.leftKey.isPressed();
                    case BACK -> isCurrentlyPressed = minecraftClient.options.backKey.isPressed();
                    case RIGHT -> isCurrentlyPressed = minecraftClient.options.rightKey.isPressed();
                    case ATTACK -> isCurrentlyPressed = minecraftClient.options.attackKey.isPressed();
                    case USE -> isCurrentlyPressed = minecraftClient.options.useKey.isPressed();
                    case SNEAK -> isCurrentlyPressed = minecraftClient.options.sneakKey.isPressed();
                    case SPRINT -> isCurrentlyPressed = minecraftClient.options.sprintKey.isPressed();
                    case JUMP -> isCurrentlyPressed = minecraftClient.options.jumpKey.isPressed();
                }
                boolean wasPreviouslyPressed = previousKeyState.getOrDefault(currentInputType, false);
                strokes.update(isCurrentlyPressed);
                if (isCurrentlyPressed && !wasPreviouslyPressed) {
                    if (activeStructure.getProfileStatistics() != null) {
                        activeStructure.getProfileStatistics().increasePressCount(currentInputType);
                    }
                }
                previousKeyState.put(currentInputType, isCurrentlyPressed);


            }
            while (keyBinding.wasPressed()) {
                if(structure.getKeystrokesStatus())
                {menuScreen.openScreen();}
            }

            while (disableKeystrokes.wasPressed()){
                disabledStatus=!disabledStatus;
                for(StrokesStructure s : profiles){
                    s.disableKeystrokes();
                    if(disabledStatus){
                        s.setPreviousSoundState(s.getKeypressSound());
                        s.keypressSound(false);
                    } else {
                        s.keypressSound(s.getPreviousSoundState());
                    }

                }
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("yako").executes(context -> {
                context.getSource().sendFeedback(Text.literal("currently active profile: " + strokesView.findActiveStructure(profiles) + "\ncurrently active sound profile: " + strokesView.findActiveStructure(profiles).getSoundProfile()));
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
                strokesStructure.newDefaultStrokes();
                profiles.add(strokesStructure);
                strokesStructure.setDidPopupShow(strokesView.findActiveStructure(profiles).isDidPopupShow());

                context.getSource().sendFeedback(Text.literal("new profile created: " + profileName));
                return 1;
            }))).then(ClientCommandManager.literal("remove")
                    .then(ClientCommandManager.argument("profile", StringArgumentType.word())
                            .executes(context ->{
                                if(profiles.size()==1){
                                    context.getSource().sendFeedback(Text.literal("failed to remove profile, due to only one profile existing"));
                                } else {
                                    String profile = StringArgumentType.getString(context, "profile");
                                    StrokesStructure s = strokesView.findActiveStructure(profiles);
                                    if (profile.equalsIgnoreCase(s.getProfileName())) {
                                        context.getSource().sendFeedback(Text.literal("Failed: Cannot remove the active profile."));
                                        return 1;
                                    }

                                    boolean removed = profiles.removeIf(structure1 -> structure1.getProfileName().equalsIgnoreCase(profile) && !structure1.getProfileName().equalsIgnoreCase(s.getProfileName()));
                                    if (removed) {
                                        context.getSource().sendFeedback(Text.literal("profile removed: " + profile));
                                    } else {
                                        context.getSource().sendFeedback(Text.literal("did not find a profile called " + profile));
                                    }
                                    return 1;
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
                                context.getSource().sendFeedback(Text.literal("Help command options: \n list \n create \n remove \n set \n lettering \n settings \n sounds \n volume \n rename \n reset"));
                                return 1;
                    }).then(ClientCommandManager.literal("list").executes(context ->{
                                context.getSource().sendFeedback(Text.literal("/yako list \n shows list of all profiles"));
                                return 1;
                                    })
                            ).then(ClientCommandManager.literal("create").executes(context -> {
                                context.getSource().sendFeedback(Text.literal("/yako create <yourProfileName> \n creates a new profile \n can't be a duplicate name"));
                                return 1;
                                    })
                            ).then(ClientCommandManager.literal("remove").executes(context -> {
                                        context.getSource().sendFeedback(Text.literal("/yako remove <profileToRemove> \n removes an existing profile \n can't remove last profile"));
                                        return 1;
                                    })
                            ).then(ClientCommandManager.literal("set").executes(context -> {
                                        context.getSource().sendFeedback(Text.literal("/yako set <profileName> \n sets a profile as active"));
                                        return 1;
                                    })
                            ).then(ClientCommandManager.literal("lettering").executes(context -> {
                                        context.getSource().sendFeedback(Text.literal("/yako lettering \n changes the default Keystroke lettering to an alternate one"));
                                        return 1;
                                    })
                            ).then(ClientCommandManager.literal("settings").executes(context -> {
                                        context.getSource().sendFeedback(Text.literal("Yet Another Keystrokes Overlay Settings \n Default keybind to open keystrokes menu is R\n pressing the button a Keystroke opens its menu \n Left mouse click moves keystrokes or creates a selection area \n Middle mouse button creates or deletes a keystroke \n Right click resizes a keystroke"));
                                        return 1;
                                    })
                            ).then(ClientCommandManager.literal("sounds").executes(context -> {
                                        context.getSource().sendFeedback(Text.literal("/yako sounds \n activates or deactivates Keystrokes sound effects on key press \n can put 'linear', 'tactile' or 'clicky' after to change the profile \n Default sound profile is linear"));
                                        return 1;
                                    })
                            ).then(ClientCommandManager.literal("volume").executes(context -> {
                                        context.getSource().sendFeedback(Text.literal("/yako volume <volume> \n changes the volume of the current profile \n volume can range from 0.01 to 100"));
                                        return 1;
                                    })
                            ).then(ClientCommandManager.literal("rename").executes(context -> {
                                        context.getSource().sendFeedback(Text.literal("/yako rename <profile> <name> \n changes the name of a profile"));
                                        return 1;
                                    })
                            ).then(ClientCommandManager.literal("reset").executes(context -> {
                                        context.getSource().sendFeedback(Text.literal("/yako reset \n resets the entire config"));
                                        return 1;
                                    })
                            )

                    ).then(ClientCommandManager.literal("lettering").executes(context -> {
                        strokesView.findActiveStructure(profiles).letteringOption(!strokesView.findActiveStructure(profiles).getLetteringOption());
                        context.getSource().sendFeedback(Text.literal("Default lettering switched"));
                        return 1;
                    })).then(ClientCommandManager.literal("sounds").executes(context -> {
                        //strokesView.findActiveStructure(profiles).setKeypressSound();
                        strokesView.findActiveStructure(profiles).keypressSound(!strokesView.findActiveStructure(profiles).getKeypressSound());
                        if(strokesView.findActiveStructure(profiles).getKeypressSound()){
                            context.getSource().sendFeedback(Text.literal("Keystroke sounds activated"));
                            return 1;
                        } else {
                            context.getSource().sendFeedback(Text.literal("Keystroke sounds deactivated"));
                            return 1;
                        }
                    }).then(ClientCommandManager.literal("linear").executes(context -> {
                        strokesView.findActiveStructure(profiles).soundProfile("linear");
                        context.getSource().sendFeedback(Text.literal("Keystroke sounds set to linear"));
                        return 1;
                            })
                    ).then(ClientCommandManager.literal("tactile").executes(context -> {
                        strokesView.findActiveStructure(profiles).soundProfile("tactile");
                        context.getSource().sendFeedback(Text.literal("Keystroke sounds set to tactile"));
                        return 1;
                    })).then(ClientCommandManager.literal("clicky").executes(context -> {
                                strokesView.findActiveStructure(profiles).soundProfile("clicky");
                                context.getSource().sendFeedback(Text.literal("Keystroke sounds set to clicky"));
                                return 1;
                            }))).then(ClientCommandManager.literal("volume").executes(context ->{
                                context.getSource().sendFeedback(Text.literal("Current volume is: " + strokesView.findActiveStructure(profiles).getVolume()));
                                return 1;
                            })
                                  .then(ClientCommandManager.argument("volume", FloatArgumentType.floatArg()).executes(context ->{
                                float volume = FloatArgumentType.getFloat(context, "volume");
                                if(volume>0.0f && volume<=100.0f){
                                    strokesView.findActiveStructure(profiles).volume(volume);
                                    context.getSource().sendFeedback(Text.literal("Volume for this profile now is: " + volume));
                                    return 1;
                                } else {
                                    context.getSource().sendFeedback(Text.literal("Failed to set volume"));
                                    return 1;
                                }
                    }))).then(ClientCommandManager.literal("rename").then(ClientCommandManager.argument("profile", StringArgumentType.word()).then(ClientCommandManager.argument("name", StringArgumentType.word()).executes(context -> {
                        String profile = StringArgumentType.getString(context, "profile");
                        String name = StringArgumentType.getString(context, "name");
                         boolean nameExists = profiles.stream().anyMatch(s -> s.getProfileName().equalsIgnoreCase(name));

                         if (nameExists) {
                            context.getSource().sendFeedback(Text.literal("failed to set name, as it already exists"));
                            return 1;
                         }

                        for(StrokesStructure s : profiles){
                            if(s.getProfileName().equalsIgnoreCase(profile)){
                                s.setProfileName(name);
                                context.getSource().sendFeedback(Text.literal("Profile: " + s.getProfileName() + " was renamed to " + name));
                                return 1;
                            }
                        }
                         context.getSource().sendFeedback(Text.literal("Failed to find profile"));
                        return 1;
                    })))).then(ClientCommandManager.literal("reset").executes(context -> {
                        YetAnotherKeystrokesModClient.resetAllProfiles();
                        context.getSource().sendFeedback(Text.literal("Successfully resetted the config"));
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
