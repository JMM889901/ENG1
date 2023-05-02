package com.devcharles.piazzapanic.componentsystems;

import org.junit.*;
import com.devcharles.piazzapanic.testEnvironment;
import com.devcharles.piazzapanic.components.ControllableComponent;
import com.devcharles.piazzapanic.components.FoodComponent;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.components.StationComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.input.KeyboardInput;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.Mappers;
import com.devcharles.piazzapanic.utility.Station.StationType;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.math.Vector2;

public class testPlayerInventorySystem {
    @Test
    /**
     * Tests if a player can pick up an item from a countertop
     */
    public void testPlayerPickUpItem() {
        // Set up environment.
        new testEnvironment();
        PooledEngine engine = new PooledEngine();
        World world = new World(new Vector2(0, 0), true);
        EntityFactory testEntityFactory = new EntityFactory(engine, world);
        KeyboardInput keyboardInput = new KeyboardInput();

        PlayerControlSystem testPlayerControlSystem = new PlayerControlSystem(keyboardInput, null);
        StationSystem testStationSystem = new StationSystem(keyboardInput, testEntityFactory);
        engine.addSystem(testPlayerControlSystem);
        engine.addSystem(testStationSystem);

        // Create entities.
        Entity chef = testEntityFactory.createCook(0, 0);  // Chef.
        PlayerComponent testPlayerComponent = new PlayerComponent();
        ControllableComponent testControllableComponent = Mappers.controllable.get(chef);
        chef.add(testPlayerComponent);  // Make our chef the active "player".

        Entity testTater = testEntityFactory.createFood(FoodType.potato);  // Potato.

        Entity testCountertop = testEntityFactory.createStation(StationType.counter, new Vector2(0, 0), null, false);  // Station.
        StationComponent testCountertopComponent = Mappers.station.get(testCountertop);
        testCountertopComponent.food.set(0, testTater);
        
        // Brief sanity check to make sure the countertop has a potato and the player has nothing.
        Assert.assertTrue(testControllableComponent.currentFood.isEmpty());
        Assert.assertTrue(Mappers.food.get(testCountertopComponent.food.get(0)).type == FoodType.potato);

        // Pick up the potato.
        testPlayerComponent.pickUp = true;
        testCountertopComponent.interactingCook = chef;  // This is so that PlayerControlSystem.processEntity() processes the chef made above. 
        testStationSystem.update(1);
        testStationSystem.processEntity(testCountertop, 1);

        Assert.assertTrue(testCountertopComponent.food.get(0) == null);
        Assert.assertTrue(testControllableComponent.currentFood.peek() == testTater);
    }

    @Test
    /**
     * Tests if a player can pick up one item of two from a countertop. This also by extension
     * tests that the order of items on the countertop is preserved.
     */
    public void testPlayerPickUpOneItem() {
        // Set up environment.
        new testEnvironment();
        PooledEngine engine = new PooledEngine();
        World world = new World(new Vector2(0, 0), true);
        EntityFactory testEntityFactory = new EntityFactory(engine, world);
        KeyboardInput keyboardInput = new KeyboardInput();

        PlayerControlSystem testPlayerControlSystem = new PlayerControlSystem(keyboardInput, null);
        StationSystem testStationSystem = new StationSystem(keyboardInput, testEntityFactory);
        engine.addSystem(testPlayerControlSystem);
        engine.addSystem(testStationSystem);

        // Create entities.
        Entity chef = testEntityFactory.createCook(0, 0);  // Chef.
        PlayerComponent testPlayerComponent = new PlayerComponent();
        ControllableComponent testControllableComponent = Mappers.controllable.get(chef);
        chef.add(testPlayerComponent);  // Make our chef the active "player".

        Entity testTater = testEntityFactory.createFood(FoodType.potato);  // Potato.

        Entity testCountertop = testEntityFactory.createStation(StationType.counter, new Vector2(0, 0), null, false);  // Station.
        StationComponent testCountertopComponent = Mappers.station.get(testCountertop);
        testCountertopComponent.food.set(0, testTater);
        testCountertopComponent.food.set(1, testEntityFactory.createFood(FoodType.onion));  // Pad the countertop with an onion, this onion otherwise does nothing and should not move.
        
        
        // Brief sanity check to make sure the countertop has an onion and a potato and the player has nothing.
        Assert.assertTrue(testControllableComponent.currentFood.isEmpty());
        Assert.assertTrue(testCountertopComponent.food.get(0) == testTater);
        Assert.assertTrue(Mappers.food.get(testCountertopComponent.food.get(1)).type == FoodType.onion);
        
        System.out.println(testCountertopComponent.food);

        // Pick up the potato.
        testPlayerComponent.pickUp = true;
        testCountertopComponent.interactingCook = chef;  // This is so that PlayerControlSystem.processEntity() processes the chef made above. 
        testStationSystem.update(1);
        testStationSystem.processEntity(testCountertop, 1);

        System.out.println(testCountertopComponent.food);

        Assert.assertTrue(testCountertopComponent.food.get(0) == null);
        Assert.assertTrue(Mappers.food.get(testCountertopComponent.food.get(1)).type == FoodType.onion);
        Assert.assertTrue(testControllableComponent.currentFood.peek() == testTater);
    }

