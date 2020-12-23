package pl.edu.agh.po.Simulation.MapElements;

import pl.edu.agh.po.Simulation.Enums.MapDirection;
import pl.edu.agh.po.Simulation.GeneralClasses.Genes;
import pl.edu.agh.po.Simulation.GeneralClasses.Vector2d;
import pl.edu.agh.po.Simulation.Interfaces.IMapElement;
import pl.edu.agh.po.Simulation.Interfaces.IPositionChangeObserver;
import pl.edu.agh.po.Simulation.Interfaces.IPositionChangePublisher;
import pl.edu.agh.po.Simulation.Interfaces.IWorldMap;

import java.util.LinkedList;
import java.util.List;

public class Animal extends AbstractMapElement implements IPositionChangePublisher {

    private MapDirection orientation;
    private double energy;
    private int age;
    private int numberOfChildren;
    private final IWorldMap map;
    private final Genes genes;
    private final List<IPositionChangeObserver> positionObservers = new LinkedList<>();

    public Animal(IWorldMap map, Vector2d initialPosition, double initialEnergy){
        this.map = map;
        this.position = map.foldPosition(initialPosition);
        this.energy = initialEnergy;
        map.energyChanged(initialEnergy);
        this.orientation = MapDirection.randomDirection();
        this.genes = new Genes();
        this.age = 0;
        this.numberOfChildren = 0;
    }

    public Animal(IWorldMap map, Vector2d initialPosition, double initialEnergy, Genes genes){
        this.map = map;
        this.position = map.foldPosition(initialPosition);
        this.energy = initialEnergy;
        map.energyChanged(initialEnergy);
        this.orientation = MapDirection.randomDirection();
        this.genes = genes;
        this.age = 0;
        this.numberOfChildren = 0;
    }

    @Override
    public boolean isEdible(){
        return false;
    }

    public void changeEnergy(double value){
        energy += value;
        if(energy < 0)
            map.energyChanged(energy - value);
        else
            map.energyChanged(value);
        if(energy <= 0) {
            energy = 0;
            energyChangedTo0();
        }
    }

    @Override
    public void energyChangedTo0(){
        for(IPositionChangeObserver observer: positionObservers)
            observer.animalDied(this);
    }

    @Override
    public void move(){
        Vector2d oldPosition = new Vector2d(this.getPosition());
        this.position = this.position.add(this.orientation.toUnitVector());
        this.position = map.foldPosition(this.position);
        this.positionChanged(this, oldPosition, this.getPosition());
        this.age++;
    }

    public void rotate(){
        int rotationsNumber = genes.returnRandomGene();
        MapDirection afterRotations = orientation;
        for(int i = 0; i < rotationsNumber; i++)
            afterRotations = afterRotations.next();
        orientation = afterRotations;
    }

    @Override
    public void addObserver(IPositionChangeObserver observer) {
        positionObservers.add(observer);
    }

    @Override
    public void removeObserver(IPositionChangeObserver observer) {
        positionObservers.remove(observer);
    }

    @Override
    public void positionChanged(IMapElement movedElement, Vector2d oldPosition, Vector2d newPosition) {
        for(IPositionChangeObserver observer: positionObservers)
            observer.positionChanged(movedElement, oldPosition, newPosition);
    }

    public Animal reproduce(Animal partner, Vector2d childPosition) {
        double childEnergy = (double) (int) (0.25 * partner.getEnergy()) + (int) (0.25 * this.energy);
        this.changeEnergy( -(0.25 * this.energy));
        partner.changeEnergy( -(0.25 * partner.getEnergy()));
        numberOfChildren++;
        partner.numberOfChildren++;
        return new Animal(map, childPosition, childEnergy, new Genes(this.genes, partner.genes));
    }

    public double getEnergy(){
        return this.energy;
    }

    public int getAge() {
        return age;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public Genes getGenes() {
        return genes;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "position=" + position +
                ", orientation=" + orientation +
                ", energy=" + energy +
                '}';
    }

}
