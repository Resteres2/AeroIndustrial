# AeroIndustrial

AeroIndustrial is an original Minecraft 1.21.1 NeoForge technology layer built around **AeroFlux**: a gameplay system intended to connect industrial infrastructure, mechanical systems and aeronautical structures without copying GregTech or Create: Aeronautics machines.

## Current implementation

- Minecraft 1.21.1
- NeoForge 21.1.x
- Java 21
- 15 registered blocks: 10 machines + 5 auxiliary structures
- 8 custom components
- Crafting chains and machine recipes
- Optional, crash-safe detection of Create, GTCEu and Aeronautics by registry namespace
- GitHub Actions build pipeline

The current core deliberately avoids hard compile-time dependencies on the optional mods. This makes the base mod independently buildable and prevents a missing optional dependency from becoming a hard crash.

## Original interaction model

AeroIndustrial machines expose an AeroFlux engineering layer. A machine reports whether nearby infrastructure belongs to Create, GTCEu or Aeronautics. The next integration stage can replace this namespace-level bridge with verified APIs where those APIs are available.

Right-clicking a machine with an AeroFlux Crystal performs a prototype phase-conversion process and produces Phase Foil.

## Build

Run `./gradlew build` with Java 21. The GitHub Actions workflow builds the project automatically on pushes to `main`.

## Status

This repository contains a reconstructed buildable core because the original local archive could not be transferred wholesale through the current GitHub connector. It is **not yet the final feature-complete release** promised by the design specification. Runtime testing and deep Create/Aeronautics/GTCEu API integrations still have to be verified.
