# Yet Another Keystrokes Overlay (YAKO)

**YAKO** is a design-focused keystrokes overlay for Minecraft 1.20.1 (Fabric).  
It shows your **WASD**, **mouse buttons**, **sneak**, **jump**, and **sprint** inputs — fully customizable to match your layout and visual preferences.

From rounded and transparent strokes to sound profiles and profile switching, YAKO is built with flexibility and user experience in mind.

---

## Features

- **Visual Keystrokes HUD**  
  Shows real-time feedback for WASD, LMB/RMB, Shift, Ctrl, and Space. You can customize the layout, look, and text for each key individually.

- **Customizable Layout**  
  Drag and resize each keystroke freely. Supports transparent strokes, outlines, rounded borders, and custom labels.

- **Interactive Mouse Controls**  
  Intuitive mouse-based interactions let you build or edit your layout without menus (see controls below).

- **Sound Feedback System**  
  Enable keystroke sounds for a tactile typing experience. Choose between `linear`, `tactile`, or `clicky` sound profiles. Volume and sound state can be adjusted per layout profile.

- **Profile System**  
  Save and switch between multiple keystroke layouts using the `/yako` command. Each profile stores strokes, layout, sound settings, and more.

- **Command-Based Customization**  
  Use the `/yako` command to manage layouts, toggle features, switch lettering and adjust sound settings.

---

## Controls

> These controls apply to layout editing mode (opened via the **R** key):

- **Left Click**:
  - On stroke: Select or drag to move
  - On blank space: Drag to select multiple strokes

- **Middle Click**:
  - On stroke: Delete it
  - On blank space: Create a new keystroke

- **Right Click**:
  - Resize a keystroke (drag to scale)

- **R** (default keybind):
  - Opens the YAKO menu
  - If hovering over a stroke, opens its individual menu

- **K** (default keybind):
  - Toggle the entire overlay on/off

---

## Commands (`/yako`)

Use `/yako help` for command hints in-game. Here's what you can do:

### Profile Management
- `/yako list` – List all saved profiles
- `/yako create <name>` – Create a new profile
- `/yako set <name>` – Switch to a specific profile
- `/yako remove <name>` – Delete the current profile (cannot delete the last one)

### Settings
- `/yako settings` – Show all controls and interactions as a quick reference
- `/yako lettering` – Toggle alternate keystroke lettering (not profile-persistent)

### Keystroke Sounds
- `/yako sounds` – Toggle sound on/off for keystrokes
- `/yako sounds <linear|tactile|clicky>` – Set sound profile
- `/yako volume [<value>]` – Show or set current volume (range: 0.01 to 25)

### Help Menu
- `/yako help` – Show available command categories
- `/yako help <command>` – Get usage for specific command

---

## Future Features

- **Gradient & Rainbow Colors**  
  Instead of single colors, give your keystrokes gradient or cycling rainbow colors!

- **Keystroke Animations**  
  Add animations to your Keystrokes, on press, while pressing or on release.

- **Custom Input Types**  
  Instead of being limited to the existing Input types, add your own to track new keys!

- **Scaling**  
  Currently, the Keystroke menu is placed absolute, however this should be based on your Screen resolution, so that nothing gets cut off.

- **Keypress Counter**  
  Ever wonder how many times you pressed a specific button? With this feature you would have an answer!

---

## Early Development Disclaimer

YAKO is still in its early stages, so you may encounter the occasional bug or jank.  
Feedback and suggestions are always welcome!

---

## Dependencies

- Minecraft **1.20.1**
- Fabric Loader **≥ 0.15.11**
- [owo-lib](https://github.com/wisp-forest/owo-lib) **0.11.2+1.20**

---

## YAKO In Action

<img width="550" height="432" alt="image" src="https://github.com/user-attachments/assets/976f1535-83e5-4fac-95f4-b7760686d585" />
<img width="328" height="323" alt="image" src="https://github.com/user-attachments/assets/07a2bd3a-e4d8-4723-8df1-87bcb5dd9984" />

**Settings Menu (outdated)**
<img width="1920" height="1008" alt="image" src="https://github.com/user-attachments/assets/a9e4b520-dcfd-4523-86a3-d4c6ea1ac3a2" />
