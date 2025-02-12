package com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;

/**
 * A chef has this if it is being controlled right now. The five values should be self explanatory,
 * but simply having this component means the chef responds to keyboard actions to move them etc.
 */
public class PlayerComponent implements Component {
    // These are all controls that a player could be utilising.
    public boolean putDown = false;
    public boolean pickUp = false;
    public boolean interact = false; // (Interact with a station.)
    public boolean compileMeal = false;
    public boolean giveToCustomer = false;
}
