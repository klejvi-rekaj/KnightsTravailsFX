import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.CornerRadii;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class KnightTravails extends Application {
    private static final int BOARD_SIZE = 8;
    private static final int TILE_SIZE = 80;
    private Set<String> obstacles;
    private List<int[]> path;
    private ImageView knightView, targetView;
    
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        showIntroScreen();
    }

    private void showIntroScreen() {
        StackPane introPane = createIntroScreen();
        Scene introScene = new Scene(introPane, 600, 400);
        primaryStage.setScene(introScene);
        primaryStage.setTitle("Sfida e Kaloresit - Mirësevini");
        primaryStage.show();
    }

    private StackPane createIntroScreen() {
        StackPane introPane = new StackPane();

        
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, null,
                new Stop(0, Color.DARKSLATEBLUE),
                new Stop(1, Color.DARKCYAN)
        );
        BackgroundFill backgroundFill = new BackgroundFill(gradient, CornerRadii.EMPTY, null);
        introPane.setBackground(new Background(backgroundFill));

        
        Label title = new Label("Sfidat e Kalorësit");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        title.setTextFill(Color.GOLD);
        title.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");

       
        Button startButton = new Button("Fillo lojën");
        startButton.setStyle(
                "-fx-font-size: 20px; " +
                "-fx-background-color: linear-gradient(to bottom, #4CAF50, #45a049); " +
                "-fx-text-fill: white; " +
                "-fx-padding: 10px 20px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);"
        );
        startButton.setOnAction(event -> showGameScreen());

        
        VBox introContent = new VBox(20, title, startButton);
        introContent.setAlignment(Pos.CENTER);
        introPane.getChildren().add(introContent);

        return introPane;
    }

    private void showGameScreen() {
        GridPane chessBoard = createChessBoard();
        Label instructions = new Label("Kliko pllakat per te vendosur pengesa ose shtyp Enter per te vazhduar.");
        instructions.setTextFill(Color.BLUE);
        chessBoard.add(instructions, 0, BOARD_SIZE, BOARD_SIZE, 1);

        Scene gameScene = new Scene(chessBoard, BOARD_SIZE * TILE_SIZE, BOARD_SIZE * TILE_SIZE + 50);

        gameScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                getInputFromDialog("Pozicioni fillestar", "Fut koordinatat per pozicionin fillestar te kaloresit (psh. 0, 0):", start -> {
                    getInputFromDialog("Pozicioni perfundimtar", "Fut koordinatat per pozicionin qe synon (psh. 7, 6):", target -> {
                        path = findShortestPath(BOARD_SIZE, start[0], start[1], target[0], target[1]);
                        if (path == null) {
                            showError("Nuk u gjet asnje rruge", "Nuk u gjet dot asnje rruge per te shkruar ne pozicionin e synuar.");
                            return;
                        }

                        
                        targetView = new ImageView(new Image("file:/C:/Users/ariya/Downloads/target.png"));
                        targetView.setFitWidth(TILE_SIZE);
                        targetView.setFitHeight(TILE_SIZE);
                        chessBoard.add(targetView, target[1], target[0]);

                        
                        knightView = new ImageView(new Image("file:/C:/Users/ariya/Downloads/knight.png"));
                        knightView.setFitWidth(TILE_SIZE);
                        knightView.setFitHeight(TILE_SIZE);
                        chessBoard.add(knightView, start[1], start[0]);

                        
                        animateKnight(() -> showOutroScreen(path));
                        disableObstacleSelection(chessBoard);
                    });
                });
            }
        });

        primaryStage.setScene(gameScene);
        primaryStage.setTitle("Sfida e Kalorësit - Loja");
    }

    private void showOutroScreen(List<int[]> path) {
        StackPane outroPane = new StackPane();

       
        LinearGradient gradient = new LinearGradient(
                0, 0, 1, 1, true, null,
                new Stop(0, Color.DARKSLATEBLUE),
                new Stop(1, Color.DARKCYAN)
        );
        BackgroundFill backgroundFill = new BackgroundFill(gradient, CornerRadii.EMPTY, null);
        outroPane.setBackground(new Background(backgroundFill));

        
        Label statsLabel = new Label("Loja Mbaroi!\nLevizjet totale: " + path.size() + "\nRruget: " + formatPath(path));
        statsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        statsLabel.setTextFill(Color.WHITE);
        statsLabel.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");

        
        Button retryButton = new Button("Provo perseri");
        retryButton.setStyle(
                "-fx-font-size: 20px; " +
                "-fx-background-color: linear-gradient(to bottom, #4CAF50, #45a049); " +
                "-fx-text-fill: white; " +
                "-fx-padding: 10px 20px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);"
        );
        retryButton.setOnAction(event -> showIntroScreen());

       
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0), outroPane);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();

        
        VBox outroContent = new VBox(20, statsLabel, retryButton);
        outroContent.setAlignment(Pos.CENTER);
        outroPane.getChildren().add(outroContent);

        Scene outroScene = new Scene(outroPane, 600, 400);
        primaryStage.setScene(outroScene);
        primaryStage.setTitle("Knight’s Travails - Fundi");
    }

    private String formatPath(List<int[]> path) {
        StringBuilder sb = new StringBuilder();
        for (int[] coord : path) {
            sb.append("(").append(coord[0]).append(",").append(coord[1]).append(") ");
        }
        return sb.toString().trim();
    }

    private GridPane createChessBoard() {
        GridPane grid = new GridPane();
        obstacles = new HashSet<>();

       
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE);
                tile.setFill((row + col) % 2 == 0 ? Color.WHITE : Color.BLACK);

                
                final int r = row, c = col;
                tile.setOnMouseClicked(event -> {
                    String coord = r + "," + c;
                    if (obstacles.contains(coord)) {
                        obstacles.remove(coord);
                        tile.setFill((r + c) % 2 == 0 ? Color.WHITE : Color.BLACK);
                    } else {
                        obstacles.add(coord);
                        tile.setFill(Color.RED);
                    }
                });

                grid.add(tile, col, row);
            }
        }

        return grid;
    }

    private void disableObstacleSelection(GridPane grid) {
        for (javafx.scene.Node node : grid.getChildren()) {
            if (node instanceof Rectangle) {
                node.setOnMouseClicked(null);
            }
        }
    }

    private void animateKnight(Runnable onFinished) {
        Timeline timeline = new Timeline();
        for (int i = 0; i < path.size(); i++) {
            int[] position = path.get(i);
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(i),
                event -> {
                    GridPane.setColumnIndex(knightView, position[1]);
                    GridPane.setRowIndex(knightView, position[0]);
                });
            timeline.getKeyFrames().add(keyFrame);
        }
        timeline.setOnFinished(event -> onFinished.run());
        timeline.play();
    }

    private void getInputFromDialog(String title, String prompt, InputCallback callback) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(prompt);

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(input -> {
            try {
                String[] parts = input.trim().split(",");
                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());
                callback.onInput(new int[]{x, y});
            } catch (Exception e) {
                showError("Input i pavlefshëm", "Ju lutemi vendosni koordinatat e vlefshme (e.g., 0, 0).");
            }
        });
    }

    private List<int[]> findShortestPath(int boardSize, int startX, int startY, int targetX, int targetY) {
        int[][] moves = {{-2, -1}, {-1, -2}, {1, -2}, {2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}};
        boolean[][] visited = new boolean[boardSize][boardSize];
        Queue<int[]> queue = new LinkedList<>();
        Map<String, String> parentMap = new HashMap<>();

        queue.add(new int[]{startX, startY});
        visited[startX][startY] = true;
        parentMap.put(startX + "," + startY, null);

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0], y = current[1];

            if (x == targetX && y == targetY) {
                return reconstructPath(parentMap, targetX, targetY);
            }

            for (int[] move : moves) {
                int nx = x + move[0], ny = y + move[1];
                String coord = nx + "," + ny;

                if (nx >= 0 && nx < boardSize && ny >= 0 && ny < boardSize && !visited[nx][ny] && !obstacles.contains(coord)) {
                    visited[nx][ny] = true;
                    queue.add(new int[]{nx, ny});
                    parentMap.put(coord, x + "," + y);
                }
            }
        }

        return null;
    }

    private List<int[]> reconstructPath(Map<String, String> parentMap, int targetX, int targetY) {
        List<int[]> path = new ArrayList<>();
        String coord = targetX + "," + targetY;

        while (coord != null) {
            String[] parts = coord.split(",");
            path.add(0, new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])});
            coord = parentMap.get(coord);
        }

        return path;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    interface InputCallback {
        void onInput(int[] input);
    }
}