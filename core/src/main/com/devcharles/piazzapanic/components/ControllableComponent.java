package main.com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;
import main.com.devcharles.piazzapanic.utility.FoodStack;

public class ControllableComponent implements Component {
    public FoodStack currentFood = new FoodStack();
}
