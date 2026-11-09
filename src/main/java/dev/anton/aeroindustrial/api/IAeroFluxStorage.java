package dev.anton.aeroindustrial.api;

/**
 * Public storage contract for AeroFlux-capable block entities.
 *
 * Implementations must clamp receive/extract return values to the requested amount,
 * never return a negative value, and must not mutate stored energy when simulate is true.
 */
public interface IAeroFluxStorage {
    int receiveAeroFlux(int amount, boolean simulate);
    int extractAeroFlux(int amount, boolean simulate);
    int getAeroFluxStored();
    int getMaxAeroFluxStored();
}
