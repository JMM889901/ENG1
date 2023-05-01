package com.devcharles.piazzapanic.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.devcharles.piazzapanic.scene2d.Hud;
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

    public static void save(String filename, World world, Hud hud) {

        SaveData saveData = new SaveData();

        // Get data from passed in world and hud.

        saveData.money = 1234567;
        saveData.reputation = 2;


        // Take the structured data and now save it.

        Json json = new Json();
        String saveFile = json.prettyPrint(saveData);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(saveFile);
            System.out.println("Saved to " + filename);
            writer.close();
        } catch (Exception e) {
            System.out.println("Error saving to " + filename);
        }
    }

    public static SaveData load(String filename) {
        Json json = new Json();
        SaveData saveData = new SaveData();
        String saveFile = "";
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            
            String curLine;
            curLine = reader.readLine();
            while (curLine != null) {
                saveFile += curLine + "\n";
                curLine = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error loading from " + filename);
        }

        saveData = json.fromJson(SaveData.class, saveFile);

        return saveData;
    }
}
