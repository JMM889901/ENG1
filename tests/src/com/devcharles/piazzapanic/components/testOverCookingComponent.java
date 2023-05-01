package com.devcharles.piazzapanic.components;

import static org.junit.Assert.assertTrue;

import org.junit.*;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.testEnvironment;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.componentsystems.PlayerControlSystem;
import com.devcharles.piazzapanic.componentsystems.StationSystem;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.Mappers;
import com.devcharles.piazzapanic.utility.Station.StationType;

public class testOverCookingComponent {
    @Test
    /**
     * Check foods become spoiled if processing too long.
     */
    public void testOverCooking() {
        testEnvironment environment = new testEnvironment();
        StationSystem stationSystem = new StationSystem(null, environment.factory);
        PlayerControlSystem playerControlSystem = new PlayerControlSystem(environment.input, environment.engine);
        environment.engine.addSystem(stationSystem);
        environment.engine.addSystem(playerControlSystem);

        // Create 3 foods that each require a different type of processing.
        Entity testOnion = environment.factory.createFood(FoodType.onion); // Cutting board.
        Entity testPatty = environment.factory.createFood(FoodType.formedPatty); // Grill.
        Entity testPotato = environment.factory.createFood(FoodType.potato); // Oven.


        // Create a chef to put things on the station.
        Entity testChef = environment.factory.createCook(0, 0);
        PlayerComponent testPlayerComponent = new PlayerComponent();
        testChef.add(testPlayerComponent); // Make our chef the active "player".

        ControllableComponent testControllableComponent = Mappers.controllable.get(testChef);
        testControllableComponent.currentFood.pushItem(testOnion, testChef);  // There are "redundant" references to testChef due to 'slightly' dodgy architecture inherited from Group26.
        testControllableComponent.currentFood.pushItem(testPatty, testChef);
        testControllableComponent.currentFood.pushItem(testPotato, testChef);
        

        // Create stations to process food.
        Entity testCuttingBoard = environment.factory.createStation(StationType.cutting_board, new Vector2(3, 0), null, false);
        Entity testOven = environment.factory.createStation(StationType.oven, new Vector2(1, 0), null, false);
        Entity testGrill = environment.factory.createStation(StationType.grill, new Vector2(2, 0), null, false);
        
        StationComponent cuttingBoardComponent = Mappers.station.get(testCuttingBoard);
        StationComponent ovenComponent = Mappers.station.get(testOven);
        StationComponent grillComponent = Mappers.station.get(testGrill);

        // Start the first stage of processing.
        ovenComponent.interactingCook = testChef;  // (This is done in reverse order as above because the player's foodstack is of course LIFO.)
        testPlayerComponent.putDown = true;
        environment.engine.update(0.1f);
        ovenComponent.interactingCook = null;

        grillComponent.interactingCook = testChef;
        testPlayerComponent.putDown = true;
        environment.engine.update(0.1f);
        grillComponent.interactingCook = null;

        cuttingBoardComponent.interactingCook = testChef;
        testPlayerComponent.putDown = true;
        environment.engine.update(0.1f);
        // Don't remove interactingCook here because you can't AFK a cutting board.

        // Everything processes for just long enough for the first stage.
        environment.engine.update(CookingComponent.COOKING_TIME_BASE / 1000f + 0.1f);

        cuttingBoardComponent.interactingCook = null;

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

        // Allow time for the second stage of processing.
        environment.engine.update(CookingComponent.COOKING_TIME_BASE / 1000f + 0.1f);

        System.out.println(Mappers.food.get(cuttingBoardComponent.food.get(0)).type);

        // Test that everything has cooked/cut.
        Assert.assertTrue(Mappers.food.get(grillComponent.food.get(0)).type == FoodType.grilledPatty);
        Assert.assertTrue(Mappers.food.get(ovenComponent.food.get(0)).type == FoodType.bakedPotatoPlain);
        Assert.assertTrue(Mappers.food.get(cuttingBoardComponent.food.get(0)).type == FoodType.slicedOnion);


        Assert.assertTrue(false);
    }
}
