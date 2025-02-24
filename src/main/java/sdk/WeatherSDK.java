package sdk;

import cache.WeatherCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.WeatherResponse;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import util.WeatherException;

import java.io.IOException;
import java.util.*;

/**
 * A software development kit (SDK) for accessing the OpenWeatherMap API.
 * This SDK provides functionality to retrieve current weather data for a given location.
 * It supports two modes: on-demand and polling.
 * <p>
 * Key Features:
 * - Stores weather data for up to 10 cities.
 * - Caches weather data for 10 minutes to reduce API calls.
 * - Supports both on-demand and polling modes.
 * - Handles errors such as invalid API keys, network issues, etc.
 *
 * @author Ruslan Musaev
 * @version 1.0
 */
public class WeatherSDK {
    /**
     * A map to store instances of the SDK created with different API keys.
     */
    private static final Map<String, WeatherSDK> instances = new HashMap<>();

    /**
     * The API key used to authenticate requests to the OpenWeatherMap API.
     */
    private final String apiKey;

    /**
     * The HTTP client used to make requests to the OpenWeatherMap API.
     */
    private OkHttpClient client = new OkHttpClient();

    /**
     * The object mapper used to serialize/deserialize JSON data.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * A cache to store weather data for up to 10 cities, with automatic eviction of the oldest entry.
     */
    private final Map<String, WeatherCache> cache = new LinkedHashMap<>(10, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, WeatherCache> eldest) {
            return size() > 10;
        }
    };

    /**
     * Constructs a new instance of the WeatherSDK with the specified API key and polling mode.
     *
     * @param apiKey      The API key for OpenWeatherMap.
     * @param pollingMode Whether to enable polling mode.
     */
    private WeatherSDK(String apiKey, boolean pollingMode) {
        this.apiKey = apiKey;

        if (pollingMode) {
            startPolling();
        }
    }

    /**
     * Sets a custom HTTP client for making requests to the OpenWeatherMap API.
     *
     * @param client The OkHttpClient instance to use.
     */
    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    /**
     * Creates a new instance of the WeatherSDK with the specified API key and mode.
     * If an instance with the same API key already exists, an exception is thrown.
     *
     * @param apiKey      The API key for OpenWeatherMap.
     * @param pollingMode Whether to enable polling mode.
     * @return A new WeatherSDK instance.
     * @throws IllegalArgumentException If an instance with the same API key already exists.
     */
    public static WeatherSDK create(String apiKey, boolean pollingMode) {
        if (instances.containsKey(apiKey)) {
            throw new IllegalArgumentException("SDK instance with this key already exists");
        }

        WeatherSDK sdk = new WeatherSDK(apiKey, pollingMode);
        instances.put(apiKey, sdk);
        return sdk;
    }

    /**
     * Deletes the WeatherSDK instance associated with the given API key.
     *
     * @param apiKey The API key of the instance to delete.
     */
    public static void deleteInstance(String apiKey) {
        instances.remove(apiKey);
    }

    /**
     * Starts the polling mechanism to periodically update weather data for all cached cities.
     */
    private void startPolling() {
        Timer pollingTimer = new Timer();
        pollingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                synchronized (cache) {
                    List<String> cities = new ArrayList<>(cache.keySet());
                    for (String city : cities) {
                        try {
                            // Fetch weather data and update cache
                            String weatherData = fetchWeather(city);
                            cache.put(city, new WeatherCache(weatherData));
                        } catch (WeatherException e) {
                            System.err.println("Polling failed for city: " + city + ". Error: " + e.getMessage());
                        }
                    }
                }
            }
        }, 0, 600_000); // Every 10 minutes
    }

    /**
     * Retrieves the current weather data for the specified city.
     *
     * @param city The name of the city.
     * @return A JSON string containing the weather data.
     * @throws WeatherException If an error occurs while fetching the weather data.
     */
    public String getWeather(String city) throws WeatherException {
        synchronized (cache) {
            WeatherCache cached = cache.get(city);
            if (cached != null && cached.isValid()) {
                return cached.getResponseJson();
            }
            return fetchWeather(city);
        }
    }

    /**
     * Fetches weather data for the specified city from the OpenWeatherMap API.
     *
     * @param city The name of the city.
     * @return A JSON string containing the weather data.
     * @throws WeatherException If an error occurs while fetching the weather data.
     */
    private String fetchWeather(String city) throws WeatherException {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new WeatherException("API Error: " + response.code() + " – " + response.message());
            }

            String jsonResponse = response.body().string();
            WeatherResponse parsedResponse = objectMapper.readValue(jsonResponse, WeatherResponse.class);
            String formattedJson = objectMapper.writeValueAsString(parsedResponse);

            cache.put(city, new WeatherCache(formattedJson));
            return formattedJson;
        } catch (IOException e) {
            throw new WeatherException("Network Error: " + e.getMessage());
        }
    }
}