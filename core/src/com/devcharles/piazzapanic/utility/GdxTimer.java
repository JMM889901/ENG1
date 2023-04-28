// Simple timer by @tedigc
// https://gist.github.com/tedigc/fe28616706025b00c6c540af4d03c827

package com.devcharles.piazzapanic.utility;

/**
 * Simple timer class suitable for this project.
 * Modified for this project.
 * @author tedigc
 */
public class GdxTimer {

    private int delay;  // How long the timer "can" run for. - Joss
    private int elapsed;  // A counter starting from 0 that increases up to the value in delay. - Joss
    private boolean running;  // Whether the timer is currently advancing the value in elapsed upon calling tick(). - Joss
    private boolean looping;  // Whether the timer keeps going (subtracting delay from elapsed) after it reaches the "end" (ie delay). - Joss

    /**
     * Create a timer.
     * 
     * @param delay   delay in milliseconds.
     * @param running whether the timer is running when it's created
     * @param looping does the timer restart itself after elapsing.
     */
    public GdxTimer(int delay, boolean running, boolean looping) {
        this.delay = delay;
        this.running = running;
        this.looping = looping;
    }

    /**
     * Progress the timer
     * 
     * @param delta time since last frame.
     * @return Whether the timer is finished.
     */
    public boolean tick(float delta) {
        if (running) {
            elapsed += delta * 1000;
            if (elapsed > delay) {
                elapsed -= looping ? delay : 0;  // If looping, decrease by the length of the timer. - Joss
                return true;  // When looping, this will return true once, then keep returning false for a bit. - Joss
            }
        }
        return false;
    }

    public void start() {
        this.running = true;
    }

    public void stop() {
        this.running = false;
    }

    /**
     * Reset the timer. This does not stop it, for that use {@code GdxTimer.stop()}
     */
    public void reset() {
        this.elapsed = 0;
    }

    public int peakElapsed() {
        return this.elapsed;
    }
}
