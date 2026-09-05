# Obsidian Client

A modern, performance-focused client-side utility mod for Minecraft Java Edition, built on Fabric.

Targets Minecraft **1.21.4 – 1.21.11**. This repository currently contains the mod's foundation:
project scaffolding, the module system architecture, config persistence, and keybind wiring.
Feature modules (HUD elements, ClickGUI, etc.) are added on top of this base.

## Requirements
- JDK 21 (the Gradle toolchain will look for JDK 21 specifically; JDK 22+ works as the Gradle
  launcher but Loom pins compilation to release 21)
- Gradle Wrapper is included, no local Gradle install needed

## Building
```powershell
.\gradlew.bat build
```
The output mod jar is written to `build/libs/obsidian-client-<version>.jar`.

To run the mod in a development client:
```powershell
.\gradlew.bat runClient
```

> **Note:** `gradle.properties` pins `minecraft_version`, `yarn_mappings`, `loader_version`, and
> `fabric_version`. Because 1.21.11 is a very recent release, double-check these against
> https://fabricmc.net/develop/ if the build stops resolving dependencies.

## Architecture
- **Mod ID:** `obsidian-client`
- **Base package:** `com.obsidian.client`
- **Common entrypoint:** `com.obsidian.client.ObsidianClientMod` (`ModInitializer`) — minimal,
  contains no client-only references.
- **Client entrypoint:** `com.obsidian.client.client.ObsidianClientModClient`
  (`ClientModInitializer`) — registers modules and wires them into Fabric's tick/HUD/lifecycle
  events.
- **Module system** (`com.obsidian.client.module`):
  - `Module` — abstract base class with the `onEnable()` / `onDisable()` / `onTick()` /
    `onRender(DrawContext, float)` lifecycle. Each module owns a Fabric `KeyBinding` for
    toggling.
  - `ModuleCategory` — `COMBAT`, `MOVEMENT`, `RENDER`, `UTILITY`, `PLAYER` (matches the planned
    ClickGUI category tabs).
  - `ModuleManager` — registry + dispatcher. Only enabled modules are ticked/rendered, so
    disabled modules cost zero time per frame/tick.
- **Config** (`com.obsidian.client.config`): JSON persistence (Gson) to
  `.minecraft/config/obsidian/obsidian-client.json`, storing each module's enabled state and
  keybind.
- **Version compatibility** (`com.obsidian.client.util.VersionCompat`): logs a warning (but does
  not block loading) if the running Minecraft version falls outside the tested 1.21.4–1.21.11
  range.

## Adding a new module
1. Create a class in `com.obsidian.client.module.impl` extending `Module`.
2. Call the `Module` constructor with a name, description, `ModuleCategory`, and default keybind
   (`GLFW.GLFW_KEY_UNKNOWN` for unbound).
3. Override the lifecycle methods you need (`onEnable`, `onDisable`, `onTick`, `onRender`).
4. Register an instance in `ObsidianClientModClient#registerModules()`.
5. Add a translation for the keybind in `src/main/resources/assets/obsidian-client/lang/en_us.json`
   using the key `key.obsidian-client.<name>`.

See `com.obsidian.client.module.impl.ExampleModule` for a minimal reference implementation.

## ClickGUI
Press **Right Shift** (default, remappable in Options > Controls > Obsidian Client) to open the
menu (`com.obsidian.client.gui.ClickGuiScreen`). It has category tabs (Combat, Movement, Render,
Utility, Player), a search box to filter modules by name, click-to-toggle rows, and a fade-in
animation. `Theme`/`Accent` buttons in the header switch dark/light mode and cycle an accent
color (`com.obsidian.client.gui.theme.Theme`). The world keeps ticking while the menu is open.

## Current module list
- **Example** (Utility) — reference implementation demonstrating the lifecycle.
- **Keystrokes** (Utility) — WASD + mouse button press indicators, bottom-right.
- **CPS Counter** (Combat) — left/right click rate, centered below the crosshair.
- **FPS Display** (Utility) — current frames per second, top-left.
- **Ping Display** (Utility) — current server latency, top-left.
- **Toggle Sprint** / **Toggle Sneak** (Movement) — hold-latch the vanilla sprint/sneak keys.
- **Coordinates** (Utility) — block position and coarse compass facing, top-left.
- **Armor Status** (Player) — equipped armor durability percentages, bottom-left.

All HUD modules currently use fixed default positions (no drag-and-drop repositioning yet); that
and a persisted settings framework per-module are natural next steps.

## Scope note
This project intentionally excludes PvP "hack" style modules (ESP, tracers, fullbright, reach
display, auto-block, criticals automation, knockback modifiers, hitbox expansion, fast place,
NoFall, sprint-reset bypass, trajectory/aim-assist). These alter game mechanics or reveal hidden
information to gain an unfair advantage over other players and are against the rules of virtually
every Minecraft server.

## License
MIT — see [LICENSE](LICENSE).
