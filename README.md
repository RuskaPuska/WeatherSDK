# Weather SDK

## Description
Weather SDK is a software library that provides a simple interface for interacting with the OpenWeatherMap API. It allows developers to easily retrieve current weather data for a given location in JSON format.

The SDK supports two modes of operation:
- **On-demand mode**: Data updates only occur when requested by the user.
- **Polling mode**: Automatic updates every 10 minutes for all stored locations to ensure zero-latency responses for customer requests.

---

## Requirements
To use this SDK, the following components are required:
- Java 8 or higher
- Maven (for dependency management)
- An API key from [OpenWeatherMap](https://openweathermap.org/api)

---

## Installation

### 1. Adding Dependency (Maven)
If you're using Maven, add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>weather-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

*Note: Replace `groupId`, `artifactId`, and `version` with the appropriate values for your project.*

---

### 2. Manual Installation
If you're not using Maven, copy the SDK JAR file into your project and add it to the classpath.

---

## Usage

### 1. Creating an SDK Instance
Create a new instance of the SDK by providing your API key and specifying the operating mode (`on-demand` or `polling`).

```java
import sdk.WeatherSDK;

public class Main {
    public static void main(String[] args) {
        try {
            // Create SDK in on-demand mode
            WeatherSDK sdk = WeatherSDK.create("your_api_key", false);

            // Create SDK in polling mode
            WeatherSDK pollingSdk = WeatherSDK.create("your_api_key", true);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
```

*Note: Do not create multiple instances of the SDK with the same API key. This will throw an exception.*

---

### 2. Retrieving Weather Data
Use the `getWeather(String city)` method to retrieve weather data for the specified city.

```java
try {
    String weatherData = sdk.getWeather("London");
    System.out.println(weatherData);
} catch (WeatherException e) {
    System.err.println("Error retrieving weather: " + e.getMessage());
}
```

#### Example Response:
```json
{
  "weather": {
    "main": "Clouds",
    "description": "scattered clouds"
  },
  "temperature": {
    "temp": 269.6,
    "feels_like": 267.57
  },
  "visibility": 10000,
  "wind": {
    "speed": 1.38
  },
  "datetime": 1675744800,
  "sys": {
    "sunrise": 1675751262,
    "sunset": 1675787560
  },
  "timezone": 3600,
  "name": "London"
}
```

---

### 3. Deleting an SDK Instance
If you need to delete an SDK instance, use the `deleteInstance` method.

```java
WeatherSDK.deleteInstance("your_api_key");
```

---

## Configuring Polling Mode
In `polling` mode, the SDK automatically updates weather data for all stored locations every 10 minutes. This ensures minimal latency for user requests.

*Note: If you're using polling mode, ensure your API key has sufficient monthly request limits.*

---

## Error Handling
The SDK throws exceptions for various error scenarios, such as:
- Invalid API key
- Network issues
- Missing data for the specified city

Example of error handling:
```java
try {
    String weatherData = sdk.getWeather("InvalidCity");
} catch (WeatherException e) {
    System.err.println("Error: " + e.getMessage());
}
```

---

## Full Program Example
```java
import sdk.WeatherSDK;
import util.WeatherException;

public class Main {
    public static void main(String[] args) {
        try {
            // Create SDK in on-demand mode
            WeatherSDK sdk = WeatherSDK.create("your_api_key", false);

            // Retrieve weather for London
            String weatherData = sdk.getWeather("London");
            System.out.println("Weather in London:");
            System.out.println(weatherData);

            // Delete SDK instance
            WeatherSDK.deleteInstance("your_api_key");
        } catch (IllegalArgumentException e) {
            System.err.println("Error creating SDK: " + e.getMessage());
        } catch (WeatherException e) {
            System.err.println("Error retrieving weather: " + e.getMessage());
        }
    }
}
```

---

## Testing
The SDK includes a set of unit tests to verify the correctness of its methods. To run the tests, execute the following command:

```bash
mvn test
```

*Note: Mocks (mocks) are used for testing network requests.*

---

## Documentation
Full documentation is available in Javadoc format. To generate the documentation, run the following command:

```bash
javadoc -d docs sdk/WeatherSDK.java
```

The documentation will be located in the `docs` directory.

---
