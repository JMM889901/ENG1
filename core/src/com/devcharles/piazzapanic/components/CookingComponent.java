package com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.devcharles.piazzapanic.utility.GdxTimer;

/**
 * Component for a food/ingredient that can be cooked/processed.
 * <p>
 * This component is removed when the food is cooked or processed.
 * <p>
 * See
 * {@link com.devcharles.piazzapanic.componentsystems.StationSystem#stationTick(StationComponent,
 * float) StationSystem.java} for details.
 */
public class CookingComponent implements Component, Poolable {
    public static int COOKING_TIME_BASE = 5000;
    // Changed from constant to static for FR_INVESTMENT to allow store to reduce
    // cooking time
    public GdxTimer timer = new GdxTimer(COOKING_TIME_BASE, false, false); // We may want to tweak this number for
                                                                           // difficulty levels.
    public float debugPrintableTimer = 0;
    /**
     * If patty is flipped, onion is chopped, etc.
     */
    public boolean processed = false;

    @Override
    public void reset() {
        timer = new GdxTimer(COOKING_TIME_BASE, false, false);// HACK: This potentially creates 2 timers but that
                                                              // shouldnt cause any issues
        // Result of implementing FR_POWERUPS to circumvent engine copying existing
        // components instead of properly reinstating them
        processed = false;
    }
}
