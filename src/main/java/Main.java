import sdk.WeatherSDK;
import util.WeatherException;

public class Main {
    public static void main(String[] args) {
        String apiKey = "2a09eabc4fca440d961c611206827a52";
        WeatherSDK sdk = WeatherSDK.create(apiKey, true);

        try {
            String moscowWeather = sdk.getWeather("Moscow");
            System.out.println("Moscow weather: " + moscowWeather);
            String parisWeather = sdk.getWeather("Paris");
            System.out.println("Paris weather: " + parisWeather);
        } catch (WeatherException e) {
            System.err.println(e.getMessage());
        }

        try {
            String updatedMoscowWeather = sdk.getWeather("Moscow");
            System.out.println("Updated Moscow weather: " + updatedMoscowWeather);

            String updatedParisWeather = sdk.getWeather("Paris");
            System.out.println("Updated Paris weather: " + updatedParisWeather);
        } catch (WeatherException e) {
            System.err.println("Error fetching weather: " + e.getMessage());
        }
    }
}