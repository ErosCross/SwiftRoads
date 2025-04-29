import com.io.CityLoader;
import com.io.TrafficLightLoader;
import com.model.CityMap;
import com.model.Intersection;
import com.model.Road;
import com.pathfinding.PathFinder;
import com.ui.MapView;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.layout.StackPane;
import javafx.geometry.Side;
import java.net.URL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private double mouseAnchorX, mouseAnchorY;
    private double translateX = 0, translateY = 0; // Keep track of current translation
    private Scale scale;
    private MapView mapView;
    private boolean sfx = true; // Sound effects flag
    private boolean isSettingsMenuOpen = false;
    private boolean isInfoMenuOpen = false;

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create and show the splash screen in a new stage
        Stage splashStage = new Stage();
        StackPane splashPane = createSplashScreen();
        Scene splashScene = new Scene(splashPane, 600, 400); // Set splash screen size
        splashStage.setScene(splashScene);

        // Hide the title bar and window decorations (X, [] and -) for the splash screen
        splashStage.initStyle(StageStyle.TRANSPARENT); // Hide title bar for splash screen
        splashStage.setResizable(false); // Disable resizing
        splashStage.setOpacity(1); // Ensure the window is fully visible
        // Set application icon
        splashStage.getIcons().add(new Image(getClass().getResource("resources/img/logo.png").toExternalForm()));
        // Show splash screen
        splashStage.show();

        // Wait for splash screen to finish, then load the main window
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), splashPane);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0); // Fade out effect
        fadeOut.setOnFinished(event -> {
            // When splash screen fades out, load the main application
            loadMainApp(primaryStage, splashStage); // Transition to main application after splash screen fades out
        });
        fadeOut.play();
    }

    private void loadMainApp(Stage primaryStage, Stage splashStage) {
        // Load city and traffic lights
        CityMap cityMap = null;
        try {
            cityMap = CityLoader.loadCityFromCSV("src/com/io/Roads.csv", "src/com/io/intersectionsCoords.csv"); // Load city map from CSV
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            TrafficLightLoader.loadTrafficLightsFromCSV("src/com/io/traffic_lights.csv", cityMap); // Load traffic lights from CSV
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Create map view with dynamic sizing
        mapView = new MapView(cityMap);

        // Group to allow transforms (zoom/pan)
        AnchorPane root = new AnchorPane(mapView);
        root.setStyle("-fx-background-color: #2f2f2f;"); // Dark background color

        addItems(root, cityMap); // Add additional UI elements (e.g., buttons)

        // Set a fixed window size (not full screen)
        double width = 1200; // Set a specific width
        double height = 900; // Set a specific height

        // Scene setup
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(getClass().getResource("/resources/css/menu-dark.css").toExternalForm());
        primaryStage.setScene(scene); // Set scene to primary stage
        primaryStage.setResizable(true); // Allow resizing of the window

        // After splash screen fades out, show the decorated title bar
        primaryStage.initStyle(StageStyle.DECORATED); // Show title bar again

        // Set application title
        primaryStage.setTitle("SwiftRoads");

        // Set application icon
        primaryStage.getIcons().add(new Image(getClass().getResource("resources/img/logo.png").toExternalForm()));

        // Show the main app window
        primaryStage.show();

        // Close the splash screen after the main application window is shown
        splashStage.close();
    }

    private StackPane createSplashScreen() {
        // Create the splash screen layout and design
        StackPane splashPane = new StackPane();

        // Set a dark background for the splash screen
        splashPane.setStyle("-fx-background-color: #2f2f2f;");  // Dark background color

        // Create splash screen content (like a logo or animation)
        ImageView splashImage = new ImageView(new Image(getClass().getResource("resources/img/logo-nobg.png").toExternalForm()));
        splashImage.setFitWidth(200); // Set width of logo
        splashImage.setFitHeight(200); // Set height of logo

        // Create a loading GIF (e.g., a spinner) and add it to the splash screen
        ImageView loadingGif = new ImageView(new Image(getClass().getResource("resources/img/loading.gif").toExternalForm()));
        loadingGif.setFitWidth(50);  // Set width of loading GIF
        loadingGif.setFitHeight(50); // Set height of loading GIF

        // Add both the logo and loading GIF to the splash screen
        splashPane.getChildren().addAll(splashImage, loadingGif);  // Add logo and loading GIF

        // Center the loading GIF below the logo
        StackPane.setAlignment(loadingGif, Pos.BOTTOM_CENTER);

        // Fade in the splash screen
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), splashPane);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);  // Set initial and final opacity for fade-in
        fadeIn.play();  // Play the fade-in animation

        return splashPane;  // Return the splash pane
    }


    private void applyContextMenuTheme(ContextMenu menu, boolean isDark) {
        menu.getScene().getStylesheets().clear();
        if (isDark) {
            menu.getScene().getStylesheets().add(getClass().getResource("/resources/css/menu-dark.css").toExternalForm());
        } else {
            menu.getScene().getStylesheets().add(getClass().getResource("/resources/css/menu-light.css").toExternalForm());
        }
    }


    public void playClickSound() {
        // Get the URL for the sound file
        if (this.sfx) {
            try {
                URL resource = getClass().getResource("resources/sfx/pressed.mp3"); // Load sound file
                if (resource == null) {
                    throw new IllegalArgumentException("Sound file not found!"); // If sound file is missing
                }
                Media media = new Media(resource.toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setCycleCount(1);
                mediaPlayer.play(); // Play the sound
            } catch (Exception e) {
                System.out.println(e.toString()); // Log error if sound fails
            }
        }
    }
    private void highlightPath(List<Road> path) {
        // Loop through the list of roads and highlight them one by one with a delay
        for (int i = 0; i < path.size(); i++) {
            final Road road = path.get(i);  // Get the current road
            // Add a delay before highlighting each road
            PauseTransition pause = new PauseTransition(Duration.seconds(3 * i));  // 3 seconds per road (increasing delay for each)

            pause.setOnFinished(event -> {
                // Highlight the road when the pause is finished
                road.highlightRoad(); // Green color with a thicker line

                // After the highlight, wait another 3 seconds before disabling the highlight
                PauseTransition disableHighlightPause = new PauseTransition(Duration.seconds(3));
                disableHighlightPause.setOnFinished(disableEvent -> {
                    // Disable the highlight
                    road.highlightRoad(); // Call again to disable highlight (or reset style)
                });
                disableHighlightPause.play(); // Start the disable highlight pause
            });

            pause.play(); // Start the pause for the current road
        }
    }





    public void addItems(AnchorPane root, CityMap cityMap) {
        // --- Enable zooming via mouse scroll ---
        scale = new Scale(1, 1, 0, 0);
        mapView.getTransforms().add(scale);

        root.setOnScroll((ScrollEvent event) -> {
            double zoomFactor = event.getDeltaY() > 0 ? 1.1 : 0.9;
            scale.setX(scale.getX() * zoomFactor);
            scale.setY(scale.getY() * zoomFactor);
        });

        // --- Enable dragging/panning ---
        root.setOnMousePressed(e -> {
            mouseAnchorX = e.getSceneX();
            mouseAnchorY = e.getSceneY();
        });

        root.setOnMouseDragged(e -> {
            double deltaX = e.getSceneX() - mouseAnchorX;
            double deltaY = e.getSceneY() - mouseAnchorY;

            translateX += deltaX;
            translateY += deltaY;

            mapView.setTranslateX(translateX);
            mapView.setTranslateY(translateY);

            mouseAnchorX = e.getSceneX();
            mouseAnchorY = e.getSceneY();
        });

        // --- Reset Button ---
        Button resetButton = new Button();
        resetButton.setPrefSize(70, 70);
        String baseStyle = "-fx-background-color: green;" +
                "-fx-background-radius: 35;" +
                "-fx-border-radius: 35;" +
                "-fx-border-width: 2;" +
                "-fx-border-color: darkgreen;" +
                "-fx-effect: dropshadow(gaussian, darkgreen, 5, 0, 0, 0);";
        resetButton.setStyle(baseStyle);

        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            resetButton.setLayoutX((newVal.doubleValue() - resetButton.getPrefWidth()) / 2);
        });
        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            resetButton.setLayoutY(newVal.doubleValue() - resetButton.getPrefHeight() - 20);
        });

        ImageView resetImageView = new ImageView(getClass().getResource("/resources/img/focus.png").toExternalForm());
        resetImageView.setFitWidth(40);
        resetImageView.setFitHeight(40);
        resetButton.setGraphic(resetImageView);

        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), resetImageView);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(1);

        resetButton.setOnMouseEntered(e -> {
            playClickSound();
            rotateTransition.play();
            resetButton.setStyle(
                    "-fx-background-color: darkgreen;" +
                            "-fx-background-radius: 35;" +
                            "-fx-border-radius: 35;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-color: darkgreen;" +
                            "-fx-effect: dropshadow(gaussian, darkgreen, 10, 0, 0, 0);"
            );
        });

        resetButton.setOnMouseExited(e -> {
            rotateTransition.stop();
            resetImageView.setRotate(0);
            resetButton.setStyle(baseStyle);
        });

        resetButton.setOnAction(e -> {
            resetMapView(cityMap, root);
        });

        root.getChildren().add(resetButton);

        // --- Search Fields ---
        TextField startField = new TextField();
        startField.setPrefWidth(150);
        startField.setStyle("-fx-padding: 5px; -fx-font-size: 14px; -fx-background-color: #333; -fx-text-fill: white; -fx-border-color: #555;");
        startField.setLayoutX(15);
        startField.setLayoutY(30);
        startField.setPromptText("start location");

        TextField destinationField = new TextField();
        destinationField.setPrefWidth(150);
        destinationField.setStyle("-fx-padding: 5px; -fx-font-size: 14px; -fx-background-color: #333; -fx-text-fill: white; -fx-border-color: #555;");
        destinationField.setLayoutX(15);
        destinationField.setLayoutY(70);
        destinationField.setPromptText("destination location");

        root.getChildren().addAll(startField, destinationField);

        // --- Search Button ---
        Button searchButton = new Button();
        searchButton.setPrefSize(70, 70);
        searchButton.setStyle(baseStyle);

        ImageView searchImageView = new ImageView(getClass().getResource("/resources/img/magnifying-glass.png").toExternalForm());
        searchImageView.setFitWidth(40);
        searchImageView.setFitHeight(40);
        searchButton.setGraphic(searchImageView);

        searchButton.setLayoutX(180);
        searchButton.setLayoutY(32.5);

        searchButton.setOnMouseEntered(e -> {
            playClickSound();
            searchButton.setStyle(
                    "-fx-background-color: darkgreen;" +
                            "-fx-background-radius: 35;" +
                            "-fx-border-radius: 35;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-color: darkgreen;" +
                            "-fx-effect: dropshadow(gaussian, darkgreen, 10, 0, 0, 0);");
        });

        searchButton.setOnMouseExited(e -> searchButton.setStyle(baseStyle));

        // Create a new context menu for displaying top paths
        ContextMenu pathMenu = new ContextMenu();
        applyContextMenuTheme(pathMenu, true); // Optional: apply dark theme if needed

