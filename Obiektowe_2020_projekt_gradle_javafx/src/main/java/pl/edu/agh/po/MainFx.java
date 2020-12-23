package pl.edu.agh.po;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import pl.edu.agh.po.Simulation.GeneralClasses.Vector2d;
import pl.edu.agh.po.Simulation.Map.LoopedJungleMap;
import pl.edu.agh.po.Simulation.MapElements.Animal;

import java.util.Arrays;


public class MainFx extends Application {

    private int width;
    private int height;
    private double jungleRatio;
    private int moveEnergy;
    private int grassEnergy;
    private int initialAnimalEnergy;

    private LoopedJungleMap map;
    private int lowBrownessIndicator;
    private int highBrownessIndicator;

    private static final int WINDOW_HEIGHT_PX = 1000;
    private int gridSize;
    private int tileSize;
    private Vector2d bottomLeftJungle;
    private Vector2d topRightJungle;
    private Tile[][] grid;
    Stage window;
    Scene initialScene, setupSimulationScene;

    public static void main(String[] args) {
        launch(args);
    }



    private class Tile extends StackPane {
        private int browness;
        private final Vector2d position;
        public boolean hasAnimal;
        public boolean hasGrass;
        private final Color backgroundColor;
        private final Rectangle border = new Rectangle(tileSize - 2, tileSize - 2);

        private Tile(int x, int y) {
            this.position = new Vector2d(x, y);
            this.hasAnimal = false;
            if(isInJungle(position))
                this.backgroundColor = Color.FORESTGREEN;
            else
                this.backgroundColor = Color.YELLOWGREEN;
            border.setStroke(backgroundColor);

            border.setFill(backgroundColor);
            browness = 0;

            getChildren().add(border);

            setTranslateX(x * tileSize);
            setTranslateY(y * tileSize);

            setOnMouseClicked(event -> {
                if(hasAnimal)
                    showStats();
                else if(hasGrass)
                    showGrass();
            });
        }

        private void changeColorTo(int newBrowness) {
            if(newBrowness < -1 || newBrowness > 3)
                throw new IllegalArgumentException("Wrong new browness on tile: " + this.position.toString());
            else {
                browness = newBrowness;
                switch (browness) {
                    case -1 -> border.setFill(Color.LAWNGREEN);
                    case 0 -> border.setFill(backgroundColor);
                    case 1 -> border.setFill(Color.SANDYBROWN);
                    case 2 -> border.setFill(Color.SADDLEBROWN);
                    case 3 -> border.setFill(Color.BROWN);
                }
            }
        }

        private void showStats() {
            String message1, message2;
            if(map.objectAt(position).isPresent()) {
                message1 = "Animal's genes: " + Arrays.toString(((Animal) map.objectAt(position).get()).getGenes().getGenesArray());
                message2 = "Number of children: " + ((Animal) map.objectAt(position).get()).getNumberOfChildren();
            }
            else {
                message1 = "-";
                message2 = "-";
            }
            AlertBox.display("Stats for animal at " + this.position.toString(), message1, message2);
        }

        private void showGrass() {
            String message1, message2;
            message1 = "Grass is not exceptionally interesting.";
            message2 = "";
            AlertBox.display("Stats for grass at " + this.position.toString(), message1, message2);
        }
    }