    @Test
    /**
     * Tests if a player can pick up an item with just 1 inventory slot left.
     */
    public void testPlayerPickUpItemInventoryAlmostFull() {
        // Set up environment.
        new testEnvironment();
        PooledEngine engine = new PooledEngine();
        World world = new World(new Vector2(0, 0), true);
        EntityFactory testEntityFactory = new EntityFactory(engine, world);
        KeyboardInput keyboardInput = new KeyboardInput();

        PlayerControlSystem testPlayerControlSystem = new PlayerControlSystem(keyboardInput, null);
        StationSystem testStationSystem = new StationSystem(keyboardInput, testEntityFactory);
        engine.addSystem(testPlayerControlSystem);
        engine.addSystem(testStationSystem);

        // Create entities.
        Entity chef = testEntityFactory.createCook(0, 0);  // Chef.
        PlayerComponent testPlayerComponent = new PlayerComponent();
        ControllableComponent testControllableComponent = Mappers.controllable.get(chef);
        chef.add(testPlayerComponent);  // Make our chef the active "player".
        for(int i = 0; i < 11; i++) {
            // Fill the player's inventory with 11 onions (inventory max is 12).
            testControllableComponent.currentFood.push(testEntityFactory.createFood(FoodType.onion));
        }

        Entity testTater = testEntityFactory.createFood(FoodType.potato);  // Potato.

        Entity testCountertop = testEntityFactory.createStation(StationType.counter, new Vector2(0, 0), null, false);  // Station.
        StationComponent testCountertopComponent = Mappers.station.get(testCountertop);
        testCountertopComponent.food.set(0, testTater);
        
        // Brief sanity check to make sure the player's inventory is almost full and the countertop has a potato.
        Assert.assertTrue(testControllableComponent.currentFood.size() == 11);
        Assert.assertTrue(Mappers.food.get(testControllableComponent.currentFood.peek()).type == FoodType.onion);
        Assert.assertTrue(testCountertopComponent.food.get(0) == testTater);

        // Pick up the potato.
        testPlayerComponent.pickUp = true;
        testCountertopComponent.interactingCook = chef;  // This is so that PlayerControlSystem.processEntity() processes the chef made above. 
        testStationSystem.update(1);
        testStationSystem.processEntity(testCountertop, 1);

        Assert.assertTrue(testCountertopComponent.food.get(0) == null);
        Assert.assertTrue(testControllableComponent.currentFood.peek() == testTater);
    }

