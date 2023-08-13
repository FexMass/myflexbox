# CSVImporter

A Vaadin Flow V23 / Spring data application in pure Java, which can import CSV files and save it into database entries.

## Requirements

npm latest version

Node latest version prefered or 18 or higher

JDK 17 minimum

## Running the application

Running the `mvn clean install` at start

The project is a standard Maven project. To run it from the command line,
type `mvnw` (Windows), or `./mvnw` (Mac & Linux), then open
http://localhost:8080 in your browser.
JAVA_HOME needs to be present for mvnw command

Or if that doesnt work we can navigate to project folder "/myflexbox" and type: `mvn spring-boot:run`

Application uses H2 in memory database which can be accessed on http://localhost:8080/h2-console/login.jsp

## Project structure

- `MainLayout.java` in `src/main/java` contains the navigation setup (i.e., the
  side/top bar and the main menu).
- `GridCsvImport.java` in `src/main/java` contains grid layout with necessary components for application
- `views` package in `src/main/java` contains the server-side Java views of your application.
- `themes` folder in `frontend/` contains the custom CSS styles.
