package Interfaces;

public interface IAbility {
    void use(); // called by owner
    void spend(); // called by use()
    void update(double time); // called by owner on every update
    void cool(double time); // called by update
    void reset(); // called by cool()
}
