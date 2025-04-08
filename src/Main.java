import com.io.CityLoader;
import com.io.TrafficLightLoader;
import com.model.CityMap;
import com.model.Intersection;
import com.ui.MapView;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.util.Duration;
import java.io.File;



public class Main extends Application {
    private double mouseAnchorX, mouseAnchorY;
    private double translateX = 0, translateY = 0; // Keep track of current translation
    private Scale scale;
    private MapView mapView;

    public static void main(String[] args) {
        launch(args);
    }




    public void playClickSound() {
        // Get the URL for the sound file
        try {
            Media media = new Media(new File("src/resources/sfx/pressed.wav").toURI().toString());
            MediaPlayer player = new MediaPlayer(media);
            player.play();

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }



    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load city and traffic lights
        CityMap cityMap = CityLoader.loadCityFromCSV("src/com/io/Roads.csv", "src/com/io/intersectionsCoords.csv");
        TrafficLightLoader.loadTrafficLightsFromCSV("src/com/io/traffic_lights.csv", cityMap);

        // Create map view with dynamic sizing
        mapView = new MapView(cityMap);

        // Group to allow transforms (zoom/pan)
        AnchorPane root = new AnchorPane(mapView);
        root.setStyle("-fx-background-color: #2f2f2f;"); // Dark background color

        addItems(root, cityMap);

        // Set a fixed window size (not full screen)
        double width = 1200; // Set a specific width
        double height = 900; // Set a specific height

        // Scene setup
        Scene scene = new Scene(root, width, height);
        // Set the application icon
        Image icon = new Image(getClass().getResource("resources/img/logo.png").toExternalForm());
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("SwiftRoads");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true); // Allow resizing of the window
        primaryStage.show();
    }

    public void addItems(AnchorPane root, CityMap cityMap) {
        // Initialize scale and add it to the map view
        scale = new Scale(1, 1, 0, 0); // Initial scale is 1 (no zoom)
        mapView.getTransforms().add(scale);

        // Zoom support (scroll event)
        root.setOnScroll((ScrollEvent event) -> {
            double zoomFactor = event.getDeltaY() > 0 ? 1.1 : 0.9;
            scale.setX(scale.getX() * zoomFactor);
            scale.setY(scale.getY() * zoomFactor);
        });

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

        // Create the reset button and style it
        Button resetButton = new Button();
        resetButton.setPrefSize(70, 70); // Increased button size

        // Base style for reset button
        String baseStyle = "-fx-background-color: green;" +
                "-fx-background-radius: 35;" + // Adjusted for larger button
                "-fx-border-radius: 35;" + // Adjusted for larger button
                "-fx-border-width: 2;" +
                "-fx-border-color: darkgreen;" +
                "-fx-effect: dropshadow(gaussian, darkgreen, 5, 0, 0, 0);";
        resetButton.setStyle(baseStyle);

        // Center the button at the bottom
        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            resetButton.setLayoutX((newVal.doubleValue() - resetButton.getPrefWidth()) / 2);
        });
        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            resetButton.setLayoutY(newVal.doubleValue() - resetButton.getPrefHeight() - 20);
        });

        // Image inside button
        ImageView resetImageView = new ImageView(getClass().getResource("/resources/img/focus.png").toExternalForm());
        resetImageView.setFitWidth(40);  // Increased icon size
        resetImageView.setFitHeight(40); // Increased icon size
        resetButton.setGraphic(resetImageView);

        // Rotate transition setup for hover effect
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), resetImageView);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(1); // Spin once

        // Hover effects for reset button
        resetButton.setOnMouseEntered(e -> {
            // Start spinning animation
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
            // Stop spinning animation (reset rotation)
            rotateTransition.stop();
            resetImageView.setRotate(0); // Reset the rotation to 0
            resetButton.setStyle(baseStyle);
        });

        // Reset action for reset button
        resetButton.setOnAction(e -> {
            playClickSound();
            resetMapView(cityMap, root);
        });

        root.getChildren().add(resetButton);

        // Create the search bar for start and destination with dark theme
        TextField startField = new TextField();
        startField.setPromptText("Start Point");
        startField.setPrefWidth(150); // Set the preferred width
        startField.setStyle("-fx-padding: 5px; -fx-font-size: 14px; -fx-background-color: #333; -fx-text-fill: white; -fx-border-color: #555;");

        TextField destinationField = new TextField();
        destinationField.setPromptText("Destination");
        destinationField.setPrefWidth(150); // Set the preferred width
        destinationField.setStyle("-fx-padding: 5px; -fx-font-size: 14px; -fx-background-color: #333; -fx-text-fill: white; -fx-border-color: #555;");

        // Layout and positioning of the search bar
        startField.setLayoutX(15);
        startField.setLayoutY(30);
        destinationField.setLayoutX(15);
        destinationField.setLayoutY(70);

        // Add the search fields to the root layout
        root.getChildren().addAll(startField, destinationField);

        // Create the search button with the same design as the reset button
        Button searchButton = new Button();
        searchButton.setPrefSize(70, 70); // Same size as the reset button
        searchButton.setStyle(baseStyle); // Same style as the reset button

        // Image for search button (same icon as reset button)
        ImageView searchImageView = new ImageView(getClass().getResource("/resources/img/magnifying-glass.png").toExternalForm());
        searchImageView.setFitWidth(40); // Same icon size
        searchImageView.setFitHeight(40); // Same icon size
        searchButton.setGraphic(searchImageView);

        // Hover effects for search button
        searchButton.setOnMouseEntered(e -> {
            searchButton.setStyle(
                    "-fx-background-color: darkgreen;" +
                            "-fx-background-radius: 35;" +
                            "-fx-border-radius: 35;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-color: darkgreen;" +
                            "-fx-effect: dropshadow(gaussian, darkgreen, 10, 0, 0, 0);"
            );
        });

        searchButton.setOnMouseExited(e -> {
            searchButton.setStyle(baseStyle); // Reset style on exit
        });

        // Position the search button right next to the destination text field
        searchButton.setLayoutX(180); // Placed right to the destination field (adjusted X)
        searchButton.setLayoutY(32.5);  // Aligned with destination field

        // Add the search button to the root layout
        root.getChildren().add(searchButton);

        // Search button action (implement your route calculation or action here)
        searchButton.setOnAction(e -> {
            String startPoint = startField.getText();
            String destination = destinationField.getText();
            playClickSound();
            // Logic to find the route or calculate something based on user input
            System.out.println("Searching for route from " + startPoint + " to " + destination);
        });

        // Create smaller buttons for Settings and Info
        Button settingsButton = new Button();
        settingsButton.setPrefSize(50, 50); // Smaller size than reset button
        settingsButton.setStyle(baseStyle); // Same style as reset button

        Button infoButton = new Button();
        infoButton.setPrefSize(50, 50); // Smaller size than reset button
        infoButton.setStyle(baseStyle); // Same style as reset button

        // Add images to the buttons
        ImageView settingsImageView = new ImageView(getClass().getResource("/resources/img/settings.png").toExternalForm());
        settingsImageView.setFitWidth(30);
        settingsImageView.setFitHeight(30);
        settingsButton.setGraphic(settingsImageView);

        ImageView infoImageView = new ImageView(getClass().getResource("/resources/img/info.png").toExternalForm());
        infoImageView.setFitWidth(30);
        infoImageView.setFitHeight(30);
        infoButton.setGraphic(infoImageView);

        // Hover effects for smaller buttons (Settings and Info)
        settingsButton.setOnMouseEntered(e -> {
            settingsButton.setStyle(
                    "-fx-background-color: darkgreen;" +
                            "-fx-background-radius: 25;" +
                            "-fx-border-radius: 25;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-color: darkgreen;" +
                            "-fx-effect: dropshadow(gaussian, darkgreen, 10, 0, 0, 0);"
            );
        });

        settingsButton.setOnMouseExited(e -> {
            settingsButton.setStyle(baseStyle);
        });

        infoButton.setOnMouseEntered(e -> {
            infoButton.setStyle(
                    "-fx-background-color: darkgreen;" +
                            "-fx-background-radius: 25;" +
                            "-fx-border-radius: 25;" +
                            "-fx-border-width: 2;" +
                            "-fx-border-color: darkgreen;" +
                            "-fx-effect: dropshadow(gaussian, darkgreen, 10, 0, 0, 0);"
            );
        });

        infoButton.setOnMouseExited(e -> {
            infoButton.setStyle(baseStyle);
        });

        // Position the smaller buttons
        // Center the button at the bottom
        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            settingsButton.setLayoutX((newVal.doubleValue() - settingsButton.getPrefWidth()) / 2 - 70);
        });
        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            settingsButton.setLayoutY(newVal.doubleValue() - settingsButton.getPrefHeight() - 20);
        });

        root.widthProperty().addListener((obs, oldVal, newVal) -> {
            infoButton.setLayoutX((newVal.doubleValue() - infoButton.getPrefWidth()) / 2 + 70);
        });
        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            infoButton.setLayoutY(newVal.doubleValue() - infoButton.getPrefHeight() - 20);
        });



        // Add smaller buttons to the root layout
        root.getChildren().addAll(settingsButton, infoButton);
    }


    private void resetMapView(CityMap cityMap, AnchorPane root) {
        // Remove only the mapView, not the whole root's children
        root.getChildren().remove(mapView);

        // Recreate the map view with the city map
        mapView = new MapView(cityMap);
        mapView.getTransforms().add(scale);

        // Reinitialize scale and translation
        scale.setX(1);
        scale.setY(1);
        translateX = 0;
        translateY = 0;

        // Calculate bounds
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

        for (Intersection intersection : cityMap.getIntersections()) {
            minX = Math.min(minX, intersection.getX());
            minY = Math.min(minY, intersection.getY());
            maxX = Math.max(maxX, intersection.getX());
            maxY = Math.max(maxY, intersection.getY());
        }

        double centerX = (minX + maxX) / 2;
        double centerY = (minY + maxY) / 2;

        double width = mapView.getWidth();
        double height = mapView.getHeight();

        translateX = (width / 2) - centerX;
        translateY = (height / 2) - centerY;

        mapView.setTranslateX(translateX);
        mapView.setTranslateY(translateY);

        root.getChildren().add(0, mapView); // Add only the new map view to bottom layer

        System.out.println("Map reset and redrawn. TranslateX: " + translateX + ", TranslateY: " + translateY);
    }

}
