package com.devcharles.piazzapanic.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.devcharles.piazzapanic.components.AIAgentComponent;
import com.devcharles.piazzapanic.components.CustomerComponent;
import com.devcharles.piazzapanic.components.TransformComponent;
import com.devcharles.piazzapanic.componentsystems.CustomerAISystem;
import com.devcharles.piazzapanic.scene2d.Hud;
import com.devcharles.piazzapanic.utility.saveStructure.CustomerData;
import com.devcharles.piazzapanic.utility.saveStructure.FoodData;
import com.devcharles.piazzapanic.utility.saveStructure.SaveData;

/**
 * 
 * We need to save:
 * - Money
 * - Reputation
 * - Stations unlocked.
 * - Customers and what they want
 * - Game time
 * - What's on each station.
 * - How processed each item in a station is.
 */
public class SaveHandler {
    public final static String SAVE_FILE = "save1.json";

    public static void save(String filename, Engine engine, World world, Hud hud) {

        SaveData saveData = new SaveData();

        // Get data from passed in world and hud.

        saveData.money = Hud.getMoney();
        saveData.reputation = Hud.reputation[0];
        saveData.difficulty = CustomerAISystem.getDifficulty();
        saveData.maxCustomers = CustomerAISystem.getMaxCustomers();
        saveData.gameTime = hud.customerTimer;

        ImmutableArray<Entity> customers = engine.getEntitiesFor(Family.all(CustomerComponent.class).get());

        saveData.customers = new CustomerData[customers.size()];

        for (int i = 0; i < customers.size(); i++) {
            Entity customer = customers.get(i);
            CustomerData customerData = new CustomerData();
            CustomerComponent customerComponent = customer.getComponent(CustomerComponent.class);
            TransformComponent transformComponent = customer.getComponent(TransformComponent.class);
            AIAgentComponent aiAgentComponent = customer.getComponent(AIAgentComponent.class);

            FoodData foodData = new FoodData();
            foodData.type = customerComponent.order;

            customerData.x = transformComponent.position.x;
            customerData.y = transformComponent.position.y;
            customerData.patience = customerComponent.timer.peakElapsed();
            customerData.objective = aiAgentComponent.currentObjective;
            customerData.order = foodData;

            saveData.customers[i] = customerData;
        }

        // Take the structured data and now save it.

        Json json = new Json();
        String saveText = json.prettyPrint(saveData);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(saveText);
            writer.close();
            System.out.println("Saved to " + filename);
        } catch (Exception e) {
            System.out.println("Error saving to " + filename);
        }
    }

    public static void load(String filename, Engine engine, EntityFactory entityFactory, World world, Hud hud) {
        Json json = new Json();
        SaveData saveData = new SaveData();
        String saveText = "";

        // Read structured data from the save file.

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            String curLine;
            curLine = reader.readLine();
            while (curLine != null) {
                saveText += curLine + "\n";
                curLine = reader.readLine();
            }
            reader.close();
            System.out.println("Loaded from " + filename);
        } catch (Exception e) {
            System.out.println("Error loading from " + filename);
        }

        saveData = json.fromJson(SaveData.class, saveText);

        // Set values in the world and hud based on the structured data.

        hud.initMoney(saveData.money);
        // hud.addMoney(saveData.money);
        Hud.reputation[0] = saveData.reputation;
        CustomerAISystem.setDifficulty(saveData.difficulty);
        CustomerAISystem
                .setMaxCustomers(saveData.maxCustomers == -1 ? (int) Double.POSITIVE_INFINITY : saveData.maxCustomers);
        hud.customerTimer = saveData.gameTime;

        engine.getSystem(CustomerAISystem.class).firstSpawn = false;

        // Clear out existing customers (and also consequently their orders).
        for (Entity customer : engine.getEntitiesFor(Family.all(CustomerComponent.class).get())) {
            engine.getSystem(CustomerAISystem.class).customers.remove(customer);
            engine.removeEntity(Mappers.customer.get(customer).food);
            world.destroyBody(Mappers.b2body.get(customer).body);
            engine.removeEntity(customer);
        }

        // Create new customers.
        for (CustomerData customer : saveData.customers) {
            Entity customerEntity = entityFactory.createCustomer(new Vector2(customer.x, customer.y));

            CustomerComponent customerComponent = customerEntity.getComponent(CustomerComponent.class);
            AIAgentComponent aiAgentComponent = customerEntity.getComponent(AIAgentComponent.class);

            customerComponent.timer.setElapsed(customer.patience);
            engine.getSystem(CustomerAISystem.class).makeItGoThere(aiAgentComponent, customer.objective - 1);
            customerComponent.order = customer.order.type;
        }
    }
}