    @Test
    /**
     * Tests that a player can't pick up an item with a full inventory.
     */
    public void testPlayerPickUpItemInventoryFull() {
        // Set up environment.
        new testEnvironment();
        PooledEngine engine = new PooledEngine();
        World world = new World(new Vector2(0, 0), true);
        EntityFactory testEntityFactory = new EntityFactory(engine, world);
        KeyboardInput keyboardInput = new KeyboardInput();

        PlayerControlSystem testPlayerControlSystem = new PlayerControlSystem(keyboardInput, null);
        StationSystem testStationSystem = new StationSystem(keyboardInput, testEntityFactory);
        engine.addSystem(testPlayerControlSystem);
        engine.addSystem(testStationSystem);

        // Create entities.
        Entity chef = testEntityFactory.createCook(0, 0);  // Chef.
        PlayerComponent testPlayerComponent = new PlayerComponent();
        ControllableComponent testControllableComponent = Mappers.controllable.get(chef);
        chef.add(testPlayerComponent);  // Make our chef the active "player".
        for(int i = 0; i < 12; i++) {
            // Completely fill the player's inventory with 12 onions.
            testControllableComponent.currentFood.push(testEntityFactory.createFood(FoodType.onion));
        }

        Entity testTater = testEntityFactory.createFood(FoodType.potato);  // Potato.

        Entity testCountertop = testEntityFactory.createStation(StationType.counter, new Vector2(0, 0), null, false);  // Station.
        StationComponent testCountertopComponent = Mappers.station.get(testCountertop);
        testCountertopComponent.food.set(0, testTater);
        
        // Brief sanity check to make sure the player's inventory is almost full and the countertop has a potato.
        Assert.assertTrue(testControllableComponent.currentFood.size() == 12);
        Assert.assertTrue(Mappers.food.get(testControllableComponent.currentFood.peek()).type == FoodType.onion);
        Assert.assertTrue(testCountertopComponent.food.get(0) == testTater);

        // Try to pick up the potato.
        testPlayerComponent.pickUp = true;
        testCountertopComponent.interactingCook = chef;  // This is so that PlayerControlSystem.processEntity() processes the chef made above. 
        testStationSystem.update(1);
        testStationSystem.processEntity(testCountertop, 1);

        Assert.assertTrue(testCountertopComponent.food.get(0) == testTater);
        Assert.assertTrue(Mappers.food.get(testControllableComponent.currentFood.peek()).type == FoodType.onion);
    }

    @Test
    /**
     * Tests that a player can't pick up an item from an empty station.
     */
    public void testPlayerPickUpItemStationEmpty() {
        // Set up environment.
        new testEnvironment();
        PooledEngine engine = new PooledEngine();
        World world = new World(new Vector2(0, 0), true);
        EntityFactory testEntityFactory = new EntityFactory(engine, world);
        KeyboardInput keyboardInput = new KeyboardInput();

        PlayerControlSystem testPlayerControlSystem = new PlayerControlSystem(keyboardInput, null);
        StationSystem testStationSystem = new StationSystem(keyboardInput, testEntityFactory);
        engine.addSystem(testPlayerControlSystem);
        engine.addSystem(testStationSystem);

        // Create entities.
        Entity chef = testEntityFactory.createCook(0, 0);  // Chef.
        PlayerComponent testPlayerComponent = new PlayerComponent();
        ControllableComponent testControllableComponent = Mappers.controllable.get(chef);
        chef.add(testPlayerComponent);  // Make our chef the active "player".

        Entity testCountertop = testEntityFactory.createStation(StationType.counter, new Vector2(0, 0), null, false);  // Station.
        StationComponent testCountertopComponent = Mappers.station.get(testCountertop);
        
        // Brief sanity check to make sure both countertop and player have nothing.
        Assert.assertTrue(testControllableComponent.currentFood.isEmpty());
        Assert.assertTrue(testCountertopComponent.food.get(0) == null);

        // Pick up the potato.
        testPlayerComponent.pickUp = true;
        testCountertopComponent.interactingCook = chef;  // This is so that PlayerControlSystem.processEntity() processes the chef made above. 
        testStationSystem.update(1);
        testStationSystem.processEntity(testCountertop, 1);

        Assert.assertTrue(testCountertopComponent.food.get(0) == null);
        Assert.assertTrue(testControllableComponent.currentFood.isEmpty());
    }

