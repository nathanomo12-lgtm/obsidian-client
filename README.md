# Obsidian Client

A modern, performance-focused client-side utility mod for Minecraft Java Edition, built on Fabric.

Targets Minecraft **1.21.11**, built against `officialMojangMappings()` (no Yarn, no Fabric
Loader `MappingResolver`). This repository contains the mod's foundation: project scaffolding,
the module system architecture, compliance tiers, profiles, a per-module settings registry,
config persistence, and keybind wiring, plus a growing set of HUD/QoL feature modules and the
ClickGUI.

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
    `onRender(GuiGraphics, float)` / `onKeyInput(int, int, boolean)` lifecycle. Each module owns
    a `KeyMapping` for toggling and declares a `ComplianceTier`.
  - `ModuleCategory` — `COMBAT`, `MOVEMENT`, `RENDER`, `UTILITY`, `PLAYER`, `PERFORMANCE` (matches
    the ClickGUI category tabs).
  - `ComplianceTier` — `SAFE` or `SERVER_DEPENDENT`. Every module must declare one explicitly;
    `ModuleManager#register` refuses (fail-closed) any module that doesn't.
  - `Profile` — `PVP` / `SURVIVAL` / `CUSTOM` / `COMPETITIVE`, each with its own persisted module
    loadout. Switching to `COMPETITIVE` force-disables every `SERVER_DEPENDENT` module regardless
    of its saved toggle.
  - `com.obsidian.client.module.setting.Setting` (+ `BooleanSetting`/`IntSetting`/`ColorSetting`/
    `EnumSetting`) — per-module configurable values surfaced in the ClickGUI's settings pane via
    `Module#addSetting`.
  - `ModuleManager` — registry + dispatcher. Only enabled modules are ticked/rendered, so
    disabled modules cost zero time per frame/tick.
- **Config** (`com.obsidian.client.config`): JSON persistence (Gson) to
  `.minecraft/config/obsidian/obsidian-client.json`, storing module enabled-state and keybind
  **per profile**, plus which profile was last active.
- **Version compatibility** (`com.obsidian.client.util.VersionCompat`): logs a warning (but does
  not block loading) if the running Minecraft version falls outside the tested 1.21.4–1.21.11
  range.

## Adding a new module
1. Create a class in `com.obsidian.client.module.impl` extending `Module`.
2. Call the `Module` constructor with a name, description, `ModuleCategory`, a `ComplianceTier`
   (required — pick `SAFE` unless the feature could be restricted by some servers' rules), and a
   default keybind (`GLFW.GLFW_KEY_UNKNOWN` for unbound).
3. Override the lifecycle methods you need (`onEnable`, `onDisable`, `onTick`, `onRender`).
4. Optionally call `addSetting(...)` in field initializers to expose configurable values.
5. Register an instance in `ObsidianClientModClient#registerModules()`.
6. Add a translation for the keybind in `src/main/resources/assets/obsidian-client/lang/en_us.json`
   using the key `key.obsidian-client.<name>`.

See `com.obsidian.client.module.impl.ExampleModule` for a minimal reference implementation.

## ClickGUI
Press **Right Shift** (default, remappable in Options > Controls > Obsidian Client) to open the
menu (`com.obsidian.client.gui.ClickGuiScreen`). It has category tabs (Combat, Movement, Render,
Utility, Player, Performance), a search box to filter modules by name, click-to-toggle rows, and a
fade-in animation. `Theme`/`Accent` buttons in the header switch dark/light mode and cycle an
accent color (`com.obsidian.client.gui.theme.Theme`); a **Profile** button in the footer cycles
`PVP` / `SURVIVAL` / `CUSTOM` / `COMPETITIVE`. The world keeps ticking while the menu is open.

Module rows with configurable settings show a **⚙** button that opens a settings pane for that
module (a "< back" row returns to the list). Every setting type is click-to-cycle rather than a
drag/slider control, matching the screen's stock-widgets-only design (see `ClickGuiScreen`'s class
doc): booleans toggle, enums advance to the next constant, colors step through a curated palette
(`ColorSetting#cyclePreset`), and ints step by ~1/10th of their range and wrap at the max.

