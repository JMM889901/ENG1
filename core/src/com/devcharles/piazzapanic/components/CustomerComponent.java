package com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.utility.GdxTimer;

/**
 * Inventory-storing component for customers.
 * <p>
 * It stores
 * order - What the customer wants.
 * interactingCook - What chef entity is bumping into the customer.
 * food - Food entity which has been given to the customer.
 * timer - A timer indicating how long the customer has been waiting (when this loops past the time limit, lose a reputation point).
 */
public class CustomerComponent implements Component, Poolable {
    public FoodType order = null;
    public Entity interactingCook = null;
    public Entity food = null;
    public GdxTimer timer = new GdxTimer(90000, false, true); // This is set to loop but I don't think it should. - Joss
    @Override
    public void reset() {
        order = null;
        interactingCook = null;
        food = null;
        timer.stop();
        timer.reset();
    }
}
