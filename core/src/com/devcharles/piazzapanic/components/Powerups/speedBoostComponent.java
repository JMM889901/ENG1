package com.devcharles.piazzapanic.components.Powerups;

import com.badlogic.ashley.core.Component;

public class speedBoostComponent implements Component {
    public static int boostSpeed = 4000;
    public static float timeMax = 5;
    public float timeHad;
}
