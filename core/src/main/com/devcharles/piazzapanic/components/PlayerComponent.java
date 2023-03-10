package main.com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;

public class PlayerComponent implements Component {
    public boolean putDown = false;
    public boolean pickUp = false;
    public boolean interact = false;
    public boolean compileMeal = false;
    public boolean giveToCustomer = false;
}
