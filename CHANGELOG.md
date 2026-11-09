# AeroIndustrial Changelog

## 1.1.0

### Machines
- 15 original AeroFlux machines with distinct roles.
- Data-driven machine processing recipes.
- Tier-specific recipe matching.
- Redstone maintenance stop control.
- Comparator energy telemetry.
- Side-aware item automation.
- Horizontal machine orientation.
- Engineering Wrench for rotation.

### Energy and integration
- Dedicated `IAeroFluxStorage` API and NeoForge block capability.
- Create kinetic input through Torque Resonator.
- GTCEu-compatible energy input through the NeoForge energy bridge when available.
- Vector Thruster uses Sable physics impulses when a machine is inside a Sable sub-level.
- Conservative entity fallback for moving-structure compatibility.
- Server configuration for integration rates and thruster tuning.

### Interface
- Custom machine GUI with AeroFlux, progress and overload telemetry.
- Native optional JEI recipe category.
- Craftable Engineering Manual.
- Localized Russian and English status, tooltips and configuration labels.

### Visuals
- Custom industrial casing textures for all machines.
- Custom component textures.
- Tier-scaled illumination for high-tier machines.
- AeroFlux Crystal and Resonance Cell visual glint.

### Compatibility
- Minecraft 1.21.1
- NeoForge 21.1.x
- Create 6.0.10
- Create Aeronautics 1.3.2
- Sable 2.0.x
- GregTech CEu Modern 7.0.x

### Verification
- GitHub Actions build passes on the current main branch.
- Runtime compatibility with every external mod combination still requires in-game validation.
