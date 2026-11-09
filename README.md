# AeroIndustrial

AeroIndustrial is an original Minecraft 1.21.1 NeoForge technology layer built around **AeroFlux**: a separate engineering resource intended to connect industrial infrastructure, mechanical systems and aeronautical structures without copying GregTech or Create: Aeronautics machines.

## Target stack

- Minecraft 1.21.1
- NeoForge 21.1.x (development build: 21.1.250)
- Java 21
- Create 6.0.10 (optional integration)
- Sable 2.0.x (optional integration)
- Create Aeronautics 1.3.x (optional integration)
- GregTech CEu Modern 7.x (optional integration target)

Create Aeronautics itself requires Create and Sable, so the full aeronautical stack is installed together.

## Current implementation

- 15 registered AeroIndustrial machine blocks, each with a distinct tier role.
- 8 custom AeroFlux components.
- Crafting recipes, data-driven machine processing recipes and block loot tables.
- Custom industrial textures for all machines and components.
- Custom AeroFlux storage and overload/resonance state.
- NeoForge energy capability exposure.
- Runtime detection of Create, Sable, Create Aeronautics and GTCEu.
- Optional GTCEu energy intake through NeoForge's energy capability when GTCEu exposes an FE-compatible storage.
- Vector Thruster applies impulses to the Sable sub-level containing the machine; outside a sub-level, its conservative fallback targets only the nearest compatible moving-structure entity.
- Optional Create kinetic harvesting through a reflection bridge.
- Optional moving-structure coupling for the Vector Thruster through a conservative reflection/class-name bridge.
- Localized machine status reporting in the action bar.
- A native optional JEI category for AeroFlux machine processing recipes.
- GitHub Actions build pipeline.
- Short-lived per-tier recipe caching to reduce repeated RecipeManager scans in machine arrays.
- Cached per-class reflection lookups for optional Create and Sable bridges.
- Defensive recipe-output validation so malformed external data cannot create empty or oversized machine outputs.
- Redstone pause control, overload-aware thruster behavior, active Sky Relay upkeep, and localized runtime telemetry for integration inputs.

The optional integrations are deliberately not hard compile-time dependencies. They are declared as optional NeoForge dependencies, so the base mod remains loadable without them.

## Important integration status

The Create bridge is a compatibility prototype: it looks for compatible kinetic block entities and reads speed methods reflectively. It does **not** claim to implement Create's internal kinetic API.

The Aeronautics/Sable bridge remains a compatibility prototype. When the thruster is mounted inside a Sable sub-level, it resolves Sable's public sub-level/physics methods once and applies the impulse to that structure. Outside a sub-level, the fallback searches within a configurable radius and affects only the nearest class-name-matched moving-structure entity. This still requires runtime verification against the intended Create Aeronautics 1.3.x and Sable 2.0.x releases.

GregTech CEu Modern is detected and has a conservative compatibility bridge: the Torque Resonator can pull energy from nearby GTCEu blocks when they expose a compatible NeoForge energy capability. This is intentionally isolated from GTCEu internals and does not pretend that AeroFlux and EU are the same unit.

## Gameplay prototype

AeroIndustrial's core loop is:

1. Supply AeroFlux to a machine.
2. The machine builds resonance progress.
3. High storage creates overload pressure, while stabilizers and local network blocks manage that pressure.
4. Create kinetic infrastructure and GTCEu-compatible storage can optionally contribute AeroFlux through the Torque Resonator.
5. The Vector Thruster can optionally couple AeroFlux to compatible moving structures; overload reduces its effective thrust.

This is now a functional vertical slice of the intended product architecture: data-driven machines, a custom GUI, recipe discovery, localized status feedback, original AeroFlux mechanics and optional integrations. The external Create/Aeronautics/GTCEu bridges remain conservative compatibility layers and still require broader runtime validation.

## Dependencies

For the **base AeroIndustrial mod**, only Minecraft 1.21.1 and NeoForge 21.1.x are required.

For the intended **full aeronautical setup**, use:

- Minecraft 1.21.1
- NeoForge 21.1.x
- Create 6.0.10
- Sable 2.0.x
- Create Aeronautics 1.3.x
- AeroIndustrial

GTCEu is optional for AeroIndustrial and is not required by the current implementation.

## Build

Run `./gradlew build` with Java 21. GitHub Actions builds the project automatically on pushes to `main`.

## Honest status

Version 1.1.0 is a **functional development release**, not a claim of full compatibility with every external mod build. The repository includes a GitHub Actions build workflow; each new revision still requires a successful workflow run before it should be treated as a verified build. Core AeroIndustrial systems are implemented directly; Create, Sable, Create Aeronautics and GTCEu integrations are intentionally isolated so that the base mod remains usable without them.


## AeroFlux API

AeroIndustrial exposes a dedicated NeoForge block capability for its internal energy system:

- capability: `aeroindustrial:aeroflux`
- interface: `IAeroFluxStorage`
- supports receive/extract operations and capacity queries
- the ordinary NeoForge energy capability remains available as a compatibility adapter

This separation is intentional: **AeroFlux is not declared to be EU or FE**. Compatibility layers may convert external energy into AeroFlux, but the native API remains independent.


## Configuration

A server configuration is registered automatically under the NeoForge server config system. It can tune:

- Create kinetic input rate
- GTCEu-compatible energy input rate
- Vector Thruster AeroFlux cost
- Vector Thruster impulse strength
- Vector Thruster fallback search radius
- Sky Relay stabilization upkeep
- Atmospheric Condenser open-sky generation
- Inertial Accumulator pull packet size
- AeroFlux Capacitor transfer packet and reserve percentage
- Mobile Dock transfer packet size
- Aeronautical Conduit transfer packet size

The defaults are intentionally conservative so the external integrations do not dominate the native AeroIndustrial progression.
