package com.example.hashtable;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Timer;

public class HashTableGUI extends Application {
    private int tableSize = 10;
    private ArrayList<String>[] table;
    private int count;
    private TableCanvas tableCanvas;
    private Timer animationTimer;


    public HashTableGUI() {
        this(10);
    }

    public HashTableGUI(int size) {
        this.tableSize = size;
        this.table = new ArrayList[this.tableSize];
        for (int i = 0; i < this.tableSize; i++) {
            this.table[i] = new ArrayList<>();
        }
        this.count = 0;
    }

    private int hashFunction(String key) {
        int hashValue = 0;
        int n = key.length();
        for (int i = 0; i < n; i++) {
            hashValue = (hashValue + key.charAt(i)) * 31;
        }
        return Math.abs(hashValue % this.tableSize);
    }

    public void add(String key) {
        int index = hashFunction(key);
        if (!table[index].contains(key)) {
            table[index].add(key);
            tableCanvas.startAnimation(key, index);
            count++;
            System.out.println("Added successfully: " + key);
        } else {
            System.out.println("Key already exists: " + key);
        }
    }
    public void remove(String key) {
        int index = hashFunction(key);
        ArrayList<String> list = table[index];

        int lastIndex = -1;
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).equals(key)) {
                lastIndex = i;
                break;
            }
        }

        if (lastIndex != -1) {
            list.remove(lastIndex);
            count--;
            System.out.println("Removed successfully: " + key);
            tableCanvas.updateTable();
        } else {
            System.out.println("Key not found: " + key);
        }
    }

    public int getHashTableSize() {
        return count;
    }

    public boolean contains(String key) {
        int index = hashFunction(key);
        if (table[index].contains(key)) {
            System.out.println("Key '" + key + "' found");
            return true;
        } else {
            System.out.println("Key '" + key + "' not found");
            return false;
        }
    }

    public String display() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tableSize; i++) {
            sb.append(i).append(": ").append(table[i]).append("\n");
        }
        return sb.toString();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hash Table GUI");

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);

        // TableCanvas
        tableCanvas = new TableCanvas(table);
        root.setTop(tableCanvas);

        // Control Panel (South)
        GridPane controlPanel = new GridPane();
        controlPanel.setPadding(new Insets(10));
        controlPanel.setHgap(5);
        controlPanel.setVgap(10);

        TextField inputField = new TextField();
        inputField.setPromptText("Enter key");
        inputField.setMaxWidth(150);  // Set max width for smaller input field

        Button addButton = new Button("Add");
        Button removeButton = new Button("Remove");
        Button sizeButton = new Button("Size");
        Button displayButton = new Button("Display");

        TextArea displayArea = new TextArea();
        displayArea.setPrefRowCount(5);  // Set preferred row count for smaller height
        displayArea.setPrefColumnCount(20);  // Set preferred column count for smaller width
        displayArea.setWrapText(true);

        controlPanel.add(inputField, 1, 0);
        controlPanel.add(displayArea, 1, 1, 4, 1); // TextArea spans multiple columns
        controlPanel.add(addButton, 2, 2);
        controlPanel.add(removeButton, 3, 2);
        controlPanel.add(sizeButton, 4, 2);
        controlPanel.add(displayButton, 5, 2);

        // Set control panel to the bottom (South) of the border pane
        root.setBottom(controlPanel);

        // Bindings for buttons
        addButton.setOnAction(e -> {
            String key = inputField.getText().trim();
            if (!key.isEmpty()) {
                add(key);
                inputField.clear();
            }
        });

        removeButton.setOnAction(e -> {
            String key = inputField.getText().trim();
            if (!key.isEmpty()) {
                remove(key); // Implement remove functionality if needed
                inputField.clear();
            }
        });

        sizeButton.setOnAction(e -> {
            int size = getHashTableSize();
            String sizeString = Integer.toString(size);
            displayArea.setText(sizeString);
            System.out.println("Hash table size: " + size);
        });

        displayButton.setOnAction(e -> {
            String displayText = display();
            displayArea.setText(displayText);
        });

        // Display the scene
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}

class TableCanvas extends Canvas {
    private static final int CELL_WIDTH = 100;
    private static final int CELL_HEIGHT = 20;
    private static final int PADDING = 5;
    private static final int ANIMATION_STEPS = 20;
    private static final int ANIMATION_DELAY = 20;

