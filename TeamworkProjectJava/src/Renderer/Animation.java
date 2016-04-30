package Renderer;

import Enumerations.AnimationState;
import Enumerations.EntityState;
import Game.Main;
import Models.Entity;
import Models.Player;

public class Animation {
    // TODO: Add a container and methods to load image frames from disk into memory and output them to a display interface
    // phase lengths instead of sprite, temporary
    public String type = "";
    public double phase1 = 3.0;
    public double phase2 = 3.0;
    public double phase3 = 3.0;

    AnimationState state;
    double progress;
    double framerate; // frames per second

    public Animation(double framerate, String caller) {
        state = AnimationState.IDLE;
        progress = 0.0;
        this.framerate = framerate;
        //this.framerate = 1; // todo /!\ change back when finished testing /!\

        // set phase lengths based on caller type, temporary
        type = caller;
        if (caller.equals("Player")) {
            phase1 = 3.0;
            phase2 = 3.0;
            phase3 = 3.0;
        } else if (caller.equals("SKELETON")) {
            phase1 = 6.0;
            phase2 = 3.0;
            phase3 = 4.0;
        } else if (caller.equals("GIANT_RAT")) {
            phase1 = 2.0;
            phase2 = 6.0;
            phase3 = 8.0;
        }
    }

    /**
     * Output directly to display interface
     */
    public void output(Entity sender, double x, double y, double direction) {
        // temporarily reroute functions trough external class
        double adjusted = progress;
        switch (state) {
            case ATTACKUP: adjusted /= phase1; break;
            case ATTACKING: adjusted /= phase2; adjusted += 1; break;
            case ATTACKDOWN: adjusted /= phase3; adjusted += 2; break;
        }
        CharView.parseCharacter(sender, x, y, direction, adjusted, state);
    }

    public void advance(double time) {
        progress += time * framerate;
        // decide if state change is needed

        switch (state) {
            // TODO: these times should be based on the actual length of the sequence of frames
            case ATTACKUP:
                // if enough time has passed, change to next state
                if (progress >= phase1) {
                    progress = 0;
                    state = AnimationState.ATTACKING;
                }
                break;
            case ATTACKING:
                if (progress >= phase2) {
                    progress = 0;
                    state = AnimationState.ATTACKDOWN;
                }
                break;
            case ATTACKDOWN:
                // if enough time has passed, change back to idle
                if (progress >= phase3) {
                    progress = 0;
                    state = AnimationState.IDLE;
                }
                break;
            case DEFEND:
                break;
            // TODO: other cases
        }
    }

    public void setState(AnimationState state) {
        this.state = state;
        progress = 0;
    }

    public AnimationState getState() {
        return state;
    }

    // TODO: a method that returns the length of animation and special cue points to ability callers
    // for instance, attack has three sections - init (uncancelable, interruptable); resolution; wind down (cancelable)

    // note: might actually not be necessary, abilities can just get the state and decide if resolution has occurred

    /**
     * Get the length of animation sequence
     *
     * @param animation target sequence name
     * @return length of sequence in seconds
     */
    public double getLength(String animation) {
        return 0.0;
    }

    /**
     * Get the moment in time where resolution occurs
     *
     * @param animation target sequence name
     * @param index     ID of cue point, if more than one
     * @return moment of resolution since beginning of sequence
     */
    public double getCue(String animation, int index) {
        return 0.0;
    }

    public double getCue(String animation) {
        return 0.0;
    }
}
