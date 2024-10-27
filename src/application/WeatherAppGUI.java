package application;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WeatherAppGUI extends Application {
    private TextField locationField;
    private Label tempLabel, humidityLabel, windSpeedLabel, conditionLabel;
    private ImageView weatherIcon;
    private ToggleButton unitToggle;
    private VBox historyBox;
    private List<String> searchHistory = new ArrayList<>();
    private boolean isCelsius = true;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Weather Information App");

        locationField = new TextField();
        locationField.setPromptText("Enter location (e.g., city name)");

        Button fetchWeatherButton = new Button("Get Weather");
        fetchWeatherButton.setOnAction(e -> fetchWeather());

        unitToggle = new ToggleButton("°C / °F");
        unitToggle.setOnAction(e -> toggleUnits());

        HBox inputBox = new HBox(10, locationField, fetchWeatherButton, unitToggle);
        inputBox.setAlignment(Pos.CENTER);

        tempLabel = new Label("Temperature: ");
        humidityLabel = new Label("Humidity: ");
        windSpeedLabel = new Label("Wind Speed: ");
        conditionLabel = new Label("Condition: ");
        weatherIcon = new ImageView();
        
        VBox displayBox = new VBox(10, tempLabel, humidityLabel, windSpeedLabel, conditionLabel, weatherIcon);
        displayBox.setAlignment(Pos.CENTER);

        historyBox = new VBox(10);
        Label historyLabel = new Label("Search History");
        historyBox.getChildren().add(historyLabel);
        ScrollPane scrollPane = new ScrollPane(historyBox);
        scrollPane.setFitToWidth(true);

        VBox root = new VBox(20, inputBox, displayBox, scrollPane);
        root.setAlignment(Pos.CENTER);
        root.setStyle(getBackgroundStyle()); // Set dynamic background

        Scene scene = new Scene(root, 400, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchWeather() {
        String location = locationField.getText();
        if (location.isEmpty()) {
            showAlert("Input Error", "Please enter a location.");
            return;
        }

        try {
            JSONObject weatherData = WeatherAPIService.fetchWeatherData(location);
            double temp = weatherData.getJSONObject("main").getDouble("temp");
            int humidity = weatherData.getJSONObject("main").getInt("humidity");
            double windSpeed = weatherData.getJSONObject("wind").getDouble("speed");
            String condition = weatherData.getJSONArray("weather").getJSONObject(0).getString("description");

            tempLabel.setText("Temperature: " + (isCelsius ? temp + "°C" : (temp * 9/5 + 32) + "°F"));
            humidityLabel.setText("Humidity: " + humidity + "%");
            windSpeedLabel.setText("Wind Speed: " + windSpeed + " m/s");
            conditionLabel.setText("Condition: " + condition);

            setWeatherIcon(condition);
            addSearchToHistory(location);
        } catch (Exception e) {
            showAlert("Error", "Failed to retrieve weather data. Please try again.");
        }
    }

    private void toggleUnits() {
        isCelsius = !isCelsius;
        fetchWeather();
    }

    private void setWeatherIcon(String condition) {
        String iconName;
        if (condition.contains("cloud")) iconName = "cloud.png";
        else if (condition.contains("rain")) iconName = "rain.png";
        else iconName = "sun.png";
        weatherIcon.setImage(new Image("file:icons/" + iconName));//file:src/icons/
    }

    private void addSearchToHistory(String location) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        searchHistory.add("[" + now.format(formatter) + "] " + location);
        
        Label historyItem = new Label(searchHistory.get(searchHistory.size() - 1));
        historyBox.getChildren().add(historyItem);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    
    private String getBackgroundStyle() {
        int hour = LocalDateTime.now().getHour();
        if (hour < 6 || hour > 18) return "-fx-background-color: skyblue;";  // Nighttime shade of blue
        if (hour < 12) return "-fx-background-color: lightskyblue;";           // Morning sky blue
        return "-fx-background-color: deepskyblue;";                           // Afternoon sky blue
    }


    public static void main(String[] args) {
        launch(args);
    }
}
