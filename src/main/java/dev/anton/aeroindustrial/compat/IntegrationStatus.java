package dev.anton.aeroindustrial.compat;

import net.neoforged.fml.ModList;

public final class IntegrationStatus {
    private IntegrationStatus() {}

    public static boolean createLoaded() {
        return isLoaded("create");
    }

    public static boolean sableLoaded() {
        return isLoaded("sable");
    }

    public static boolean aeronauticsLoaded() {
        return isLoaded("aeronautics");
    }

    public static boolean gtceuLoaded() {
        return isLoaded("gtceu");
    }

    public static boolean isLoaded(String modId) {
        return ModList.get() != null && ModList.get().isLoaded(modId);
    }

    public static String summary() {
        return "Create=" + (createLoaded() ? "ON" : "OFF")
                + " Sable=" + (sableLoaded() ? "ON" : "OFF")
                + " Aeronautics=" + (aeronauticsLoaded() ? "ON" : "OFF")
                + " GTCEu=" + (gtceuLoaded() ? "ON" : "OFF");
    }
}
