package pl.edu.agh.po.Simulation.Interfaces;



import pl.edu.agh.po.Simulation.GeneralClasses.Vector2d;
import pl.edu.agh.po.Simulation.MapElements.Animal;

/**
 *
 * The interface responsible for observing potential position changes.
 *
 * @author Byczong
 *
 */
public interface IPositionChangeObserver {

    /**
     * Take steps to ensure cohesion regarding the element's change of position.
     *
     * @param movedElement to know which element changed its position.
     *
     * @param oldPosition previous position of the movedElement.
     *
     * @param newPosition current position of the movedElement.
     */
    void positionChanged(IMapElement movedElement, Vector2d oldPosition, Vector2d newPosition);

    /**
     * Take steps to remove dead animal from the map.
     *
     * @param animal to know which animal died.
     */
    void animalDied(Animal animal);

}