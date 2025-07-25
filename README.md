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
- `/yako rename <profile> <name>` – Rename a profile

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
- Fabric Loader **≥ 0.14.0**

---

## YAKO In Action
<img width="573" height="473" alt="yako1" src="https://github.com/user-attachments/assets/08b6f032-8547-4b35-bc4a-c9c3588fc03f" />
<img width="610" height="646" alt="yako2" src="https://github.com/user-attachments/assets/72a3f64c-d8bf-45b1-8aad-7a46469c69e5" />
<img width="730" height="611" alt="yako3" src="https://github.com/user-attachments/assets/40425e3a-6943-40d1-9282-96aaa41a28a9" />
<img width="477" height="592" alt="yako_personal" src="https://github.com/user-attachments/assets/5050498f-ef7c-4f53-9295-7cca4bdb7f96" />

**Settings Menu**
<img width="1908" height="1028" alt="2025-07-25_19 46 53" src="https://github.com/user-attachments/assets/44ba0e7c-6e1a-46b8-a9b0-b3a4876671f7" />
<img width="1908" height="1028" alt="2025-07-25_19 47 02" src="https://github.com/user-attachments/assets/e712b9b3-3212-49a7-8d5d-8be2a443e131" />

