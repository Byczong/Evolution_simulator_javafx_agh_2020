package pl.edu.agh.po.Simulation.Map;

import pl.edu.agh.po.Simulation.GeneralClasses.Vector2d;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class AvailableGrassPositions {

    private final Set<Vector2d> jungleSet;
    private final Set<Vector2d> steppeSet;
    private final LoopedJungleMap map;


    AvailableGrassPositions(LoopedJungleMap map, Vector2d lowerLeft, Vector2d upperRight, Vector2d lowerLeftJungle, Vector2d upperRightJungle){
        this.map = map;
        jungleSet = new HashSet<>();
        steppeSet = new HashSet<>();
        initializeSets(lowerLeft, upperRight, lowerLeftJungle, upperRightJungle);
    }

    private void initializeSets(Vector2d lowerLeft, Vector2d upperRight, Vector2d lowerLeftJungle, Vector2d upperRightJungle){
        for(int x = lowerLeftJungle.getX(); x <= upperRightJungle.getX(); x++)
            for(int y = lowerLeftJungle.getY(); y <= upperRightJungle.getY(); y++)
                jungleSet.add(new Vector2d(x, y));

        for(int x = lowerLeft.getX(); x <= upperRight.getX(); x++)
            for(int y = lowerLeft.getY(); y <= upperRight.getY(); y++) {
                Vector2d currentPosition = new Vector2d(x, y);
                if(!jungleSet.contains(currentPosition))
                    steppeSet.add(currentPosition);
            }
    }

    public void grassPlantedJungle(Vector2d position){
        jungleSet.remove(position);
    }

    public void grassPlantedSteppe(Vector2d position){
        steppeSet.remove(position);
    }

    public void grassEaten(Vector2d position) {
        if(map.getLowerLeftJungle().precedes(position) && map.getUpperRightJungle().follows(position))
            grassEatenJungle(position);
        else if (LoopedJungleMap.getLowerLeft().precedes(position) && map.getUpperRight().follows(position))
            grassEatenSteppe(position);
        else
            throw new IllegalArgumentException("Grass eaten from out of the map, position: " + position);
    }

    public void grassEatenJungle(Vector2d position){
        jungleSet.add(position);
    }

    public void grassEatenSteppe(Vector2d position){
        steppeSet.add(position);
    }

    public Optional<Vector2d> getAvailableJunglePosition() {
        if(jungleSet.size() > 0) {
            int randIndex = ThreadLocalRandom.current().nextInt(0, jungleSet.size());
            int firstIndex = randIndex;
            Vector2d randomPosition;
            do {
                randomPosition = (Vector2d) jungleSet.toArray()[randIndex];
                randIndex = (randIndex + 1) % jungleSet.size();
                if(firstIndex == randIndex) {
                    randIndex = (randIndex + 1) % jungleSet.size();
                    randomPosition = (Vector2d) jungleSet.toArray()[randIndex];
                    if(map.isFree(randomPosition))
                        return Optional.of(randomPosition);
                    return Optional.empty();
                }
            }
            while(!map.isFree(randomPosition));

            return Optional.of(randomPosition);
        }
        return Optional.empty();
    }

    public Optional<Vector2d> getAvailableSteppePosition() {
        if(steppeSet.size() > 0) {
            int randIndex = ThreadLocalRandom.current().nextInt(0, steppeSet.size());
            int firstIndex = randIndex;
            Vector2d randomPosition;
            do {
                randomPosition = (Vector2d) steppeSet.toArray()[randIndex];
                randIndex = (randIndex + 1) % steppeSet.size();
                if(firstIndex == randIndex) {
                    randIndex = (randIndex + 1) % steppeSet.size();
                    randomPosition = (Vector2d) steppeSet.toArray()[randIndex];
                    if(map.isFree(randomPosition))
                        return Optional.of(randomPosition);
                    return Optional.empty();
                }
            }
            while(!map.isFree(randomPosition));

            return Optional.of(randomPosition);
        }
        return Optional.empty();
    }
}
