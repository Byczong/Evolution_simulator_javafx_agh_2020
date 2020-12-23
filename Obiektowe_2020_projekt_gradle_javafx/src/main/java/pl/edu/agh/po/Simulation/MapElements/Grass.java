package pl.edu.agh.po.Simulation.MapElements;


import pl.edu.agh.po.Simulation.GeneralClasses.Vector2d;

public class Grass extends AbstractMapElement {

    public Grass(Vector2d position){
        this.position = position;
    }

    @Override
    public boolean isEdible(){
        return true;
    }

    @Override
    public void move() {}

    @Override
    public String toString() {
        return "*";
    }
}
