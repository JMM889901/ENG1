package com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;
import com.devcharles.piazzapanic.utility.FoodStack;

/**
 * Component given to any entity that may at some point be controlled by the player.
 * <p>
 * This is essentially used to remember chef inventories.
 */
public class ControllableComponent implements Component {
    public FoodStack currentFood = new FoodStack();
}
