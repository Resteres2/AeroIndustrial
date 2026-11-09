# AeroIndustrial integration and regression test matrix

This document is a **manual QA plan**, not a report of tests already passed. Run it against the exact Minecraft, NeoForge, and optional-mod versions listed in the README. Record the game log, server log, installed mod versions, and outcome for each case.

## 1. Base installation

| Case | Setup and action | Expected result |
|---|---|---|
| B-01 | Launch Minecraft 1.21.1 with NeoForge and AeroIndustrial only. | The game reaches the title screen and creates/loads a world without optional-mod class-loading errors. |
| B-02 | Open the AeroFlux creative tab. | All 15 machines, 8 components, the manual, and the wrench appear with valid models and textures. |
| B-03 | Place each machine, break it with the required tool, and pick it up. | Each block places, faces the player as intended, drops itself, and does not leave a block entity behind. |
| B-04 | Open and close a machine GUI; test quick-move, input restrictions, output extraction, and player inventory. | Only valid recipe inputs enter the input slot; output cannot be inserted; quick-move does not delete or duplicate stacks. |
| B-05 | Charge a machine with an AeroFlux Crystal in survival and creative. | Up to 2500 AF is accepted, overflow remains unaccepted, and creative mode does not consume the crystal. |
| B-06 | Apply and remove a redstone signal during processing. | Processing pauses without consuming input/energy or resetting accumulated progress; it resumes after the signal is removed. |
| B-07 | Save, exit, and reload a world with partially charged and processing machines. | Stored AeroFlux, overload, progress, and inventories persist; transient status telemetry resets safely. |

## 2. Processing and recipe data

| Case | Setup and action | Expected result |
|---|---|---|
| R-01 | Test every AeroMachine recipe at its declared machine tier. | Correct input, output, processing time, and energy cost are used. |
| R-02 | Attempt to insert an item not accepted by the machine's current recipe data. | The input handler rejects it without changing the stack. |
| R-03 | Fill the output slot with a different item or insufficient free capacity. | The machine pauses without consuming input or energy. |
| R-04 | Load a datapack recipe with an empty, oversized, or mismatched assembled output. | The machine refuses the malformed result and preserves input and energy. |
| R-05 | Reload datapacks while machines are loaded. | Recipe caches refresh and updated recipes become available without restarting the world. |
| R-06 | Inspect custom machine recipes in JEI with the optional client installed. | Every recipe shows its tier, input, output, processing time, and AeroFlux cost. |

## 3. AeroFlux network roles

| Case | Setup and action | Expected result |
|---|---|---|
| N-01 | Fill a machine close to capacity and observe overload. | Overload rises only under the configured high-charge condition and never exceeds 100%. |
| N-02 | Place a Flux Lattice, Pressure Lattice, Sky Relay, and Vector Frame in their intended positions. | Each stabilizes only according to its own role and consumes its documented upkeep. |
| N-03 | Feed an Inertial Accumulator from an adjacent AeroFlux storage above 80% while it is below 40%. | It pulls no more than the configured packet, source contents, or its own free capacity. |
| N-04 | Place an AeroFlux Capacitor next to a low-charge AeroIndustrial machine. | It feeds the machine by no more than the configured packet and retains its configured reserve. |
| N-05 | Repeat N-04 using a separate block entity that exposes the aeroindustrial:aeroflux capability. | The capacitor uses the public capability and does not require the target to be an AeroIndustrial block entity. |
| N-06 | Place a Mobile Dock next to a source, then next to a low-charge destination. | It draws below one-third charge and unloads above 75%, respecting the configured packet size. |
| N-07 | Connect two Aeronautical Conduits face-to-face, then reverse one. | Transfer occurs only through the output-facing side; reversing the direction reverses the flow. |
| N-08 | Place two conduits facing each other and observe them over several pulses. | The network does not create energy or repeatedly bounce the same packet without a source. |
| N-09 | Change each network setting in the server config and restart/reload as required. | Each machine uses the new setting and remains within its documented bounds. |

## 4. Optional integrations

Run each test both with the optional mod absent and present. The base mod must remain loadable when an integration is absent.

| Case | Setup and action | Expected result |
|---|---|---|
| I-01 | Run without Create, Sable, Aeronautics, and GTCEu. | Base machines, recipes, GUI, and native AeroFlux API work; optional bridges remain inactive. |
| I-02 | Install Create 6.0.10 and place a Torque Resonator near rotating Create machinery. | The resonator harvests bounded kinetic input and never extracts more than its free capacity. |
| I-03 | Stop the Create shaft and then rotate it again. | Input falls to zero while stopped and resumes when speed returns. |
| I-04 | Install GTCEu Modern 7.x and place the resonator near an energy output that exposes NeoForge FE. | At most one side is extracted per GTCEu machine per pulse; no multi-side double counting occurs. |
| I-05 | Install Sable 2.0.x and assemble a moving structure with a Vector Thruster mounted inside it. | The thruster applies its impulse to the containing Sable sub-level, in the block's facing direction. |
| I-06 | Install Create Aeronautics and Sable; test a thruster outside a Sable sub-level near multiple compatible moving entities. | The fallback affects only the nearest matching entity inside the configured radius. |
| I-07 | Rotate the Vector Thruster and repeat I-05/I-06. | The impulse follows the block's facing direction. |
| I-08 | Change thruster strength, pulse cost, and fallback radius. | The thruster uses the configured values without affecting base-mod loading. |

## 5. Stability and performance

| Case | Setup and action | Expected result |
|---|---|---|
| P-01 | Run a 3×3 cluster of processing machines for several minutes. | No repeated recipe-manager scans beyond the short-lived cache, no item loss, and no unexplained energy changes. |
| P-02 | Run a larger mixed AeroFlux network with capacitors, docks, and conduits. | Transfer remains bounded; no recursive capability calls, crashes, or unbounded per-tick work. |
| P-03 | Remove a machine while its GUI is open and while it contains items. | The GUI closes safely and machine contents drop once. |
| P-04 | Reload datapacks and configuration while machines are loaded. | No stale recipes or invalid config values remain active after the reload takes effect. |
| P-05 | Repeat the same tests on a dedicated server with a remote client. | No client-only class loads on the server and no GUI/menu synchronization errors occur. |

## Test record

For each case, record PASS, FAIL, or NOT RUN, the exact mod versions, reproduction steps, and the relevant latest.log excerpt. Do not mark an integration as compatible based only on the mod being detected in the mod list.
