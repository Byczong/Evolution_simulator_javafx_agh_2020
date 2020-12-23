package pl.edu.agh.po.Simulation.MapElements;


import pl.edu.agh.po.Simulation.GeneralClasses.Vector2d;
import pl.edu.agh.po.Simulation.Interfaces.IMapElement;

public abstract class AbstractMapElement implements IMapElement {

    protected Vector2d position;

    @Override
    public Vector2d getPosition() {
        return this.position;
    }

    @Override
    public abstract void move();
}
