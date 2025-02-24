Weather SDK
Описание
Weather SDK — это программная библиотека, которая предоставляет простой интерфейс для взаимодействия с API OpenWeatherMap. Она позволяет разработчикам легко получать актуальные данные о погоде для заданного города в формате JSON.

SDK поддерживает два режима работы:

On-demand mode : Обновление данных происходит только при запросе пользователя.
Polling mode : Автоматическое обновление данных каждые 10 минут для всех сохраненных городов.
Требования
Для использования этого SDK необходимы следующие компоненты:

Java 8 или выше
Maven (для управления зависимостями)
API ключ от OpenWeatherMap
Установка
1. Добавление зависимости (Maven)
   Если вы используете Maven, добавьте следующую зависимость в ваш pom.xml:

xml
Copy
1
2
3
4
5
⌄
<dependency>
<groupId>com.example</groupId>
<artifactId>weather-sdk</artifactId>
<version>1.0.0</version>
</dependency>
Примечание: Замените groupId, artifactId и version на соответствующие значения из вашего проекта.

2. Ручная установка
   Если вы не используете Maven, скопируйте JAR-файл SDK в ваш проект и добавьте его в classpath.

Использование
1. Создание экземпляра SDK
   Создайте новый экземпляр SDK, передав ваш API ключ и указав режим работы (on-demand или polling).

java
Copy
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
⌄
⌄
⌄
⌄
import sdk.WeatherSDK;

public class Main {
public static void main(String[] args) {
try {
// Создание SDK в on-demand режиме
WeatherSDK sdk = WeatherSDK.create("your_api_key", false);

            // Создание SDK в polling режиме
            WeatherSDK pollingSdk = WeatherSDK.create("your_api_key", true);
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}
Примечание: Не создавайте несколько экземпляров SDK с одним и тем же API ключом. Это вызовет исключение.

2. Получение данных о погоде
   Используйте метод getWeather(String city) для получения данных о погоде для указанного города.

java
Copy
1
2
3
4
5
6
⌄
⌄
try {
String weatherData = sdk.getWeather("London");
System.out.println(weatherData);
} catch (Exception e) {
System.err.println("Ошибка получения погоды: " + e.getMessage());
}
Пример ответа:
json
Copy
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
⌄
⌄
⌄
⌄
⌄
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
3. Удаление экземпляра SDK
   Если вам нужно удалить экземпляр SDK, используйте метод deleteInstance.

java
Copy
1
WeatherSDK.deleteInstance("your_api_key");
Настройка Polling Mode
В режиме polling, SDK автоматически обновляет данные о погоде для всех сохраненных городов каждые 10 минут. Это обеспечивает нулевую задержку при запросах пользователей.

Примечание: Если вы используете режим polling, убедитесь, что ваш API ключ имеет достаточное количество запросов в месяц.

Обработка ошибок
SDK генерирует исключения при возникновении ошибок, таких как:

Невалидный API ключ
Сетевые проблемы
Отсутствие данных для указанного города
Пример обработки ошибок:

java
Copy
1
2
3
4
5
⌄
⌄
try {
String weatherData = sdk.getWeather("InvalidCity");
} catch (Exception e) {
System.err.println("Ошибка: " + e.getMessage());
}
Пример полной программы
java
Copy
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
⌄
⌄
⌄
⌄
⌄
import sdk.WeatherSDK;
import util.WeatherException;

public class Main {
public static void main(String[] args) {
try {
// Создание SDK в режиме on-demand
WeatherSDK sdk = WeatherSDK.create("your_api_key", false);

            // Получение погоды для London
            String weatherData = sdk.getWeather("London");
            System.out.println("Погода в London:");
            System.out.println(weatherData);

            // Удаление экземпляра SDK
            WeatherSDK.deleteInstance("your_api_key");
        } catch (IllegalArgumentException e) {
            System.err.println("Ошибка создания SDK: " + e.getMessage());
        } catch (WeatherException e) {
            System.err.println("Ошибка получения погоды: " + e.getMessage());
        }
    }
}
Тестирование
SDK содержит набор юнит-тестов для проверки корректности работы методов. Для запуска тестов выполните следующую команду:

bash
Copy
1
mvn test
Примечание: Для тестирования сетевых запросов используются моки (mocks).

Документация
Полная документация доступна в формате Javadoc. Чтобы сгенерировать документацию, выполните следующую команду:

bash
Copy
1
javadoc -d docs sdk/WeatherSDK.java
Документация будет находиться в директории docs.

Возможности расширения
Поддержка нескольких API ключей : SDK уже поддерживает работу с разными API ключами одновременно.
Добавление новых параметров запроса : Например, прогноз погоды на несколько дней.
Локализация ответов : Поддержка разных языков для вывода данных.
Лицензия
Этот проект распространяется под лицензией MIT. Подробнее см. файл LICENSE .

Контакты
Если у вас есть вопросы или предложения по улучшению SDK, свяжитесь со мной:

Email: your-email@example.com
GitHub: your-github-profile