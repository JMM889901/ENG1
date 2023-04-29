package com.devcharles.piazzapanic.components;

import org.junit.*;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.devcharles.piazzapanic.testEnvironment;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.componentsystems.PlayerControlSystem;
import com.devcharles.piazzapanic.componentsystems.StationSystem;
import com.devcharles.piazzapanic.utility.Mappers;
import com.devcharles.piazzapanic.utility.Station.StationType;

public class testCookingComponent {
    @Test
    /**
     * Test that food has two stages of processing and changes what food it is after the end of those stages.
     */
    public void testCooking() {
        // Initialise environment
        testEnvironment environment = new testEnvironment();
        StationSystem stationSystem = new StationSystem(null, environment.factory);
        PlayerControlSystem playerControlSystem = new PlayerControlSystem(environment.input, environment.engine);
        environment.engine.addSystem(stationSystem);
        environment.engine.addSystem(playerControlSystem);

        // Create 3 foods that each require a different type of processing.
        Entity testPatty = environment.factory.createFood(FoodType.formedPatty); // Grill.
        Entity testPotato = environment.factory.createFood(FoodType.potato); // Oven.
        Entity testOnion = environment.factory.createFood(FoodType.onion); // Cutting board.

        // Create a chef to put things on the station.
        Entity testChef = environment.factory.createCook(0, 0);
        PlayerComponent testPlayerComponent = new PlayerComponent();
        testChef.add(testPlayerComponent);  // Make our chef the active "player".

        ControllableComponent testControllableComponent = Mappers.controllable.get(testChef);
        testControllableComponent.currentFood.pushItem(testPatty, testChef);  // There are "redundant" references to testChef due to 'slightly' dodgy architecture inherited from Group26.
        testControllableComponent.currentFood.pushItem(testPotato, testChef);
        testControllableComponent.currentFood.pushItem(testOnion, testChef);

        // Create stations to process food.
        Entity testOven = environment.factory.createStation(StationType.oven, new Vector2(1, 0), null, false);
        Entity testGrill = environment.factory.createStation(StationType.grill, new Vector2(2, 0), null, false);
        Entity testCuttingBoard = environment.factory.createStation(StationType.cutting_board, new Vector2(3, 0), null, false);

        StationComponent ovenComponent = Mappers.station.get(testOven);
        StationComponent grillComponent = Mappers.station.get(testGrill);
        StationComponent cuttingBoardComponent = Mappers.station.get(testCuttingBoard);


        // Start the first stage of processing.
        cuttingBoardComponent.interactingCook = testChef;
        testPlayerComponent.putDown = true;
        environment.engine.update(0.1f);
        cuttingBoardComponent.interactingCook = null;

        ovenComponent.interactingCook = testChef;
        testPlayerComponent.putDown = true;
        environment.engine.update(0.1f);
        ovenComponent.interactingCook = null;

        grillComponent.interactingCook = testChef;
        testPlayerComponent.putDown = true;
        environment.engine.update(0.1f);
        grillComponent.interactingCook = null;
        // (This is done in reverse order as above because the player's foodstack is of course LIFO.)
        

        // Everything processes for just long enough for the first stage.
        environment.engine.update(CookingComponent.COOKING_TIME_BASE / 1000f + 0.1f);

        // Start the second stage of processing.
        grillComponent.interactingCook = testChef;
        testPlayerComponent.interact = true;
        environment.engine.update(0.1f);
        grillComponent.interactingCook = null;

        ovenComponent.interactingCook = testChef;
        testPlayerComponent.interact = true;
        environment.engine.update(0.1f);
        ovenComponent.interactingCook = null;

        cuttingBoardComponent.interactingCook = testChef;
        testPlayerComponent.interact = true;
        environment.engine.update(0.1f);
        cuttingBoardComponent.interactingCook = null;


        // Allow time for the second stage of processing.
        environment.engine.update(CookingComponent.COOKING_TIME_BASE / 1000f + 0.1f);

        System.out.println(Mappers.food.get(cuttingBoardComponent.food.get(0)).type);

        // Test that everything has cooked/cut.
        Assert.assertTrue(Mappers.food.get(grillComponent.food.get(0)).type == FoodType.grilledPatty);
        Assert.assertTrue(Mappers.food.get(ovenComponent.food.get(0)).type == FoodType.bakedPotatoPlain);
        Assert.assertTrue(Mappers.food.get(cuttingBoardComponent.food.get(0)).type == FoodType.slicedOnion);
    }
}
