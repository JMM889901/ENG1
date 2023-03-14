package com.devcharles.piazzapanic;
import org.junit.*;
import com.devcharles.piazzapanic.GameScreen;
public class testTests {
   @Test
   public void test() {
       Assert.assertTrue(true);
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
