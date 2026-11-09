# Changelog

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
