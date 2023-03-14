package com.devcharles.piazzapanic;

import org.junit.*;

// Import libgdx desktop to test that.
//import com.devcharles.piazzapanic.DesktopLauncher;
import com.devcharles.piazzapanic.PiazzaPanic;

public class testTests {
   @Test
   public void test() {
       Assert.assertTrue(true);

       PiazzaPanic piazzaPanic = new PiazzaPanic();
       Assert.assertEquals(piazzaPanic.VIRTUAL_HEIGHT, 20f, 0.01);  // Use the delta to specify an acceptable error margin.
   }
}
