package com.devcharles.piazzapanic;

import org.junit.*;

import com.devcharles.piazzapanic.PiazzaPanic;

import com.devcharles.piazzapanic.GameScreen;
public class testPiazzaPanic {
   @Test
   public void test() {
       Assert.assertTrue(true);

       PiazzaPanic piazzaPanic = new PiazzaPanic();
       Assert.assertEquals(piazzaPanic.VIRTUAL_HEIGHT, 20f, 0.01);  // Use the delta to specify an acceptable error margin.
   }
   @Test
   /**
    * Checks the VIRTUAL_HEIGHT attribute in the PiazzaPanic object is as it should be
    */
   public void virtualHeightTest() {
        PiazzaPanic panic = new PiazzaPanic();
        Assert.assertEquals(panic.VIRTUAL_HEIGHT, 20f, 0.01d);
   }
}
