package dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import util.WeatherException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherResponse {
    public Weather[] weather;

    @JsonAlias("main")
    private Temperature temperature;

    public int visibility;

    public Wind wind;

    @JsonAlias("dt")
    public long datetime;

    public Sys sys;

    public int timezone;

    public String name;

    public Weather getWeather() throws WeatherException {
        if (weather != null && weather.length > 0) {
            return weather[0];
        } else {
            throw new WeatherException("The field \"weather\" cannot be parsed");
        }
    }

    public Temperature getTemperature() {
        return temperature;
    }
}