# EntityAssist Native Image

This smoke test compiles the EntityAssist Reactive JPA date converter into a
standalone executable. The CLI converts an ISO date to a SQL date and back.
The Maven project uses the EntityAssist Reactive JAR and the Jakarta Persistence
API. Hibernate Reactive bootstrap and database queries are outside this smoke
test's scope.

## Build

Use Maven 4, JDK 25, and GraalVM Native Image 25 with a working C toolchain.
Run these commands from the DevelopmentSuite root. The first command is needed
when the EntityAssist Reactive snapshot is missing from your local Maven repository.

```sh
mvn -f EntityAssist/EntityAssist/pom.xml -DskipTests install
mvn -f EntityAssist/native-image/pom.xml -Pnative package
EntityAssist/native-image/target/entityassist 2024-02-29
```

The executable should print `2024-02-29`. If `native-image` is outside your
`PATH`, pass `-Dnative.image.executable=/path/to/graalvm/bin/native-image`
to the second Maven command.
