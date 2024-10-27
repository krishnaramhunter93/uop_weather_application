package application;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class WeatherAPIService {
    private static final String API_KEY = "1d66db018a6da935eb5cfc87b7894c4f";//"1d66db018a6da935eb5cfc87b7894c4f"; // Replace with your API key
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";

    public static JSONObject fetchWeatherData(String location) throws Exception {
        String urlString = BASE_URL + "?q=" + location + "&appid=" + API_KEY + "&units=metric";
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        return new JSONObject(content.toString());
    }
}

