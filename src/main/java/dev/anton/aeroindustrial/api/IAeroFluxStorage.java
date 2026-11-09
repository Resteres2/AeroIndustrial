package dev.anton.aeroindustrial.api;

public interface IAeroFluxStorage {
    int receiveAeroFlux(int amount, boolean simulate);
    int extractAeroFlux(int amount, boolean simulate);
    int getAeroFluxStored();
    int getMaxAeroFluxStored();
}
