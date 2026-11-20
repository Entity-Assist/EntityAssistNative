# EntityAssist Native Image

This project builds a standalone GraalVM executable with two smoke tests.
The date test round-trips an ISO date through EntityAssist's JPA converter.
The PostgreSQL test starts Hibernate Reactive, inserts two rows into a temporary
table, and uses EntityAssist's criteria query builder to select and verify one
row. The table is dropped when the transaction commits.

## Build

Use Maven 4, JDK 25, and GraalVM Native Image 25 with a working C toolchain.
Install the EntityAssist Reactive snapshot from the DevelopmentSuite checkout if
it is missing from your local Maven repository. Run these commands from the
DevelopmentSuite root.

```sh
mvn -f EntityAssist/EntityAssist/pom.xml -DskipTests install
mvn -f EntityAssist/native-image/pom.xml -Pnative package
EntityAssist/native-image/target/entityassist 2024-02-29
```

The executable should print `2024-02-29`. If `native-image` is outside your
`PATH`, pass `-Dnative.image.executable=/path/to/graalvm/bin/native-image`
to the second Maven command.

## PostgreSQL smoke test

Use a running PostgreSQL database and a user with permission to create temporary
tables. Set `EA_POSTGRES_PASSWORD` in the environment before running the test.

```sh
EA_POSTGRES_URL=postgresql://127.0.0.1:5432/smoke \
EA_POSTGRES_USER=postgres \
EntityAssist/native-image/target/entityassist --postgres
```

Expect `EntityAssist PostgreSQL query passed` and exit status zero. The smoke
test uses Hibernate Reactive's programmatic bootstrap, an entity without lazy
associations, and the Vert.x PostgreSQL client. The reachability metadata covers
this path, including Hibernate's reflective access to the smoke-test entity.
