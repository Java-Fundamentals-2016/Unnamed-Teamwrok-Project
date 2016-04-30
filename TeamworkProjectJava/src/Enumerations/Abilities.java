package Enumerations;

public enum Abilities {
    ATTACKPRIMARY ("Primary Attack"),
    DASH ("Dash"),
    DEFEND ("Defend");

    private String name;

    private Abilities(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