// Add action listener to search button
        searchButton.setOnAction(e -> {
            String start = startField.getText();
            String dest = destinationField.getText();
            System.out.println("Searching for route from " + start + " to " + dest);

            Intersection startIntersection = cityMap.getIntersectionByName(start);
            Intersection destinationIntersection = cityMap.getIntersectionByName(dest);

            if (startIntersection == null || destinationIntersection == null) {
                System.out.println("Invalid start or destination location.");
            } else {
                // Get up to 3 top paths
                List<List<Road>> topPaths = PathFinder.findTopKPaths(startIntersection, destinationIntersection, cityMap.getRoads(), 3);

                // Clear existing items in the path menu
                pathMenu.getItems().clear();

                if (topPaths.isEmpty()) {
                    MenuItem noPathItem = new MenuItem("No valid path found.");
                    noPathItem.setDisable(true);
                    pathMenu.getItems().add(noPathItem);
                } else {
                    int index = 1;
                    for (List<Road> path : topPaths) {
                        // Create a list of intersection names
                        List<String> pathIntersections = new ArrayList<>();
                        for (Road road : path) {
                            pathIntersections.add(road.getSource().getId());
                        }
                        if (!path.isEmpty()) {
                            pathIntersections.add(path.get(path.size() - 1).getDestination().getId());
                        }

                        // Convert to readable string
                        String pathText = String.join(" -> ", pathIntersections);
                        double distance = PathFinder.calculatePathDistance(path);

                        MenuItem pathItem = new MenuItem("Path " + index + ": " + pathText + " (Distance: " + distance + " KM)");
                        int finalIndex = index;
                        pathItem.setOnAction(event -> {
                            System.out.println("Selected Path " + finalIndex);
                            highlightPath(path);

                        });

                        pathMenu.getItems().add(pathItem);
                        index++;
                    }
                }

                // Show the menu near the button
                pathMenu.show(searchButton, Side.TOP, 0, -20);
            }
        });






        root.getChildren().add(searchButton);

        // --- Settings & Info Buttons ---
        Button settingsButton = new Button();
        settingsButton.setPrefSize(50, 50);
        settingsButton.setStyle(baseStyle);

        Button infoButton = new Button();
        infoButton.setPrefSize(50, 50);
        infoButton.setStyle(baseStyle);

        ImageView settingsImageView = new ImageView(getClass().getResource("/resources/img/settings.png").toExternalForm());
        settingsImageView.setFitWidth(30);
        settingsImageView.setFitHeight(30);
        settingsButton.setGraphic(settingsImageView);

        ImageView infoImageView = new ImageView(getClass().getResource("/resources/img/info.png").toExternalForm());
        infoImageView.setFitWidth(30);
        infoImageView.setFitHeight(30);
        infoButton.setGraphic(infoImageView);

        settingsButton.setOnMouseEntered(e -> {
            playClickSound();
            settingsButton.setStyle("-fx-background-color: darkgreen;" +
                    "-fx-background-radius: 25;" +
                    "-fx-border-radius: 25;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-color: darkgreen;" +
                    "-fx-effect: dropshadow(gaussian, darkgreen, 10, 0, 0, 0);");
        });

        settingsButton.setOnMouseExited(e -> settingsButton.setStyle(baseStyle));

        infoButton.setOnMouseEntered(e -> {
            playClickSound();
            infoButton.setStyle("-fx-background-color: darkgreen;" +
                    "-fx-background-radius: 25;" +
                    "-fx-border-radius: 25;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-color: darkgreen;" +
                    "-fx-effect: dropshadow(gaussian, darkgreen, 10, 0, 0, 0);");
        });

        infoButton.setOnMouseExited(e -> infoButton.setStyle(baseStyle));

        // --- Info Popup Menu ---
        ContextMenu infoMenu = new ContextMenu();
        applyContextMenuTheme(infoMenu, true); // <<< Apply dark theme

        MenuItem appInfo = new MenuItem("SwiftRoads - Urban Traffic Simulator");
        MenuItem versionInfo = new MenuItem("Version 1.0.0");
        MenuItem authorInfo = new MenuItem("Developed by Amit Shaviv");

        appInfo.setDisable(true);
        versionInfo.setDisable(true);
        authorInfo.setDisable(true);

        infoMenu.getItems().addAll(appInfo, versionInfo, authorInfo);

        infoButton.setOnAction(e -> {
            playClickSound();

            if (isInfoMenuOpen) {
                // Close the menu if it's already open
                infoMenu.hide();
            } else {
                // Open the menu if it's closed
                infoMenu.show(infoButton, Side.TOP, 0, -20); // Open 20px above the button
            }

            // Toggle the state of the menu
            isInfoMenuOpen = !isInfoMenuOpen;
        });

        // Position buttons
        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            settingsButton.setLayoutX((newVal.doubleValue() - settingsButton.getPrefWidth()) / 2 - 70);
            infoButton.setLayoutX((newVal.doubleValue() - infoButton.getPrefWidth()) / 2 + 70);
        });
        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            settingsButton.setLayoutY(newVal.doubleValue() - settingsButton.getPrefHeight() - 20);
            infoButton.setLayoutY(newVal.doubleValue() - infoButton.getPrefHeight() - 20);
        });

        root.getChildren().addAll(settingsButton, infoButton);

        // --- Settings Popup Menu ---
        ContextMenu settingsMenu = new ContextMenu();
        applyContextMenuTheme(settingsMenu, true); // <<< Apply dark theme

        CheckMenuItem themeToggle = new CheckMenuItem("Dark Theme");
        themeToggle.setSelected(true);
        themeToggle.setOnAction(e -> {
            playClickSound();
            boolean isDark = themeToggle.isSelected();
            System.out.println("Theme toggled: " + (isDark ? "Dark" : "Light"));

            Scene scene = root.getScene();
            if (scene != null) {
                scene.getStylesheets().clear();
                if (isDark) {
                    scene.getStylesheets().add(getClass().getResource("/resources/css/menu-dark.css").toExternalForm());
                } else {
                    scene.getStylesheets().add(getClass().getResource("/resources/css/menu-light.css").toExternalForm());
                }

                applyContextMenuTheme(settingsMenu, isDark);
                applyContextMenuTheme(infoMenu, isDark);
            }
        });

        CheckMenuItem soundToggle = new CheckMenuItem("Enable Sounds");
        soundToggle.setSelected(true);
        soundToggle.setOnAction(e -> {
            playClickSound();
            this.sfx = !this.sfx;
            System.out.println("Sounds: " + (soundToggle.isSelected() ? "Enabled" : "Muted"));
        });

        settingsMenu.getItems().addAll(themeToggle, soundToggle);

        settingsButton.setOnAction(e -> {
            playClickSound();

            if (isSettingsMenuOpen) {
                // Close the menu if it's already open
                settingsMenu.hide();
            } else {
                // Open the menu if it's closed
                settingsMenu.show(settingsButton, Side.TOP, -15, -20); // Open 20px above the button
            }

            // Toggle the state of the menu
            isSettingsMenuOpen = !isSettingsMenuOpen;
        });
    }





    // --- Reset Map View ---
    private void resetMapView(CityMap cityMap, AnchorPane root) {
        // Reset zoom and position
        scale.setX(1);
        scale.setY(1);
        translateX = 0;
        translateY = 0;
        mapView.setTranslateX(translateX);
        mapView.setTranslateY(translateY);
    }
}
