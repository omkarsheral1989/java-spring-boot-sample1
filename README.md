# Java Spring Boot Sample

A small REST API for creating, reading, updating, and deleting users. Users may have an optional one-to-one address containing a city and country.

## Requirements

- Java 17

## Run the application

On macOS or Linux:

```bash
./gradlew bootRun
```

On Windows:

```bat
gradlew.bat bootRun
```

The application listens on `http://localhost:8080`.

For local development with hot reload, run:

```bash
./gradlew dev
```

On Windows, use `gradlew.bat dev`.

## API

All endpoints use JSON.

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/users` | Create a user |
| `GET` | `/users` | List users |
| `GET` | `/users/{id}` | Get a user |
| `PATCH` | `/users/{id}` | Update provided user fields |
| `DELETE` | `/users/{id}` | Delete a user |

Create a user:

```bash
curl -i -X POST http://localhost:8080/users \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Alice",
    "email": "alice@example.com",
    "address": {
      "city": "Toronto",
      "country": "Canada"
    }
  }'
```

The `address` field is optional. If present, both `city` and `country` are required and must not be blank.

PATCH only changes fields included in the request. Omit `address` to leave the current address unchanged, send an address object to replace it, or send `"address": null` to remove it.

```bash
curl -i -X PATCH http://localhost:8080/users/1 \
  -H 'Content-Type: application/json' \
  -d '{"address":{"city":"Vancouver","country":"Canada"}}'
```

## Database and H2 console

The application uses an H2 file database at `./data/usersdb`. Hibernate updates the schema at startup. Existing values in the former string-based user address column are discarded.

With the application running, open the H2 console at <http://localhost:8080/h2-console> and use:

- JDBC URL: `jdbc:h2:file:./data/usersdb`
- User name: `sa`
- Password: leave blank

## Run tests

```bash
./gradlew test
```

Repository persistence tests use the embedded H2 database through Spring's `@DataJpaTest`; service tests mock the repository.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE).
