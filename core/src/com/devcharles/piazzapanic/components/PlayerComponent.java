package com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;

/**
 * A chef has this if it is being controlled right now. The five values here are self explanatory,
 * but simply having this component means the chef responds to keyboard actions to move them etc.
 */
public class PlayerComponent implements Component {
    public boolean putDown = false;
    public boolean pickUp = false;
    public boolean interact = false;
    public boolean compileMeal = false;
    public boolean giveToCustomer = false;
}
