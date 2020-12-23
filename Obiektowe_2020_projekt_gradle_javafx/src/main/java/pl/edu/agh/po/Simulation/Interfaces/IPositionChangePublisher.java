package pl.edu.agh.po.Simulation.Interfaces;


import pl.edu.agh.po.Simulation.GeneralClasses.Vector2d;

/**
 *
 * The interface responsible for publishing
 * potential position changes to observers,
 * as well as adding and removing observers
 *
 * @author Byczong
 *
 */

public interface IPositionChangePublisher {

    /**
     * Add specified object to the collection of observers.
     *
     * @param observer to specify which object should be given information about publisher.
     */
    void addObserver(IPositionChangeObserver observer);

    /**
     * Remove specified object from the collection of observers.
     *
     * @param observer to specify which object should cease to be given information about publisher.
     */
    void removeObserver(IPositionChangeObserver observer);

    /**
     * Take steps to ensure cohesion regarding the element's change of position.
     *
     * @param movedElement to specify that this element changed its position.
     *
     * @param oldPosition previous position of this element.
     *
     * @param newPosition current position of this element.
     */
    void positionChanged(IMapElement movedElement, Vector2d oldPosition, Vector2d newPosition);

    /**
     * Take steps to ensure cohesion regarding the animal's death.
     *
     */
    void energyChangedTo0();
}