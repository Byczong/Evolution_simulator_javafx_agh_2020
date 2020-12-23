package pl.edu.agh.po.Simulation.Interfaces;


import pl.edu.agh.po.Simulation.GeneralClasses.Vector2d;

/**
 *
 * The interface responsible for interaction of map elements like Animals or Grasses.
 *
 * @author Byczong
 *
 */

public interface IMapElement {

    /**
     *
     * @return Position of the element.
     *
     */
    Vector2d getPosition();

    /**
     *
     * Move this map element forward.
     * If it can't move -> empty method.
     *
     */
    void move();

    /**
     *
     * Determine if an element is edible.
     * If it's a grass -> edible.
     * If it's an animal -> not edible.
     *
     */
    boolean isEdible();
}
