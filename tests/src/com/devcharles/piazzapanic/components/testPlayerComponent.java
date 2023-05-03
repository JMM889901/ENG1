package com.devcharles.piazzapanic.components;
import org.junit.*;
public class testPlayerComponent {
    @Test
    /**
     * Check if we can instantiate the player component
     */
    public void instantiatePlayerComponent(){
        PlayerComponent testPlayer = new PlayerComponent();
        Assert.assertTrue(testPlayer instanceof PlayerComponent);
    }
    /**
     * Check the default attributes of the testPlayer component
     */
    @Test
    public void testPlayerComponentAttributes(){
        PlayerComponent testPlayer = new PlayerComponent();
        Assert.assertFalse(testPlayer.putDown);
        Assert.assertFalse(testPlayer.pickUp);
        Assert.assertFalse(testPlayer.interact);
        Assert.assertFalse(testPlayer.compileMeal);
        Assert.assertFalse(testPlayer.giveToCustomer);
    }

    
}