    @Test
    /**
     * Tests if a player can put down an item.
     */
    public void testPlayerPutDownItem() {
        // Set up environment.
        new testEnvironment();
        PooledEngine engine = new PooledEngine();
        World world = new World(new Vector2(0, 0), true);
        EntityFactory testEntityFactory = new EntityFactory(engine, world);
        KeyboardInput keyboardInput = new KeyboardInput();

        PlayerControlSystem testPlayerControlSystem = new PlayerControlSystem(keyboardInput, null);
        StationSystem testStationSystem = new StationSystem(keyboardInput, testEntityFactory);
        engine.addSystem(testPlayerControlSystem);
        engine.addSystem(testStationSystem);

        // Create entities.
        Entity testTater = testEntityFactory.createFood(FoodType.potato);  // Potato.

        Entity chef = testEntityFactory.createCook(0, 0);  // Chef.
        PlayerComponent testPlayerComponent = new PlayerComponent();
        ControllableComponent testControllableComponent = Mappers.controllable.get(chef);
        chef.add(testPlayerComponent);  // Make our chef the active "player".
        testControllableComponent.currentFood.push(testTater);

        Entity testCountertop = testEntityFactory.createStation(StationType.counter, new Vector2(0, 0), null, false);  // Station.
        StationComponent testCountertopComponent = Mappers.station.get(testCountertop);
        

        // Brief sanity check to make sure the player has the potato.
        Assert.assertTrue(testControllableComponent.currentFood.peek() == testTater);
        Assert.assertTrue(testCountertopComponent.food.get(0) == null);

        // Put down the potato.
        testPlayerComponent.putDown = true;
        testCountertopComponent.interactingCook = chef;  // This is so that PlayerControlSystem.processEntity() processes the chef made above. 
        testStationSystem.update(1);
        testStationSystem.processEntity(testCountertop, 1);

        Assert.assertTrue(testCountertopComponent.food.get(0) == testTater);
        Assert.assertTrue(testControllableComponent.currentFood.isEmpty());
    }

    @Test
    /**
     * Tests if a player puts down just one item when holding more. This also by extension checks
     * that the order of items in the player's inventory is preserved.
     */
    public void testPlayerPutDownOneItem() {
        // Set up environment.
        new testEnvironment();
        PooledEngine engine = new PooledEngine();
        World world = new World(new Vector2(0, 0), true);
        EntityFactory testEntityFactory = new EntityFactory(engine, world);
        KeyboardInput keyboardInput = new KeyboardInput();

        PlayerControlSystem testPlayerControlSystem = new PlayerControlSystem(keyboardInput, null);
        StationSystem testStationSystem = new StationSystem(keyboardInput, testEntityFactory);
        engine.addSystem(testPlayerControlSystem);
        engine.addSystem(testStationSystem);

        // Create entities.
        Entity testTater = testEntityFactory.createFood(FoodType.potato);  // Potato.

        Entity chef = testEntityFactory.createCook(0, 0);  // Chef.
        PlayerComponent testPlayerComponent = new PlayerComponent();
        ControllableComponent testControllableComponent = Mappers.controllable.get(chef);
        chef.add(testPlayerComponent);  // Make our chef the active "player".
        testControllableComponent.currentFood.push(testEntityFactory.createFood(FoodType.onion));
        testControllableComponent.currentFood.push(testTater);

        Entity testCountertop = testEntityFactory.createStation(StationType.counter, new Vector2(0, 0), null, false);  // Station.
        StationComponent testCountertopComponent = Mappers.station.get(testCountertop);
        

        // Brief sanity check to make sure the player has the potato.
        Assert.assertTrue(testControllableComponent.currentFood.peek() == testTater);
        Assert.assertTrue(testCountertopComponent.food.get(0) == null);

        // Put down the potato.
        testPlayerComponent.putDown = true;
        testCountertopComponent.interactingCook = chef;  // This is so that PlayerControlSystem.processEntity() processes the chef made above. 
        testStationSystem.update(1);
        testStationSystem.processEntity(testCountertop, 1);

        Assert.assertTrue(testCountertopComponent.food.get(0) == testTater);
        Assert.assertTrue(Mappers.food.get(testControllableComponent.currentFood.peek()).type == FoodType.onion);  // Make sure the player has the onion left after putting down the potato.
    }

