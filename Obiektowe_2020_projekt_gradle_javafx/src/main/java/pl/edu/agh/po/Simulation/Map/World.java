package pl.edu.agh.po.Simulation.Map;

import pl.edu.agh.po.Simulation.MapElements.Animal;
import pl.edu.agh.po.Simulation.MapElements.Grass;

import java.util.LinkedList;

public class World {
    public static void main(String[] args) {
        try {
            LoopedJungleMap map = new LoopedJungleMap(10, 10, 0.3, 1.0, 2.0, 15.0);
            map.placeAnimalsRandomly(8);
            map.plantGrass();

            for(LinkedList<Animal> animalsOnPosition : map.getAnimalHashMap().values()) {
                if(animalsOnPosition != null)
                    if(animalsOnPosition.size() > 0) {
                        for (Animal animal : animalsOnPosition)
                            System.out.println(animal);
                    }
            }

            for(Grass grass : map.getGrassHashMap().values())
                System.out.println("Grass on " + grass.getPosition());

            System.out.println();


            int days = 16;
            while(days > 0) {
                map.moveAnimals();
                map.letAnimalsEatGrass();
                map.letAnimalsReproduce();
                map.plantGrass();
                days--;
            }

            for(LinkedList<Animal> animalsOnPosition : map.getAnimalHashMap().values()) {
                if(animalsOnPosition != null)
                    if(animalsOnPosition.size() > 0) {
                        for (Animal animal : animalsOnPosition)
                            System.out.println(animal);
                    }
            }

            for(Grass grass : map.getGrassHashMap().values())
                System.out.println("Grass on " + grass.getPosition());
        }

        catch (IllegalArgumentException ex) {
            System.out.println(ex + "\nAn illegal argument has been passed over. Ending all processes.");
            System.exit(1);
        }
    }
}

// genotyp dominujący -> gen a nie genotyp ( najpopulatniejszy obrot / 3 najpopularniejsze + ich szansa procentowa)
// kazde zwierze z kazdej epoki sprawdzamy

