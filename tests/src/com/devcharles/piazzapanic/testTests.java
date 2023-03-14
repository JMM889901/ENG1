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
    * 
    */
   public void virtualHeightTest() {
        PiazzaPanic panic = new PiazzaPanic();
        Assert.assertTrue(panic.VIRTUAL_HEIGHT == 20f);
   }
}