    private Parent createGrid() {
        Pane root = new Pane();
        root.setPrefSize(gridSize, gridSize);
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                Tile tile = new Tile(x, y);
                grid[x][y] = tile;
                root.getChildren().add(tile);
            }
        }
        return root;
    }

    private void refreshTiles() {
        for(int y = 0; y < height; y++)
            for(int x = 0; x < width; x++) {
                Vector2d position = new Vector2d(x, y);
                Tile tile = grid[x][y];
                if(map.objectAt(position).isEmpty()) {
                    tile.changeColorTo(0);
                    tile.hasAnimal = false;
                    tile.hasGrass = false;
                }
                else {
                    if(map.objectAt(position).get().isEdible()) {
                        tile.changeColorTo(-1);
                        tile.hasAnimal = false;
                        tile.hasGrass = true;
                    }
                    else {
                        int energy = (int) ((Animal) map.objectAt(position).get()).getEnergy();
                        if(energy < lowBrownessIndicator)
                            tile.changeColorTo(1);
                        else if(energy < highBrownessIndicator)
                            tile.changeColorTo(2);
                        else
                            tile.changeColorTo(3);
                        tile.hasAnimal = true;
                        tile.hasGrass = false;
                    }
                }
            }
    }

    private void updateStats(GridPane leftStatistics) {
        Label statNumberOfAnimalsValue = new Label(Integer.toString(map.getNumberOfAnimals()));
        leftStatistics.getChildren().removeIf(node -> (GridPane.getRowIndex(node) == 0 || GridPane.getRowIndex(node) == null) && GridPane.getColumnIndex(node) == 1);
        leftStatistics.add(statNumberOfAnimalsValue, 1, 0);

        Label statNumberOfGrassesValue = new Label(Integer.toString(map.getNumberOfGrasses()));
        leftStatistics.getChildren().removeIf(node -> GridPane.getRowIndex(node) == 1 && GridPane.getColumnIndex(node) == 1);
        leftStatistics.add(statNumberOfGrassesValue, 1, 1);

        Label statAverageEnergyValue = new Label(Double.toString(map.getAverageEnergy()));
        leftStatistics.getChildren().removeIf(node -> GridPane.getRowIndex(node) == 2 && GridPane.getColumnIndex(node) == 1);
        leftStatistics.add(statAverageEnergyValue, 1, 2);

        Label statAverageLifetimeValue = new Label(Double.toString(map.getAverageLifetime()));
        leftStatistics.getChildren().removeIf(node -> GridPane.getRowIndex(node) == 3 && GridPane.getColumnIndex(node) == 1);
        leftStatistics.add(statAverageLifetimeValue, 1, 3);

        Label statAverageNumberOfChildrenValue = new Label(Double.toString(map.getAverageNumberOfChildren()));
        leftStatistics.getChildren().removeIf(node -> GridPane.getRowIndex(node) == 4 && GridPane.getColumnIndex(node) == 1);
        leftStatistics.add(statAverageNumberOfChildrenValue, 1, 4);

        Label statDominantGeneValue = new Label(Integer.toString(map.getDominantGene()));
        leftStatistics.getChildren().removeIf(node -> GridPane.getRowIndex(node) == 5 && GridPane.getColumnIndex(node) == 1);
        leftStatistics.add(statDominantGeneValue, 1, 5);

        Label statChanceDominantGeneValue = new Label(Double.toString(map.getDominantGeneChance()));
        leftStatistics.getChildren().removeIf(node -> GridPane.getRowIndex(node) == 6 && GridPane.getColumnIndex(node) == 1);
        leftStatistics.add(statChanceDominantGeneValue, 1, 6);
    }

    public void nextDay(GridPane leftStatistics) {
        map.moveAnimals();
        map.letAnimalsEatGrass();
        map.letAnimalsReproduce();
        map.plantGrass();
        refreshTiles();
        updateStats(leftStatistics);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("Evolution simulator");


        // Initial scene

        GridPane initialLayoutGrid = new GridPane();
        initialLayoutGrid.setAlignment(Pos.CENTER);
        initialLayoutGrid.setHgap(10);
        initialLayoutGrid.setVgap(8);
        initialLayoutGrid.setPadding(new Insets(25, 25, 25, 25));

        Text initialSceneTitle = new Text("Select initial parameters and start simulation");
        initialSceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
        initialLayoutGrid.add(initialSceneTitle, 0, 0, 2, 1);

        Label insertWidth = new Label("Map width:");
        initialLayoutGrid.add(insertWidth, 0, 1);
        TextField widthTextField = new TextField();
        widthTextField.setPromptText("e.g. 20");
        initialLayoutGrid.add(widthTextField, 1, 1);

        Label insertHeight = new Label("Map height:");
        initialLayoutGrid.add(insertHeight, 0, 2);
        TextField heightTextField = new TextField();
        heightTextField.setPromptText("e.g. 15");
        initialLayoutGrid.add(heightTextField, 1, 2);

        Label insertJungleRatio = new Label("Jungle-steppe ratio:");
        initialLayoutGrid.add(insertJungleRatio, 0, 3);
        TextField ratioTextField = new TextField();
        ratioTextField.setPromptText("e.g. 0.3");
        initialLayoutGrid.add(ratioTextField, 1, 3);

        Label insertMoveEnergy = new Label("Move energy:");
        initialLayoutGrid.add(insertMoveEnergy, 0, 4);
        TextField moveTextField = new TextField();
        moveTextField.setPromptText("e.g. 1");
        initialLayoutGrid.add(moveTextField, 1, 4);

        Label insertGrassEnergy = new Label("Grass energy:");
        initialLayoutGrid.add(insertGrassEnergy, 0, 5);
        TextField grassTextField = new TextField();
        grassTextField.setPromptText("e.g. 10");
        initialLayoutGrid.add(grassTextField, 1, 5);

        Label insertAnimalEnergy = new Label("Initial animal energy:");
        initialLayoutGrid.add(insertAnimalEnergy, 0, 6);
        TextField animalTextField = new TextField();
        animalTextField.setPromptText("e.g. 30");
        initialLayoutGrid.add(animalTextField, 1, 6);


        // Initialization on clicking the startButton

        Button startButton = new Button("Load from above");
        startButton.setOnAction(event -> {
            this.width = getInt(widthTextField);
            this.height = getInt(heightTextField);
            this.jungleRatio = getDouble(ratioTextField);
            this.moveEnergy = getInt(moveTextField);
            this.grassEnergy = getInt(grassTextField);
            this.initialAnimalEnergy = getInt(animalTextField);

            this.gridSize = WINDOW_HEIGHT_PX - 100;
            this.tileSize = gridSize / Math.max(width, height);
            grid = new Tile[width][height];
            map = new LoopedJungleMap(width, height, jungleRatio, moveEnergy, grassEnergy, initialAnimalEnergy);
            this.topRightJungle = map.getUpperRightJungle();
            this.bottomLeftJungle = map.getLowerLeftJungle();
            this.highBrownessIndicator = initialAnimalEnergy;
            this.lowBrownessIndicator = initialAnimalEnergy / 2;

            int numberOfAnimalsAtStart = Math.max(2, (width * height) / 5 );
            map.placeAnimalsRandomly(numberOfAnimalsAtStart);
            map.plantGrass();

            Parent simulationLayout = createGrid();
            refreshTiles();

            GridPane leftStatistics = new GridPane();
            leftStatistics.setHgap(10);
            leftStatistics.setVgap(8);
            leftStatistics.setPadding(new Insets(25, 25, 25, 25));

            Label statNumberOfAnimals = new Label("Number of animals:");
            leftStatistics.add(statNumberOfAnimals, 0, 0);
            Label statNumberOfAnimalsValue = new Label(Integer.toString(map.getNumberOfAnimals()));
            leftStatistics.add(statNumberOfAnimalsValue, 1, 0);

            Label statNumberOfGrasses = new Label("Number of grasses:");
            leftStatistics.add(statNumberOfGrasses, 0, 1);
            Label statNumberOfGrassesValue = new Label(Integer.toString(map.getNumberOfGrasses()));
            leftStatistics.add(statNumberOfGrassesValue, 1, 1);

            Label statAverageEnergy = new Label("Average energy:");
            leftStatistics.add(statAverageEnergy, 0, 2);
            Label statAverageEnergyValue = new Label(Double.toString(map.getAverageEnergy()));
            leftStatistics.add(statAverageEnergyValue, 1, 2);

            Label statAverageLifetime = new Label("Average (dead) lifetime:");
            leftStatistics.add(statAverageLifetime, 0, 3);
            Label statAverageLifetimeValue = new Label("0.0");
            leftStatistics.add(statAverageLifetimeValue, 1, 3);

            Label statAverageNumberOfChildren = new Label("Average number of children:");
            leftStatistics.add(statAverageNumberOfChildren, 0, 4);
            Label statAverageNumberOfChildrenValue = new Label("0.0");
            leftStatistics.add(statAverageNumberOfChildrenValue, 1, 4);

            Label statDominantGene = new Label("Dominant gene:");
            leftStatistics.add(statDominantGene, 0, 5);
            Label statDominantGeneValue = new Label(Integer.toString(map.getDominantGene()));
            leftStatistics.add(statDominantGeneValue, 1, 5);

            Label statChanceDominantGene = new Label("Average chance of dominant direction:");
            leftStatistics.add(statChanceDominantGene, 0, 6);
            Label statChanceDominantGeneValue = new Label(Double.toString(map.getDominantGeneChance()));
            leftStatistics.add(statChanceDominantGeneValue, 1, 6);


            HBox bottomButtons = new HBox();
            Button nextButton = new Button("Next day");
            Button goBackButton = new Button("Restart");
            goBackButton.setOnAction(event1 -> window.setScene(initialScene));
            bottomButtons.setPadding(new Insets(25, 25, 25, 25));
            bottomButtons.setSpacing(100);
            bottomButtons.getChildren().addAll(nextButton, goBackButton);


            BorderPane borderPane = new BorderPane();
            borderPane.setLeft(leftStatistics);
            borderPane.setBottom(bottomButtons);
            borderPane.setCenter(simulationLayout);
            nextButton.setOnAction(event1 -> nextDay(leftStatistics));

            setupSimulationScene = new Scene(borderPane);
            window.setScene(setupSimulationScene);
        });


        initialLayoutGrid.add(startButton, 0, 7);
        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> window.close());
        initialLayoutGrid.add(closeButton, 1, 7);

        initialScene = new Scene(initialLayoutGrid, 400, 320);

        window.setScene(initialScene);
        window.show();
    }

    private int getInt(TextField input) {
        String message = input.getText();
        try {
            int intNumber = Integer.parseInt(message);
            if(intNumber <= 0)
                throw new IllegalArgumentException("Given number: " + intNumber + " is negative.");
            else if(intNumber > 200)
                throw new IllegalArgumentException("Given number: " + intNumber + " is too large.");
            return intNumber;
        }
        catch (IllegalArgumentException e) {
            System.out.println("Error concerning input: \"" + message + "\" " + e);
            window.close();
            return -1;
        }
    }

    private double getDouble(TextField input) {
        String message = input.getText();
        try {
            double doubleNumber = Double.parseDouble(message);
            if(doubleNumber < 0)
                throw new IllegalArgumentException("Given number: " + doubleNumber + " is negative.");
            else if(doubleNumber > 1)
                throw new IllegalArgumentException("Given number: " + doubleNumber + " is larger than 1.");
            return doubleNumber;
        }
        catch (IllegalArgumentException e) {
            System.out.println("Error concerning input: \"" + message + "\" " + e);
            window.close();
            return -1;
        }
    }

    private boolean isInJungle(Vector2d position) {
        return position.precedes(topRightJungle) && position.follows(bottomLeftJungle);
    }

}
