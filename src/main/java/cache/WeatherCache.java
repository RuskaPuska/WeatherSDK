package cache;

import java.time.Instant;

public class WeatherCache {
    private final String responseJson;
    private long timestamp;

    public WeatherCache(String responseJson) {
        this.responseJson = responseJson;
        this.timestamp = Instant.now().getEpochSecond();
    }

    public boolean isValid() {
        return Instant.now().getEpochSecond() - timestamp < 600; // 10 minutes
    }

    public String getResponseJson() {
        return responseJson;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}