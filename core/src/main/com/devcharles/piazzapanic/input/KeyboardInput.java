package main.com.devcharles.piazzapanic.input;

import com.badlogic.gdx.Gdx;

// Changes from original:
// Added functionality in PlayerControlSystem to allow all changing cooks in the reverse order.
// R, originally pick up, now does nothing.
// Q, originally interact, now does nothing.
// F is still put down.
// Space now picks up and also interacts.
// Ctrl+Space now puts down.
// E no longer changes cooks, K for forwards and L for backwards.
// LShift and RShift together enable an alternate set of keys, this is a debug feature so will
// likely do nothing in release and for most of development.


import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;

public class KeyboardInput implements InputProcessor {

    public boolean left, right, up, down;

    public boolean changeCooks;

    public boolean changeCooksReverse;

    public boolean putDown;

    public boolean pickUp;

    public boolean interact;

    public boolean compileMeal;

    public boolean giveToCustomer;

    public boolean disableHud;

    // checks whether ctrl+space was used to put down an item, rather than f.
    private boolean ctrl_spaced = false;

    private static int alternative_keys_progress = 0;
    private static boolean alt_keys = false;
    
    public void clearInputs() {
        left = false;
        right = false;
        up = false;
        down = false;
        changeCooks = false;
        putDown = false;
        pickUp = false;
        interact = false;
        compileMeal = false;
        giveToCustomer = false;
        disableHud = false;

        ctrl_spaced = false;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean processed = true;
        switch (keycode) {
            case Keys.LEFT:
            case Keys.A:
                left = true;
                break;
            case Keys.RIGHT:
            case Keys.D:
                right = true;
                break;
            case Keys.UP:
            case Keys.W:
                up = true;
                break;
            case Keys.DOWN:
            case Keys.S:
                down = true;
                break;
            case Keys.F:
                putDown = true;
                ctrl_spaced = false;
                break;
            case Keys.K:
                changeCooks = true;
                break;
            case Keys.L:
                changeCooksReverse = true;
                break;
            case Keys.SPACE:
                interact = true;
                compileMeal = true;
                giveToCustomer = true;
                // if ctrl is held, put down instead of pick up.
                if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT))
                {
                    putDown = true;
                    ctrl_spaced = true;
                } else {
                    pickUp = true;
                }
                break;
            case Keys.SHIFT_RIGHT:
                alternative_keys_progress |= 1;
                break;
            case Keys.SHIFT_LEFT:
                alternative_keys_progress |= 2;
                break;
            case Keys.H:
                disableHud = true;
            default:
                processed = false;
        }
        if((alternative_keys_progress & 3) == 3)
        {
            alt_keys = true;
            System.out.println("### Using alternative keys ###");
        }

        return processed;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean processed = true;
        switch (keycode) {
            case Keys.LEFT:
            case Keys.A:
                left = false;
                break;
            case Keys.RIGHT:
            case Keys.D:
                right = false;
                break;
            case Keys.UP:
            case Keys.W:
                up = false;
                break;
            case Keys.DOWN:
            case Keys.S:
                down = false;
                break;
            case Keys.K:
                changeCooks = false;
                break;
            case Keys.L:
                changeCooksReverse = false;
                break;
            case Keys.F:
                putDown = false;
                break;
            case Keys.SPACE:
                interact = false;
                compileMeal = false;
                giveToCustomer = false;
                pickUp = false;
                if(ctrl_spaced)
                {
                    putDown = false;
                    ctrl_spaced = false;
                }
                break;
            case Keys.SHIFT_RIGHT:
                alternative_keys_progress &= ~1;
                break;
            case Keys.SHIFT_LEFT:
                alternative_keys_progress &= ~2;
                break;
            case Keys.H:
                disableHud = false;
            default:
                processed = false;
        }
        return processed;
    }

    @Override
    public boolean keyTyped(char character) {
        boolean processed = true;
        switch (character) {
            default:
                processed = false;
        }
        return processed;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

}