## Current module list
- **Example** (Utility, SAFE) — reference implementation demonstrating the lifecycle.
- **Keystrokes** (Utility, SAFE) — WASD + mouse button press indicators, bottom-right.
- **CPS Counter** (Combat, SAFE) — left/right click rate, centered below the crosshair.
- **Combo Counter** (Combat, SAFE) — consecutive-hit streak, resets after 3s idle. Fed by the
  `AttackEntityCallback` hook in `ObsidianClientModClient`, which only ever returns `PASS` — it
  observes landed hits and never influences hit registration, cooldown, or damage.
- **FPS Display** (Utility, SAFE) — current frames per second, top-left.
- **Ping Display** (Utility, SAFE) — current server latency, top-left.
- **Toggle Sprint** / **Toggle Sneak** (Movement, SAFE) — hold-latch the vanilla sprint/sneak keys.
  Because `onTick` re-asserts the held key every tick for as long as the module stays enabled,
  this already survives death/respawn with no special-casing (there's no separate "sprint after
  death" module).
- **Coordinates** (Utility, SAFE) — block position and coarse compass facing, top-left.
- **Armor Status** (Player, SAFE) — equipped armor durability percentages, bottom-left.
- **Potion Effects** (Utility, SAFE) — active effect list with remaining duration.
- **Memory Usage** (Utility, SAFE) — JVM heap usage, top-right.
- **Clock** (Utility, SAFE) — real-world time, top-right.
- **Zoom** (Render, SERVER_DEPENDENT, default key `C`) — optical FOV zoom via the vanilla FOV
  option (settings: Zoom FOV); force-disabled under the Competitive profile since some servers
  restrict FOV changes.
- **Crosshair** (Render, SAFE) — replaces the vanilla crosshair with a custom shape/color/size
  (settings: Shape — Cross/Dot/Circle/T, Color, Size). Implemented by wrapping the vanilla
  crosshair via `HudElementRegistry#replaceElement`, so disabling it instantly restores stock
  rendering with no extra state to track.
- **Health Display** (Player, SAFE) — numeric current/max health, color-coded at low health.
- **Hunger Display** (Player, SAFE) — numeric food level and saturation.
- **XP Tracker** (Utility, SAFE) — experience level and percent progress to the next level.
- **Fall Distance** (Utility, SAFE) — current fall distance while airborne, informational only
  (does not suppress or reduce fall damage).
- **Light Level** (Utility, SAFE) — block light level at your feet, color-coded as a rough
  mob-spawn safety hint (8+ is generally safe).
- **Biome Display** (Utility, SAFE) — name of the biome you're currently standing in.
- **Day Time Tracker** (Utility, SAFE) — in-game day count, day/night status, and time until the
  next transition; pairs with Light Level to anticipate hostile mob spawn windows.
- **Speed Display** (Utility, SAFE) — horizontal movement speed in blocks/second.
- **Session Timer** (Utility, SAFE) — elapsed playtime since the module was enabled (`h:mm:ss`).
- **Death Counter** (Utility, SAFE) — counts death-screen appearances since enabled; purely
  observational, never affects respawn/health/damage.
- **Distance Traveled** (Utility, SAFE) — cumulative blocks moved since enabled.
- **Target Block Info** (Utility, SAFE) — name, position, and face of the block you're looking
  at, reusing vanilla's own `Minecraft#hitResult` rather than a new raycast.
- **Held Item Durability** (Player, SAFE) — remaining durability of the main-hand item, as a
  warning note (not automation) to swap gear before it breaks.
- **Movement Status** (Movement, SAFE) — compact readout of sprinting/sneaking/swimming/gliding.
- **Auto Sneak While Mining** (Movement, SAFE) — holds sneak automatically while breaking a
  block and releases it the instant mining stops, an edge-safety QoL aid rather than general
  input automation.
- **Attack Cooldown** (Combat, SAFE) — bar above the crosshair driven by vanilla's own
  `Player#getAttackStrengthScale`; a rendering aid only, it never changes cooldown timing,
  damage, or hit registration.
- **Hit Flash** (Combat, SAFE) — brief accent-colored screen-edge flash when your own attack
  lands, fed by the same `AttackEntityCallback` hook as Combo Counter (always returns `PASS`).
- **Hit Sound Cue** (Combat, SAFE) — subtle client-local sound cue on landing your own attack,
  played via `ClientLevel#playLocalSound` (never broadcast, never gameplay-affecting).
- **Low Health Warning** (Render, SAFE, setting: Threshold) — pulsing red screen-edge tint while
  health stays at or below a configurable threshold; distinct from vanilla's one-shot damage
  flash since it persists for as long as health remains low.
- **Compass Bar** (Utility, SAFE) — graphical heading strip across the top of the screen showing
  nearby cardinal/intercardinal directions, using the same yaw convention as Coordinates.
- **FPS Min Max** (Performance, SAFE) — rolling one-second min/max FPS, a cheap "1% low"-style
  companion to FPS Display for spotting stutters an average would hide.

All HUD modules currently use fixed default positions (no drag-and-drop repositioning yet); a
HUD edit/reposition overlay is a natural next step.

## Deferred features (require mixins; not yet implemented)
Every module above is built entirely on public Fabric API events and accessors (tick/HUD/lifecycle
events, `HudElementRegistry`, `AttackEntityCallback`) — see `Module#onKeyInput`'s doc comment for
why the architecture deliberately avoids mixins in hot paths. A few originally-scoped QoL features
can't be built that way and would need real Mixins into rendering/entity code, which is a larger,
separate undertaking (correct target selection, refmap generation, and — critically — they can only
be verified by actually running the client, which wasn't exercised in this pass):
- **Freelook** — needs the camera decoupled from the player's body rotation (a `GameRenderer`/
  `Camera` mixin).
- **Motion Blur** — real per-pixel motion blur needs a custom post-processing shader/`PostChain`
  pass; there's no legitimate way to approximate it with HUD draw calls alone.
- **Nametag scale/background opacity** — nameplates are drawn inside `EntityRenderer`, not exposed
  through `HudElementRegistry`.
- **Item physics (dropped items lie flat)** — needs a mixin into `ItemEntity`/`ItemRenderer`.
- **3D skin layers / cosmetic cape rendering** — needs a custom `RenderLayer` mixed into the player
  model, plus (for capes) texture assets this repo doesn't ship.
- **Scoreboard sidebar reposition/opacity** — `HudElementRegistry#replaceElement` can only swap the
  whole element wholesale, not tweak how vanilla draws its own scoreboard content; doing this
  properly needs either a mixin or a full reimplementation of vanilla's scoreboard renderer.
- **Chat timestamps/extended history** — Fabric API's chat hooks (`ClientReceiveMessageEvents`)
  only cover *system* messages (`MODIFY_GAME`); modifying signed player chat before render isn't
  exposed without a mixin into `ChatComponent`.

Two other originally-scoped items turned out to be unnecessary, not deferred:
- **Shulker box tooltip preview** is already built into vanilla 1.21.11 (`ItemContainerContents`
  implements `TooltipProvider` and renders its own preview), so no mod-side module is needed.
- **"Sprint after death"** is already covered by **Toggle Sprint** as described above.

## Scope note
This project intentionally excludes PvP "hack" style modules (ESP, tracers, fullbright, reach
display, auto-block, criticals automation, knockback modifiers, hitbox expansion, fast place,
NoFall, sprint-reset bypass, trajectory/aim-assist). These alter game mechanics or reveal hidden
information to gain an unfair advantage over other players and are against the rules of virtually
every Minecraft server.

## License
MIT — see [LICENSE](LICENSE).
