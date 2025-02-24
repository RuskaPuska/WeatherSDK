import cache.WeatherCache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sdk.WeatherSDK;
import util.WeatherException;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherSDKTest {

    @Mock
    private OkHttpClient mockClient;

    @Mock
    private Response mockResponse;

    @Mock
    private ResponseBody mockResponseBody;

    private WeatherSDK weatherSDK;

    @BeforeEach
    void setUp() {
        weatherSDK = WeatherSDK.create("2a09eabc4fca440d961c611206827a52", false);
        weatherSDK.setClient(mockClient);
    }

    @AfterEach
    void cleanUp() {
        WeatherSDK.deleteInstance("2a09eabc4fca440d961c611206827a52");
    }

    @Test
    void testGetWeather_Success() throws IOException, WeatherException {
        // Arrange
        String city = "Moscow";
        String jsonResponse = "{\"weather\":[{\"main\":\"Clouds\",\"description\":\"scattered clouds\"}],\"main\":{\"temp\":267.39,\"feels_like\":264.64},\"visibility\":10000,\"wind\":{\"speed\":1.6},\"dt\":1740301664,\"sys\":{\"sunrise\":1740285342,\"sunset\":1740322227},\"timezone\":10800,\"name\":\"Moscow\"}";

        // Setting mock OkHttpClient
        okhttp3.Call mockCall = mock(okhttp3.Call.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn(jsonResponse);

        // Act
        String result = weatherSDK.getWeather(city);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("\"temperature\":{\"temp\":267.39,\"feels_like\":264.64}"));
        assertTrue(result.contains("\"datetime\":1740301664"));
        assertTrue(result.contains("\"name\":\"Moscow\""));

        verify(mockClient, times(1)).newCall(any(Request.class));
        verify(mockCall, times(1)).execute();
        verify(mockResponse, times(1)).isSuccessful();
        verify(mockResponse, times(1)).body();
        verify(mockResponseBody, times(1)).string();
    }

    @Test
    void testGetWeather_Failure() throws IOException {
        // Arrange
        String city = "NonExistentCity";
        String errorMessage = "API Error: 404 – Not Found";

        // Setting mock OkHttpClient
        okhttp3.Call mockCall = mock(okhttp3.Call.class);
        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(false);
        when(mockResponse.code()).thenReturn(404);
        when(mockResponse.message()).thenReturn("Not Found");

        // Act & Assert
        WeatherException exception = assertThrows(WeatherException.class, () -> {
            weatherSDK.getWeather(city);
        });

        assertEquals(errorMessage, exception.getMessage());

        verify(mockClient, times(1)).newCall(any(Request.class));
        verify(mockCall, times(1)).execute();
        verify(mockResponse, times(1)).isSuccessful();
        verify(mockResponse, times(1)).code();
        verify(mockResponse, times(1)).message();
    }

    @Test
    void testCreate_DeleteInstance() {
        // Arrange
        String apiKey = "testApiKey";
        WeatherSDK sdk1 = WeatherSDK.create(apiKey, false);

        // Act & Assert
        assertNotNull(sdk1);
        assertThrows(IllegalArgumentException.class, () -> {
            WeatherSDK.create(apiKey, false);
        });

        WeatherSDK.deleteInstance(apiKey);
        WeatherSDK sdk2 = WeatherSDK.create(apiKey, false);
        assertNotNull(sdk2);
    }

    @Test
    void testWeatherCache_Valid() {
        // Arrange
        String jsonResponse = "{\"weather\":[{\"main\":\"Clouds\",\"description\":\"scattered clouds\"}],\"main\":{\"temp\":267.39,\"feels_like\":264.64},\"visibility\":10000,\"wind\":{\"speed\":1.6},\"dt\":1740301664,\"sys\":{\"sunrise\":1740285342,\"sunset\":1740322227},\"timezone\":10800,\"name\":\"Moscow\"}";
        WeatherCache cache = new WeatherCache(jsonResponse);

        // Act & Assert
        assertTrue(cache.isValid());
    }

    @Test
    void testWeatherCache_Expired() {
        // Arrange
        String jsonResponse = "{\"weather\":[{\"main\":\"Clouds\",\"description\":\"scattered clouds\"}],\"main\":{\"temp\":267.39,\"feels_like\":264.64},\"visibility\":10000,\"wind\":{\"speed\":1.6},\"dt\":1740301664,\"sys\":{\"sunrise\":1740285342,\"sunset\":1740322227},\"timezone\":10800,\"name\":\"Moscow\"}";
        WeatherCache cache = new WeatherCache(jsonResponse);

        // Setting invalid cache time (more than 10 minutes)
        cache.setTimestamp(Instant.now().minusSeconds(601).getEpochSecond());

        // Act & Assert
        assertFalse(cache.isValid());
    }
}