# egyptian-pyramids

Nassef's Egyptian Pyramids App — CIS-055 Data Structures assignment. Reads `pharaoh.json` and `pyramid.json`, reports pharaohs and pyramids with contributor gold totals. HashMaps give O(1) id/hieroglyphic lookups; a HashSet backs the no-duplicates requested-pyramids report.

Commands: `1` all pharaohs, `2` pharaoh by id, `3` all pyramids + contributors, `4` pyramid by id (records the request), `5` requested-pyramids report (no duplicates), `q` quit.

## Run

```
mvn compile org.codehaus.mojo:exec-maven-plugin:3.5.0:java -Dexec.mainClass=com.joelperez.pyramids.App
```

Or open the folder in VS Code and Run `src/main/java/com/joelperez/pyramids/App.java`. Run from the project root so `src/main/resources/*.json` resolve.

## Test

```
mvn test
```

## Requirements

- JDK 25 (JAVA_HOME set), Apache Maven 3.9+ (pulls json-simple 1.1.1)
