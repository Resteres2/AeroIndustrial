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

- 15 registered blocks: 10 named machines + 5 auxiliary structures.
- 8 custom AeroFlux components.
- Crafting recipes and block loot tables.
- Custom AeroFlux storage and overload/resonance state.
- NeoForge energy capability exposure.
- Runtime detection of Create, Sable, Create Aeronautics and GTCEu.
- Optional Create kinetic harvesting through a reflection bridge.
- Optional moving-structure coupling for the Vector Thruster through a conservative reflection/class-name bridge.
- Status reporting in the machine action bar.
- GitHub Actions build pipeline.

The optional integrations are deliberately not hard compile-time dependencies. They are declared as optional NeoForge dependencies, so the base mod remains loadable without them.

## Important integration status

The Create bridge is a compatibility prototype: it looks for compatible kinetic block entities and reads speed methods reflectively. It does **not** claim to implement Create's internal kinetic API.

The Aeronautics/Sable bridge is also a prototype: it identifies moving-structure entities conservatively and applies the Vector Thruster impulse to matching entities. It has not yet been verified against every Create Aeronautics 1.3.x internal class.

GregTech CEu Modern is currently detected but does **not** have a real EU-to-AeroFlux conversion API bridge yet.

## Gameplay prototype

AeroIndustrial's core loop is:

1. Supply AeroFlux to a machine.
2. The machine builds resonance progress.
3. High storage creates overload pressure.
4. Create kinetic infrastructure can optionally contribute AeroFlux.
5. The Vector Thruster can optionally couple AeroFlux to compatible moving structures.

This is the current foundation for the larger original machine-processing system; it should not be described as a finished production release yet.

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

The repository contains a buildable reconstructed core. The latest CI build must still be treated as a **prototype release**, not a fully runtime-verified final release: the optional integrations have not been tested in a real Minecraft instance with the complete Create + Sable + Aeronautics + GTCEu stack.
