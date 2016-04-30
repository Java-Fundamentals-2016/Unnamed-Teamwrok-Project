package Enumerations;

import Abilities.Ability;
import Renderer.Animation;

import java.util.HashMap;

public enum EnemyTypes {
    GIANT_RAT(new Animation(7, "GIANT_RAT"),
            50, 5, 0,
            new HashMap<Abilities, Ability>(),
            0.15, 3, 10),
    SLIME(new Animation(7, "SLIME"),
            150, 15, 0,
            new HashMap<Abilities, Ability>(),
            0.25, 3.5, 7),
    ZOMBIE(new Animation(7, "ZOMBIE"),
            200, 10, 5,
            new HashMap<Abilities, Ability>(),
            0.25, 4, 8),
    SKELETON(new Animation(7, "SKELETON"),
            100, 15, 5,
            new HashMap<Abilities, Ability>(),
            0.25, 3, 5);


    private Animation animation;
    private int healthPoints;
    private int attackPoints;
    private int armorValue;
    private HashMap<Abilities, Ability> abilities;
    private double radius;
    private double maxSpeed;
    private double maxAcceleration;

    EnemyTypes(Animation animation, int healthPoints, int attackPoints, int armorValue, HashMap<Abilities, Ability> abilities, double radius, double maxSpeed, double maxAcceleration) {
        this.animation = animation;
        this.healthPoints = healthPoints;
        this.attackPoints = attackPoints;
        this.armorValue = armorValue;
        this.abilities = abilities;
        this.radius = radius;
        this.maxSpeed = maxSpeed;
        this.maxAcceleration = maxAcceleration;
    }

    public Animation getAnimation() {
        return animation;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public int getAttackPoints() {
        return attackPoints;
    }

    public int getArmorValue() {
        return armorValue;
    }

    public HashMap<Abilities, Ability> getAbilities() {
        return abilities;
    }

    public double getRadius() {
        return radius;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getMaxAcceleration() {
        return maxAcceleration;
    }
}
