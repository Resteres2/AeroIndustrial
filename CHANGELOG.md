# Changelog

## Unreleased — Propulsion and network pass

### Compatibility and propulsion
- Resolved Sable's public API methods by exact signatures and cached the reflection bindings instead of searching method overloads on every thruster pulse.
- Limited the non-Sable Vector Thruster fallback to the nearest compatible moving-structure entity within a configurable radius.
- Cached Create speed-method lookups per block-entity class and ignored non-finite speed values.
- Limited Torque Resonator extraction to the actual free AeroFlux capacity to prevent energy loss at a full reservoir.

### AeroFlux network
- Made the AeroFlux Capacitor use the public AeroFlux block capability, allowing it to feed compatible third-party storage implementations instead of only native AeroIndustrial machines.
- Added server settings for accumulator intake, capacitor packet size and reserve, Mobile Dock transfer, and Aeronautical Conduit throughput.
- Updated RU/EN configuration labels, manual descriptions, and README configuration documentation.

### Verification
- Source diffs and localization JSON were checked after each change.
- GitHub Actions run 42684953561 completed successfully for commit c07c005; the Java 21 Gradle build and JAR upload both passed.
- In-game behavior and compatibility with the optional Create, Sable, Aeronautics, and GTCEu versions still require the manual matrix in docs/INTEGRATION_TEST_MATRIX.md.

## 1.1.0 — Engineering hardening

### Runtime and compatibility
- Hardened Create kinetic detection so the bridge only inspects classes in the Create package namespace.
- Hardened moving-structure integration against disabled integrations and invalid strength/radius values.
- Kept GTCEu harvesting to one energy extraction per machine per pulse to avoid multi-side double counting.
- Reset integration telemetry every machine tick so status output cannot report stale Create/GTCEu input after a pause.

### Machine system
- Added a short-lived per-tier recipe cache to reduce repeated RecipeManager scans while machines are processing.
- Reused the same cached tier recipe list for machine-role detection.
- Added defensive validation of recipe outputs before processing to prevent malformed data from creating empty or oversized output stacks.
- Preserved periodic block-entity persistence for long-running processing.

### Assets and usability
- Restored the missing AeroManual and AeroWrench item textures.
- Added iron-tier mining tags for all registered AeroIndustrial machines.
- Kept the native JEI processing category, localized machine status, manual and wrench workflows.

### Verification note
The repository contains a GitHub Actions build workflow, but a successful workflow run is required before a revision is considered build-verified. Optional Create, Sable, Create Aeronautics and GTCEu bridges remain intentionally conservative compatibility layers.