    @Test
    /**
     * Tests if a player can put down an item on a full station.
     */
    public void testPlayerPutDownItemStationAlmostFull() {
        // Set up environment.
        new testEnvironment();
        PooledEngine engine = new PooledEngine();
        World world = new World(new Vector2(0, 0), true);
        EntityFactory testEntityFactory = new EntityFactory(engine, world);
        KeyboardInput keyboardInput = new KeyboardInput();

        PlayerControlSystem testPlayerControlSystem = new PlayerControlSystem(keyboardInput, null);
        StationSystem testStationSystem = new StationSystem(keyboardInput, testEntityFactory);
        engine.addSystem(testPlayerControlSystem);
        engine.addSystem(testStationSystem);

        // Create entities.
        Entity testTater = testEntityFactory.createFood(FoodType.potato);  // Potato.

        Entity chef = testEntityFactory.createCook(0, 0);  // Chef.
        PlayerComponent testPlayerComponent = new PlayerComponent();
        ControllableComponent testControllableComponent = Mappers.controllable.get(chef);
        chef.add(testPlayerComponent);  // Make our chef the active "player".
        testControllableComponent.currentFood.push(testTater);

        Entity testCountertop = testEntityFactory.createStation(StationType.counter, new Vector2(0, 0), null, false);  // Station.
        StationComponent testCountertopComponent = Mappers.station.get(testCountertop);
        int stationSize = testCountertopComponent.food.size();
        for(int i = 0; i < stationSize - 1; i++) { // Fill 'er up with onions.
            testCountertopComponent.food.set(i, testEntityFactory.createFood(FoodType.onion));
        }
        

        // Brief sanity check to make sure the player has the potato and the station is almost full.
        Assert.assertTrue(testControllableComponent.currentFood.peek() == testTater);
        Assert.assertTrue(Mappers.food.get(testCountertopComponent.food.get(0)).type == FoodType.onion);
        Assert.assertTrue(Mappers.food.get(testCountertopComponent.food.get(stationSize - 2)).type == FoodType.onion);
        Assert.assertTrue(testCountertopComponent.food.get(stationSize - 1) == null);

        // Put down the potato.
        testPlayerComponent.putDown = true;
        testCountertopComponent.interactingCook = chef;  // This is so that PlayerControlSystem.processEntity() processes the chef made above. 
        testStationSystem.update(1);
        testStationSystem.processEntity(testCountertop, 1);

        Assert.assertTrue(testCountertopComponent.food.get(stationSize - 1) == testTater);
        Assert.assertTrue(testControllableComponent.currentFood.isEmpty());
    }

