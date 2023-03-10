package main.com.devcharles.piazzapanic.components.Powerups;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class PowerupComponent implements Component {
    public enum powerupType {
        speedBoost,
        cookBoost,
        cutBoost,
        timeFreezeBoost,
        orderBoost
    }

    public Entity playerTouched;
    public powerupType type;
    public boolean markedForDeletion;
}
