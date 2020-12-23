package pl.edu.agh.po.Simulation.Interfaces;

import pl.edu.agh.po.Simulation.GeneralClasses.Vector2d;

import java.util.Optional;


/**
 * The interface responsible for interacting with the map of the world.
 * Assumes that Vector2d and MoveDirection classes are defined.
 *
 * @author apohllo
 * modified by Byczong
 */
public interface IWorldMap {

    /**
     * Place an element on the map.
     *
     * @param element
     *            The element to place on the map.
     */
    void place(IMapElement element);

    /**
     * Check if the position is free.
     *
     * @param position
     *            The position on the map.
     * @return true if there is no elements at a given position.
     */
    boolean isFree(Vector2d position);

    /**
     * Get an object at the given position.
     *
     * @param position
     *            The position of the object.
     * @return Object or empty Optional if the position is not occupied.
     */
    Optional<IMapElement> objectAt(Vector2d position);

    /**
     * Get folded Vector2d position.
     *
     * @param position
     *            The position of the object.
     * @return Position after moving (looped or normal).
     */
    Vector2d foldPosition(Vector2d position);

    /**
     * Take steps to update the total energy of the map.
     *
     * @param value to know how energy changed.
     */
    void energyChanged(double value);
}