    @Test
    /**
     * Tests if a player can put down an item on a full station.
     */
    public void testPlayerPutDownItemStationFull() {
        // Set up environment.
        new testEnvironment();
        PooledEngine engine = new PooledEngine();
        World world = new World(new Vector2(0, 0), true);
        EntityFactory testEntityFactory = new EntityFactory(engine, world);
        KeyboardInput keyboardInput = new KeyboardInput();

        PlayerControlSystem testPlayerControlSystem = new PlayerControlSystem(keyboardInput, null);
        StationSystem testStationSystem = new StationSystem(keyboardInput, testEntityFactory);
        engine.addSystem(testPlayerControlSystem);
        engine.addSystem(testStationSystem);

        // Create entities.
        Entity testTater = testEntityFactory.createFood(FoodType.potato);  // Potato.

        Entity chef = testEntityFactory.createCook(0, 0);  // Chef.
        PlayerComponent testPlayerComponent = new PlayerComponent();
        ControllableComponent testControllableComponent = Mappers.controllable.get(chef);
        chef.add(testPlayerComponent);  // Make our chef the active "player".
        testControllableComponent.currentFood.push(testTater);

        Entity testCountertop = testEntityFactory.createStation(StationType.counter, new Vector2(0, 0), null, false);  // Station.
        StationComponent testCountertopComponent = Mappers.station.get(testCountertop);
        int stationSize = testCountertopComponent.food.size();
        for(int i = 0; i < stationSize; i++) { // Fill 'er up with onions.
            testCountertopComponent.food.set(i, testEntityFactory.createFood(FoodType.onion));
        }
        

        // Brief sanity check to make sure the player has the potato and the station is full.
        Assert.assertTrue(testControllableComponent.currentFood.peek() == testTater);
        Assert.assertTrue(Mappers.food.get(testCountertopComponent.food.get(0)).type == FoodType.onion);
        Assert.assertTrue(Mappers.food.get(testCountertopComponent.food.get(stationSize - 1)).type == FoodType.onion);

        // Try to put down the potato.
        testPlayerComponent.putDown = true;
        testCountertopComponent.interactingCook = chef;  // This is so that PlayerControlSystem.processEntity() processes the chef made above. 
        testStationSystem.update(1);
        testStationSystem.processEntity(testCountertop, 1);

        Assert.assertTrue(Mappers.food.get(testCountertopComponent.food.get(stationSize - 1)).type == FoodType.onion);
        Assert.assertTrue(testControllableComponent.currentFood.peek() == testTater);
    }

    /**
     * Test that a chef rembers the item it is holidng even when not selected
     */
    @Test
    public void testChefRememberItems() {
        testEnvironment environment = new testEnvironment();
        // Initialise wider systems.
        PooledEngine engine = (PooledEngine) environment.engine;

        KeyboardInput input = new KeyboardInput();
        PlayerControlSystem testPlayerControlSystem = new PlayerControlSystem(input, environment.engine);
        engine.addSystem(testPlayerControlSystem);

        // Initialise foods (and the entity factory to make said food).
        EntityFactory testEntityFactory = environment.factory;
        FoodComponent testFoodType = new FoodComponent();

        testFoodType.type = FoodType.unformedPatty; // From the FoodType: unformedPatty(1)
        Entity testFoodEntity = testEntityFactory.createFood(testFoodType.type);

        // Initialise a chef.
        Vector2 spawn = environment.spawnPoints.get(0);
        Entity chef = testEntityFactory.createCook((int) spawn.x, (int) spawn.y);
        spawn = environment.spawnPoints.get(1);
        Entity chef2 = testEntityFactory.createCook((int) spawn.x, (int) spawn.y);

        PlayerComponent testPlayerComponent = new PlayerComponent();
        chef.add(testPlayerComponent);
        engine.update(1);// Engine needs to tick so playercomponent is detected by systems
        testPlayerControlSystem.playerComponent.pickUp = true; // Set the pickUp flag to false, allows chef to pickup
        ControllableComponent testControllableComponent = chef.getComponent(ControllableComponent.class);
        // We need to make the chef actually pick something up! the testFoodEntity we
        // created earlier.

        testPlayerControlSystem.processEntity_test(testFoodEntity, 0);
        testControllableComponent.currentFood.init(engine);
        testControllableComponent.currentFood.pushItem(testFoodEntity, chef);

        input.changeCooks = true; // Change the chef to chef2
        engine.update(1);

        assert (testControllableComponent.currentFood.pop() == testFoodEntity); // chef remembers the food he picked up
                                                                                // when not selected
        testControllableComponent = chef2.getComponent(ControllableComponent.class);
        assert (testControllableComponent.currentFood.size() == 0); // chef2 does not remember the food chef1 picked
                                                                    // up
    }
}
