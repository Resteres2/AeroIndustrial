package dev.anton.aeroindustrial.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class AeroConfig {
    public static final AeroConfig CONFIG;
    public static final ModConfigSpec SPEC;

    public final ModConfigSpec.IntValue createInputPerPulse;
    public final ModConfigSpec.IntValue gtceuInputPerPulse;
    public final ModConfigSpec.IntValue thrusterEnergyCost;
    public final ModConfigSpec.DoubleValue thrusterStrength;

    private AeroConfig(ModConfigSpec.Builder builder) {
        builder.push("integration");

        createInputPerPulse = builder
                .comment("Maximum AeroFlux accepted from Create by a Torque Resonator every 10 ticks.")
                .translation("config.aeroindustrial.create_input_per_pulse")
                .defineInRange("create_input_per_pulse", 120, 0, 10000);

        gtceuInputPerPulse = builder
                .comment("Maximum AeroFlux accepted from GTCEu-compatible energy storage every 10 ticks.")
                .translation("config.aeroindustrial.gtceu_input_per_pulse")
                .defineInRange("gtceu_input_per_pulse", 120, 0, 10000);

        builder.pop();
        builder.push("thruster");

        thrusterEnergyCost = builder
                .comment("AeroFlux consumed by the Vector Thruster per successful pulse.")
                .translation("config.aeroindustrial.thruster_energy_cost")
                .defineInRange("thruster_energy_cost", 25, 1, 10000);

        thrusterStrength = builder
                .comment("Base impulse strength applied by the Vector Thruster.")
                .translation("config.aeroindustrial.thruster_strength")
                .defineInRange("thruster_strength", 0.03D, 0.001D, 10.0D);

        builder.pop();
    }

    static {
        Pair<AeroConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(AeroConfig::new);
        CONFIG = pair.getLeft();
        SPEC = pair.getRight();
    }

    private AeroConfig() {
        this.createInputPerPulse = null;
        this.gtceuInputPerPulse = null;
        this.thrusterEnergyCost = null;
        this.thrusterStrength = null;
    }
}
