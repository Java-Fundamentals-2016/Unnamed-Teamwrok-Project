package Renderer;

import Enumerations.AnimationState;
import Enumerations.EntityState;
import Game.Main;
import Models.Enemy;
import Models.Entity;
import Models.Player;

public class CharView {

    public static void parseCharacter(Entity sender, double x, double y, double direction, double progress, AnimationState state) {
        if (sender instanceof Player) animateSwordsman(sender, x, y, direction, progress, state);
        else if (sender instanceof Enemy) {
            if (sender.getAnimation().type.equals("SKELETON")) animateSwordsman(sender, x, y, direction, progress, state);
            else if (sender.getAnimation().type.equals("GIANT_RAT")) animateBeast(sender, x, y, direction, progress, state);
        }
    }

    public static void animateSwordsman(Entity sender, double x, double y, double direction, double progress, AnimationState state) {
        // check type and state an output accordingly
        int selector = 0;
        if (sender.hasState(EntityState.DEAD)) selector = 4;
        if (sender instanceof Player) {
            selector = 1;
            if (state == AnimationState.IDLE) {
                QuickView.renderSword(x, y, direction, 0.0);
                QuickView.renderShield(x, y, direction, 0);
            } else if (state == AnimationState.DEFEND) {
                QuickView.renderSword(x, y, direction, 0.01);
                QuickView.renderShield(x, y, direction, 1);
            }
        }
        if (state == AnimationState.ATTACKUP) {
            QuickView.renderSword(x, y, direction, progress);
            selector = 2;
        }
        if (state == AnimationState.ATTACKING) {
            if (progress > 1.33) {
                QuickView.renderSwipe(x, y, direction, 1);
                QuickView.renderSword(x, y, direction, 2);
            } else {
                QuickView.renderSwipe(x, y, direction, (progress - 1) * 3);
                QuickView.renderSword(x, y, direction, (progress - 1) * 3 + 1);
                selector = 2;
            }
        }
        if (state == AnimationState.ATTACKDOWN) {
            QuickView.renderSword(x, y, direction, progress);
        }
        if (sender.getState().contains(EntityState.DAMAGED)) selector = 3;
        QuickView.renderSprite(selector, x, y, direction, sender.getRadius());
    }

    public static void animateBeast(Entity sender, double x, double y, double direction, double progress, AnimationState state) {
        // check type and state an output accordingly
        int selector = 0;
        if (sender.hasState(EntityState.DEAD)) selector = 4;
        if (state == AnimationState.ATTACKUP) {
            QuickView.renderClaws(x, y, direction, progress);
            selector = 2;
        }
        if (state == AnimationState.ATTACKING) {
            // todo render swipe
            if (progress > 1.33) {
                QuickView.renderClaws(x, y, direction, 2);
            } else {
                QuickView.renderClaws(x, y, direction, (progress - 1) * 3 + 1);
                selector = 2;
            }
        }
        if (state == AnimationState.ATTACKDOWN) {
            QuickView.renderClaws(x, y, direction, progress);
        }
        if (sender.getState().contains(EntityState.DAMAGED)) selector = 3;
        QuickView.renderSprite(selector, x, y, direction, sender.getRadius());
    }

}