    private ArrayList<String>[] table;
    private String animatingKey;
    private int animatingIndex;
    private int animatingChainPosition;
    private double currentX;
    private double currentY;
    private double targetX;
    private double targetY;
    private boolean movingToIndex;
    private int animationStep;

    public TableCanvas(ArrayList<String>[] table) {
        this.table = table;
        this.animatingKey = null;
        this.animatingIndex = -1;
        this.animatingChainPosition = -1;
        this.currentX = PADDING;
        this.currentY = -CELL_HEIGHT;
        this.targetX = PADDING + (CELL_WIDTH + PADDING);
        this.targetY = 0;
        this.movingToIndex = true;
        this.animationStep = 0;

        this.setWidth(1100); // Adjust width for initial display
        this.setHeight((CELL_HEIGHT + PADDING +10 ) * table.length);
        drawTable();
    }

    private void drawTable() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        for (int i = 0; i < table.length; i++) {
            double x = PADDING;
            double y = i * (CELL_HEIGHT + PADDING) + PADDING;

            gc.setStroke(Color.BLUE);
            gc.strokeRect(x, y, CELL_WIDTH , CELL_HEIGHT );
            gc.strokeText(String.valueOf(i), x + PADDING, y + CELL_HEIGHT / 2 + PADDING );

            for (int j = 0; j < table[i].size(); j++) {
                double previousX = x;
                x += CELL_WIDTH + PADDING;
                gc.strokeRect(x, y, CELL_WIDTH, CELL_HEIGHT);
                gc.strokeText(table[i].get(j), x + PADDING, y + CELL_HEIGHT / 2 + PADDING);
                // Draw connecting line
                gc.strokeLine(previousX + CELL_WIDTH, y + CELL_HEIGHT / 2, x, y + CELL_HEIGHT / 2);
            }

            // Draw end-of-list symbol if the list is not empty
            // Draw end-of-list symbol if the list is not empty
            if (!table[i].isEmpty()) {
                double previousX = x;
                x += CELL_WIDTH + PADDING;
                gc.strokeLine(previousX + CELL_WIDTH, y + CELL_HEIGHT/2 , x, y + CELL_HEIGHT/2  );
                // Draw vertical line
                gc.strokeLine(x, y + PADDING, x , y + CELL_HEIGHT - PADDING + 10);
                // Draw horizontal dashes that do not cross the vertical line
                int dashLength = 8;
                int dashSpacing = 5;
                for (double dashY = y + PADDING - 1; dashY < y + 2 + CELL_HEIGHT - PADDING; dashY += dashSpacing) {
                    gc.strokeLine(x, dashY + dashLength, x + dashLength, dashY - dashLength);
                }
            }

        }

        if (animatingKey != null) {
            gc.setFill(Color.BLACK);
            gc.fillText(animatingKey, currentX + PADDING, currentY + CELL_HEIGHT / 2 + PADDING);
        }
    }

    public void startAnimation(String key, int index) {
        animatingKey = key;
        animatingIndex = index;
        animatingChainPosition = table[index].size();
        animationStep = 0;

        currentX = PADDING;
        currentY = -CELL_HEIGHT;
        targetX = PADDING + (CELL_WIDTH + PADDING);
        targetY = index * (CELL_HEIGHT + PADDING) + PADDING;
        movingToIndex = true;

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (animationStep < ANIMATION_STEPS) {
                    currentX += (targetX - currentX) / (ANIMATION_STEPS - animationStep + 1);
                    currentY += (targetY - currentY) / (ANIMATION_STEPS - animationStep + 1);
                    drawTable(); // Redessiner à chaque itération pour voir le mouvement
                } else {
                    if (movingToIndex) {
                        movingToIndex = false;
                        animationStep = 0;
                        targetX = PADDING + (CELL_WIDTH + PADDING) * (animatingChainPosition + 1);
                    } else {
                        currentX += (targetX - currentX) / (ANIMATION_STEPS - animationStep + 1);
                        if (animationStep >= ANIMATION_STEPS) {
                            stop();
                            // Ajouter l'élément uniquement s'il n'est pas déjà présent
                            if (!table[animatingIndex].contains(animatingKey)) {
                                table[animatingIndex].add(animatingKey);
                            }
                            animatingKey = null;
                            drawTable(); // Redessiner à la fin de l'animation
                        }
                    }
                }

                animationStep++;
            }
        };

        animationTimer.start();
    }




    public void updateTable() {
        drawTable();
    }
}