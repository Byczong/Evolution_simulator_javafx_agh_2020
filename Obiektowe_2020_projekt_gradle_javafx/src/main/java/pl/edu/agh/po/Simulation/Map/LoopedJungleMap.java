package pl.edu.agh.po.Simulation.Map;

import pl.edu.agh.po.Simulation.GeneralClasses.Vector2d;
import pl.edu.agh.po.Simulation.Interfaces.IMapElement;
import pl.edu.agh.po.Simulation.Interfaces.IPositionChangeObserver;
import pl.edu.agh.po.Simulation.Interfaces.IWorldMap;
import pl.edu.agh.po.Simulation.MapElements.Animal;
import pl.edu.agh.po.Simulation.MapElements.Grass;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class LoopedJungleMap implements IWorldMap, IPositionChangeObserver {

    public final int width;
    public final int height;
    public final int jungleWidth;
    public final int jungleHeight;
    private final Vector2d upperRight;
    private final static Vector2d LOWER_LEFT = new Vector2d(0, 0);
    private final Vector2d lowerLeftJungle;
    private final Vector2d upperRightJungle;
    private final AvailableGrassPositions availableGrassPositions;

    private final double grassEnergy;
    private final double moveEnergy;
    private final double initialAnimalEnergy;
    private final double energyRequiredToReproduce;


    private double totalEnergy = 0;
    private int deadAnimals = 0;
    private long deadAnimalsLifetime = 0;
    private int totalNumberOfChildren = 0;
    private final int[] numbersOfGenes;

    private final List<Animal> animals = new LinkedList<>();
    private final Map<Vector2d,  LinkedList<Animal>> animalHashMap = new HashMap<>();
    private final List<Grass> grasses = new LinkedList<>();
    private final Map<Vector2d, Grass> grassHashMap = new HashMap<>();

    public LoopedJungleMap(int width, int height, double jungleRatio, double moveEnergy, double grassEnergy, double initialAnimalEnergy) {
        this.width = width;
        this.height = height;
        this.jungleWidth = (int)(jungleRatio * width);
        this.jungleHeight = (int)(jungleRatio * height);
        this.initialAnimalEnergy = initialAnimalEnergy;
        this.energyRequiredToReproduce = initialAnimalEnergy / 2;
        this.moveEnergy = moveEnergy;
        this.grassEnergy = grassEnergy;
        this.upperRight = new Vector2d(width - 1, height - 1);
        createJungle();
        Vector2d[] jungleBoundaries = createJungle();
        this.lowerLeftJungle = jungleBoundaries[0];
        this.upperRightJungle = jungleBoundaries[1];
        availableGrassPositions = new AvailableGrassPositions(this, LOWER_LEFT, upperRight, lowerLeftJungle, upperRightJungle);
        this.numbersOfGenes = new int[] {0, 0, 0, 0, 0, 0, 0, 0};
    }

    private Vector2d[] createJungle() {
        int bottomLeftX = 0;
        int bottomLeftY = 0;
        int topRightX = width - 1;
        int topRightY = height - 1;
        for (int i = 0; i < (width - jungleWidth); i++) {
            if (i % 2 == 0)
                bottomLeftX++;
            else
                topRightX--;
        }
        for (int i = 0; i < (height - jungleHeight); i++) {
            if (i % 2 == 0)
                bottomLeftY++;
             else
                topRightY--;
        }
        return new Vector2d[]{new Vector2d(bottomLeftX, bottomLeftY), new Vector2d(topRightX, topRightY)};
    }

    @Override
    public Vector2d foldPosition(Vector2d position) {
        int X, Y;
        if (position.x < LOWER_LEFT.x)
            X = (width - Math.abs(position.x % width)) % width;
        else
            X = Math.abs(position.x % width);
        if (position.y < LOWER_LEFT.y)
            Y = (height - Math.abs(position.y % height)) % height;
        else
            Y = Math.abs(position.y % height);
        return new Vector2d(X, Y);
    }

    @Override
    public boolean isFree(Vector2d position) {
        return objectAt(position).isEmpty();
    }

    @Override
    public Optional<IMapElement> objectAt(Vector2d position) {
        position = foldPosition(position);
        if(animalHashMap.containsKey(position)) {
            LinkedList<Animal> result;
            result = animalHashMap.get(position);
            if(result == null || result.size() == 0)
                return Optional.empty();
            else
                return Optional.of(result.getFirst());
        }
        else if(grassHashMap.containsKey(position))
            return Optional.of(grassHashMap.get(position));
        return Optional.empty();
    }

    @Override
    public void positionChanged(IMapElement movedElement, Vector2d oldPos, Vector2d newPos) {
        Vector2d oldPosition = foldPosition(oldPos);
        Vector2d newPosition = foldPosition(newPos);
        removeAnimalFromHashMap((Animal) movedElement, oldPosition);
        addAnimalToHashMap((Animal) movedElement, newPosition);
    }

    @Override
    public void place(IMapElement element) {
        Vector2d position = foldPosition(element.getPosition());
        if(element.isEdible()) {
            if(isFree(position)) {
                grassHashMap.put(position, (Grass) element);
                grasses.add((Grass) element);
            }
        }
        else {
            animals.add((Animal) element);
            addAnimalToHashMap((Animal) element, position);
            ((Animal) element).addObserver(this);

            int[] animalGenes = ((Animal) element).getGenes().getGenesArray();
            for (int animalGene : animalGenes)
                numbersOfGenes[animalGene]++;
        }
    }

    private void addAnimalToHashMap(Animal animal, Vector2d pos) {
        Vector2d position = foldPosition(pos);
        LinkedList<Animal> animalsOnPosition = animalHashMap.get(position);
        if(animalsOnPosition == null) {
            LinkedList<Animal> initialList = new LinkedList<>();
            initialList.add(animal);
            animalHashMap.put(position, initialList);
        }
        else
            animalsOnPosition.add(animal);
    }

    private void removeAnimalFromHashMap(Animal animal, Vector2d pos) {
        Vector2d position = foldPosition(pos);
        if(animalHashMap.get(position) != null) {
            animalHashMap.get(position).remove(animal);
            if(animalHashMap.get(position).size() == 0)
                animalHashMap.remove(position);
        }
        else
            throw new IllegalArgumentException("No animals on " + position);
    }

    /**
     * Below, there is an implementation of
     * simulation of every day on the LoopedJungleMap.
     */

    @Override
    public void animalDied(Animal animal) {
        deadAnimals++;
        deadAnimalsLifetime += animal.getAge();
        removeAnimalFromHashMap(animal, animal.getPosition());
        animals.remove(animal);
        animal.removeObserver(this);
    }

    public void moveAnimals() {
        LinkedList<Animal> currentAnimals = new LinkedList<>(animals);
        for(Animal animal : currentAnimals){
            animal.rotate();
            animal.move();
            animal.changeEnergy(-moveEnergy);
        }
    }

    public void letAnimalsEatGrass() {
        LinkedList<Grass> currentGrasses = new LinkedList<>(grasses);
        for(Grass grass : currentGrasses){
            Vector2d position = grass.getPosition();
            LinkedList<Animal> animalsOnPosition = animalHashMap.get(position);
            if(animalsOnPosition != null)
                if(animalsOnPosition.size() > 0) {
                    double maxEnergy = 0;
                    LinkedList<Animal> strongestAnimals = new LinkedList<>();
                    for (Animal animal : animalsOnPosition) {
                        if (animal.getEnergy() > maxEnergy) {
                            maxEnergy = animal.getEnergy();
                            strongestAnimals.clear();
                            strongestAnimals.add(animal);
                        } else if (animal.getEnergy() == maxEnergy) {
                            strongestAnimals.add(animal);
                        }
                    }
                    for (Animal animal : strongestAnimals)
                        animal.changeEnergy(grassEnergy / strongestAnimals.size());

                    grassHashMap.remove(position);
                    grasses.remove(grass);
                    availableGrassPositions.grassEaten(position);
                }
        }
    }

    public void letAnimalsReproduce() {
        LinkedList<LinkedList<Animal>> animalHashMapToList= new LinkedList<>(animalHashMap.values());
        for(LinkedList<Animal> animalsOnPosition : animalHashMapToList) {
            if(animalsOnPosition != null)
                if(animalsOnPosition.size() > 1) {
                    Vector2d childPosition = findChildPosition(animalsOnPosition.getFirst().getPosition());
                    double maxEnergy = 0.0;
                    double secondMaxEnergy = -1.0;
                    LinkedList<Animal> strongestAnimals = new LinkedList<>();
                    LinkedList<Animal> secondStrongestAnimals = new LinkedList<>();
                    int numberOfEagerAnimals = 0;
                    for (Animal animal : animalsOnPosition)
                        if(animal.getEnergy() >= energyRequiredToReproduce) {
                            numberOfEagerAnimals++;
                            if (animal.getEnergy() > maxEnergy) {
                                secondMaxEnergy = maxEnergy;
                                maxEnergy = animal.getEnergy();
                                secondStrongestAnimals.clear();
                                secondStrongestAnimals.addAll(strongestAnimals);
                                strongestAnimals.clear();
                                strongestAnimals.add(animal);
                            }
                            else if (animal.getEnergy() == maxEnergy)
                                strongestAnimals.add(animal);
                            else if (animal.getEnergy() < maxEnergy && animal.getEnergy() > secondMaxEnergy) {
                                secondMaxEnergy = animal.getEnergy();
                                secondStrongestAnimals.clear();
                                secondStrongestAnimals.add(animal);
                            }
                            else if (animal.getEnergy() == secondMaxEnergy)
                                secondStrongestAnimals.add(animal);
                        }
                    if(numberOfEagerAnimals < 2)
                        break;
                    else if(strongestAnimals.size() > 1) {
                        Animal partner1 = strongestAnimals.get(ThreadLocalRandom.current().nextInt(0, strongestAnimals.size()));
                        Animal partner2 = partner1;
                        while(partner1 == partner2)
                            partner2 = strongestAnimals.get(ThreadLocalRandom.current().nextInt(0, strongestAnimals.size()));
                        Animal child = partner1.reproduce(partner2, childPosition);
                        place(child);
                        totalNumberOfChildren += 1;
                    }
                    else {
                        Animal partner = secondStrongestAnimals.get(ThreadLocalRandom.current().nextInt(0, secondStrongestAnimals.size()));
                        Animal child = strongestAnimals.get(0).reproduce(partner, childPosition);
                        place(child);
                    }
                }
        }
    }

    private Vector2d findChildPosition(Vector2d parentsPosition) {
        LinkedList<Vector2d> availableFreePositions = new LinkedList<>();
        LinkedList<Vector2d> allFeasiblePositions = new LinkedList<>();
        int x = parentsPosition.x;
        int y = parentsPosition.y;
        for(int xi = x - 1; xi <= x + 1; xi++)
            for(int yi = y - 1; yi <= y + 1; yi++) {
                Vector2d currentPosition = foldPosition(new Vector2d(xi, yi));
                if(!currentPosition.equals(parentsPosition))
                    allFeasiblePositions.add(currentPosition);
                if(!currentPosition.equals(parentsPosition) && isFree(currentPosition))
                    availableFreePositions.add(currentPosition);
            }
        if(availableFreePositions.size() > 0)
            return availableFreePositions.get(ThreadLocalRandom.current().nextInt(0, availableFreePositions.size()));
        else
            return allFeasiblePositions.get(ThreadLocalRandom.current().nextInt(0, allFeasiblePositions.size()));
    }

    public void plantGrass() {
        Vector2d junglePlantPosition, steppePlantPosition;
        Optional<Vector2d> jungleOptional = availableGrassPositions.getAvailableJunglePosition(),
                steppeOptional = availableGrassPositions.getAvailableSteppePosition();
        if(jungleOptional.isPresent()) {
            junglePlantPosition = new Vector2d(jungleOptional.get());
            Grass newGrass = new Grass(junglePlantPosition);
            place(newGrass);
            availableGrassPositions.grassPlantedJungle(junglePlantPosition);
        }
        if(steppeOptional.isPresent()) {
            steppePlantPosition = new Vector2d(steppeOptional.get());
            Grass newGrass = new Grass(steppePlantPosition);
            place(newGrass);
            availableGrassPositions.grassPlantedSteppe(steppePlantPosition);
        }
    }

    @Override
    public void energyChanged(double value) {
        totalEnergy += value;
    }

    public void placeAnimalsRandomly(int number) {
        for(int i = 0; i < number; i++) {
            Animal animal = new Animal(this, getRandomPosition(), initialAnimalEnergy);
            place(animal);
        }
    }

    private Vector2d getRandomPosition() {
        int x = ThreadLocalRandom.current().nextInt(0, width);
        int y = ThreadLocalRandom.current().nextInt(0, height);
        return new Vector2d(x, y);
    }

    public String toString() {
        return "Looped Jungle Map ( ͡° ͜ʖ ͡°)";
    }

    public Vector2d getUpperRight() {
        return upperRight;
    }

    public static Vector2d getLowerLeft() {
        return LOWER_LEFT;
    }

    public Vector2d getLowerLeftJungle() {
        return lowerLeftJungle;
    }

    public Vector2d getUpperRightJungle() {
        return upperRightJungle;
    }

    public Map<Vector2d, LinkedList<Animal>> getAnimalHashMap() {
        return animalHashMap;
    }

    public Map<Vector2d, Grass> getGrassHashMap() {
        return grassHashMap;
    }

    public double getAverageLifetime() {
        if(deadAnimals > 0)
            return ((double) deadAnimalsLifetime) / ((double) deadAnimals);
        else
            return 0.0;
    }

    public double getAverageEnergy() {
        if(animals.size() == 0)
            return 0.0;
        else
            return totalEnergy / animals.size();
    }

    public double getAverageNumberOfChildren() {
        return ((double) (2 * totalNumberOfChildren)) / ( (double) (animals.size() + deadAnimals) );
    }

    public int getNumberOfAnimals() {
        return animals.size();
    }

    public int getNumberOfGrasses() {
        return grasses.size();
    }

    public int getDominantGene() {
        int maxValue = -1, i = -1;
        for(int j = 0; j < numbersOfGenes.length; j++) {
            if(numbersOfGenes[j] > maxValue) {
                maxValue = numbersOfGenes[j];
                i = j;
            }
        }
        return i;
    }

    public double getDominantGeneChance() {
        int index = getDominantGene();
        int sum = 0;
        for(int number : numbersOfGenes)
            sum += number;

        return ((double) numbersOfGenes[index]) / ((double) sum);
    }

}
