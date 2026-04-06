# mite4java

Java client library for the [mite.de](https://mite.de) time tracking API.

**This project has moved to [https://github.com/martinellich/mite4java/](https://github.com/martinellich/mite4java/)**

## Requirements

- Java 25+

## Usage

```xml
<dependency>
    <groupId>ch.martinelli.oss</groupId>
    <artifactId>mite-java</artifactId>
    <version>2.0.0</version>
</dependency>
```

```java
var client = MiteClient.of("your-subdomain", "your-api-key");

// Account & user
var account = client.getAccount();
var myself = client.getMyself();

// Time entries
var entries = client.getTimeEntries();
var entry = client.getTimeEntry(123);

var newEntry = client.createTimeEntry(
    CreateTimeEntry.builder()
        .dateAt(LocalDate.now())
        .minutes(60)
        .note("Working on feature X")
        .projectId(456)
        .serviceId(789)
        .build()
);

// Projects, Customers, Services
var projects = client.getProjects();
var customers = client.getCustomers();
var services = client.getServices();

// Archived resources
var archivedProjects = client.getArchivedProjects();

// Filtering with query parameters
var filtered = client.getTimeEntries(Map.of(
    "project_id", "123",
    "from", "2025-01-01",
    "to", "2025-12-31"
));

// Grouped time entries
var grouped = client.getTimeEntriesGrouped("customer,project");

// Tracker (stopwatch)
var tracker = client.startTracker(entryId);
client.stopTracker(entryId);

// Users (read-only)
var users = client.getUsers();

// Bookmarks (read-only)
var bookmarks = client.getBookmarks();
```

## API Coverage

| Resource     | GET | POST | PATCH | DELETE |
|-------------|-----|------|-------|--------|
| Account     | x   |      |       |        |
| Myself      | x   |      |       |        |
| Time Entries| x   | x    | x     | x      |
| Tracker     | x   |      | x     | x      |
| Projects    | x   | x    | x     | x      |
| Customers   | x   | x    | x     | x      |
| Services    | x   | x    | x     | x      |
| Users       | x   |      |       |        |
| Bookmarks   | x   |      |       |        |

## License

Apache License 2.0
