package ch.martinelli.mite4java;

import ch.martinelli.mite4java.api.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MiteClientTest {

    // ---- Account ----

    @Test
    void getAccount() {
        var stub = new StubHttpClient("""
                {"account":{"id":1234,"name":"demo","title":"Demo Account","currency":"EUR",
                "created_at":"2020-01-15T10:00:00+01:00","updated_at":"2025-06-01T12:00:00+02:00"}}""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var account = client.getAccount();

        assertEquals(1234, account.id());
        assertEquals("demo", account.name());
        assertEquals("Demo Account", account.title());
        assertEquals("EUR", account.currency());
        assertNotNull(account.createdAt());
        assertRequestPath(stub, "/account.json");
    }

    // ---- Myself ----

    @Test
    void getMyself() {
        var stub = new StubHttpClient("""
                {"user":{"id":1,"name":"Simon","email":"simon@example.com","note":"","archived":false,
                "role":"admin","language":"de","created_at":"2020-01-15T10:00:00+01:00",
                "updated_at":"2025-06-01T12:00:00+02:00"}}""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var user = client.getMyself();

        assertEquals(1, user.id());
        assertEquals("Simon", user.name());
        assertEquals("simon@example.com", user.email());
        assertEquals("admin", user.role());
        assertRequestPath(stub, "/myself.json");
    }

    // ---- Time Entries ----

    @Test
    void getTimeEntries() {
        var stub = new StubHttpClient("""
                [{"time_entry":{"id":100,"minutes":120,"date_at":"2025-03-15","note":"Coding",
                "billable":true,"locked":false,"revenue":200.0,"hourly_rate":10000,
                "user_id":1,"user_name":"Simon","project_id":10,"project_name":"Project A",
                "customer_id":5,"customer_name":"ACME","service_id":3,"service_name":"Development",
                "created_at":"2025-03-15T10:00:00+01:00","updated_at":"2025-03-15T12:00:00+01:00"}}]""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var entries = client.getTimeEntries();

        assertEquals(1, entries.size());
        var entry = entries.getFirst();
        assertEquals(100, entry.id());
        assertEquals(120, entry.minutes());
        assertEquals(LocalDate.of(2025, 3, 15), entry.dateAt());
        assertEquals("Coding", entry.note());
        assertTrue(entry.billable());
        assertFalse(entry.locked());
        assertEquals("Simon", entry.userName());
        assertEquals("Project A", entry.projectName());
        assertEquals("ACME", entry.customerName());
        assertEquals("Development", entry.serviceName());
    }

    @Test
    void getTimeEntryWithStartedTime() {
        var stub = new StubHttpClient("""
                {"time_entry":{"id":42,"minutes":120,"date_at":"2025-03-15","note":"Coding",
                "billable":true,"locked":false,"revenue":200.0,"hourly_rate":10000,
                "user_id":1,"user_name":"Simon","project_id":10,"project_name":"Project A",
                "customer_id":5,"customer_name":"ACME","service_id":3,"service_name":"Development",
                "started_time":600,
                "created_at":"2025-03-15T10:00:00+01:00","updated_at":"2025-03-15T12:00:00+01:00"}}""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var entry = client.getTimeEntry(42);

        assertEquals(600, entry.startedTime());
        assertEquals(java.time.LocalTime.of(10, 0), entry.startTime());
        assertEquals(java.time.LocalTime.of(12, 0), entry.endTime());
    }

    @Test
    void getTimeEntryWithoutStartedTime() {
        var stub = new StubHttpClient("""
                {"time_entry":{"id":42,"minutes":60,"date_at":"2025-03-15","note":"Test",
                "billable":false,"locked":false,"revenue":null,"hourly_rate":0,
                "user_id":1,"user_name":"Simon","project_id":null,"project_name":null,
                "customer_id":null,"customer_name":null,"service_id":null,"service_name":null,
                "created_at":"2025-03-15T10:00:00+01:00","updated_at":"2025-03-15T10:00:00+01:00"}}""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var entry = client.getTimeEntry(42);

        assertNull(entry.startedTime());
        assertNull(entry.startTime());
        assertNull(entry.endTime());
    }

    @Test
    void getTimeEntriesWithFilter() {
        var stub = new StubHttpClient("[]");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        client.getTimeEntries(Map.of("project_id", "10", "from", "2025-01-01"));

        var uri = stub.lastRequest().uri().toString();
        assertTrue(uri.contains("project_id=10"));
        assertTrue(uri.contains("from=2025-01-01"));
    }

    @Test
    void getTimeEntry() {
        var stub = new StubHttpClient("""
                {"time_entry":{"id":42,"minutes":60,"date_at":"2025-03-15","note":"Test",
                "billable":false,"locked":false,"revenue":null,"hourly_rate":0,
                "user_id":1,"user_name":"Simon","project_id":null,"project_name":null,
                "customer_id":null,"customer_name":null,"service_id":null,"service_name":null,
                "created_at":"2025-03-15T10:00:00+01:00","updated_at":"2025-03-15T10:00:00+01:00"}}""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var entry = client.getTimeEntry(42);

        assertEquals(42, entry.id());
        assertEquals(60, entry.minutes());
        assertNull(entry.revenue());
        assertRequestPath(stub, "/time_entries/42.json");
    }

    @Test
    void createTimeEntry() {
        var stub = new StubHttpClient(201, """
                {"time_entry":{"id":999,"minutes":90,"date_at":"2025-04-01","note":"New entry",
                "billable":true,"locked":false,"revenue":150.0,"hourly_rate":10000,
                "user_id":1,"user_name":"Simon","project_id":10,"project_name":"Project A",
                "customer_id":5,"customer_name":"ACME","service_id":3,"service_name":"Development",
                "created_at":"2025-04-01T10:00:00+02:00","updated_at":"2025-04-01T10:00:00+02:00"}}""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var command = CreateTimeEntry.builder()
                .dateAt(LocalDate.of(2025, 4, 1))
                .minutes(90)
                .note("New entry")
                .projectId(10)
                .serviceId(3)
                .build();
        var entry = client.createTimeEntry(command);

        assertEquals(999, entry.id());
        assertEquals(90, entry.minutes());
        assertEquals("New entry", entry.note());
        assertEquals("POST", stub.lastRequest().method());
        assertRequestPath(stub, "/time_entries.json");
    }

    @Test
    void updateTimeEntry() {
        var stub = new StubHttpClient("""
                {"time_entry":{"id":42,"minutes":120,"date_at":"2025-03-15","note":"Updated",
                "billable":true,"locked":false,"revenue":200.0,"hourly_rate":10000,
                "user_id":1,"user_name":"Simon","project_id":10,"project_name":"Project A",
                "customer_id":5,"customer_name":"ACME","service_id":3,"service_name":"Development",
                "created_at":"2025-03-15T10:00:00+01:00","updated_at":"2025-03-15T14:00:00+01:00"}}""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var command = UpdateTimeEntry.builder().minutes(120).note("Updated").build();
        var entry = client.updateTimeEntry(42, command);

        assertEquals(120, entry.minutes());
        assertEquals("Updated", entry.note());
        assertEquals("PATCH", stub.lastRequest().method());
        assertRequestPath(stub, "/time_entries/42.json");
    }

    @Test
    void deleteTimeEntry() {
        var stub = new StubHttpClient("");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        client.deleteTimeEntry(42);

        assertEquals("DELETE", stub.lastRequest().method());
        assertRequestPath(stub, "/time_entries/42.json");
    }

    // ---- Daily ----

    @Test
    void getDaily() {
        var stub = new StubHttpClient("[]");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        client.getDaily();
        assertRequestPath(stub, "/daily.json");
    }

    @Test
    void getDailyWithDate() {
        var stub = new StubHttpClient("[]");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        client.getDaily(2025, 3, 15);
        assertRequestPath(stub, "/daily/2025/3/15.json");
    }

    // ---- Tracker ----

    @Test
    void getTracker() {
        var stub = new StubHttpClient("""
                {"tracker":{"tracking_time_entry":{"id":42,"minutes":15,
                "since":"2025-03-15T10:00:00+01:00"}}}""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var tracker = client.getTracker();

        assertNotNull(tracker.trackingTimeEntry());
        assertEquals(42, tracker.trackingTimeEntry().id());
        assertEquals(15, tracker.trackingTimeEntry().minutes());
        assertRequestPath(stub, "/tracker.json");
    }

    @Test
    void startTracker() {
        var stub = new StubHttpClient("""
                {"tracker":{"tracking_time_entry":{"id":42,"minutes":0,
                "since":"2025-03-15T10:00:00+01:00"}}}""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var tracker = client.startTracker(42);

        assertEquals(42, tracker.trackingTimeEntry().id());
        assertEquals("PATCH", stub.lastRequest().method());
        assertRequestPath(stub, "/tracker/42.json");
    }

    @Test
    void stopTracker() {
        var stub = new StubHttpClient("""
                {"tracker":{"stopped_time_entry":{"id":42,"minutes":30}}}""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var tracker = client.stopTracker(42);

        assertNotNull(tracker.stoppedTimeEntry());
        assertEquals(42, tracker.stoppedTimeEntry().id());
        assertEquals(30, tracker.stoppedTimeEntry().minutes());
        assertEquals("DELETE", stub.lastRequest().method());
    }

    // ---- Projects ----

    @Test
    void getProjects() {
        var stub = new StubHttpClient("""
                [{"project":{"id":10,"name":"Website","note":"","customer_id":5,"customer_name":"ACME",
                "budget":0,"budget_type":"minutes","hourly_rate":10000,"active_hourly_rate":"hourly_rate",
                "archived":false,"created_at":"2025-01-01T10:00:00+01:00",
                "updated_at":"2025-01-01T10:00:00+01:00"}}]""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var projects = client.getProjects();

        assertEquals(1, projects.size());
        assertEquals("Website", projects.getFirst().name());
        assertEquals("ACME", projects.getFirst().customerName());
        assertRequestPath(stub, "/projects.json");
    }

    @Test
    void getArchivedProjects() {
        var stub = new StubHttpClient("[]");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        client.getArchivedProjects();
        assertRequestPath(stub, "/projects/archived.json");
    }

    @Test
    void getProject() {
        var stub = new StubHttpClient("""
                {"project":{"id":10,"name":"Website","note":"","customer_id":5,"customer_name":"ACME",
                "budget":0,"budget_type":"minutes","hourly_rate":10000,"active_hourly_rate":"hourly_rate",
                "archived":false,"created_at":"2025-01-01T10:00:00+01:00",
                "updated_at":"2025-01-01T10:00:00+01:00"}}""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var project = client.getProject(10);

        assertEquals(10, project.id());
        assertEquals("Website", project.name());
    }

    @Test
    void createProject() {
        var stub = new StubHttpClient(201, """
                {"project":{"id":20,"name":"New Project","note":"","customer_id":null,"customer_name":null,
                "budget":0,"budget_type":"minutes","hourly_rate":0,"active_hourly_rate":null,
                "archived":false,"created_at":"2025-04-01T10:00:00+02:00",
                "updated_at":"2025-04-01T10:00:00+02:00"}}""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var project = client.createProject(new CreateProject("New Project"));

        assertEquals(20, project.id());
        assertEquals("New Project", project.name());
        assertEquals("POST", stub.lastRequest().method());
    }

    @Test
    void deleteProject() {
        var stub = new StubHttpClient("");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        client.deleteProject(10);

        assertEquals("DELETE", stub.lastRequest().method());
        assertRequestPath(stub, "/projects/10.json");
    }

    // ---- Customers ----

    @Test
    void getCustomers() {
        var stub = new StubHttpClient("""
                [{"customer":{"id":5,"name":"ACME","note":"Important client",
                "active_hourly_rate":"hourly_rate","hourly_rate":10000,
                "archived":false,"created_at":"2025-01-01T10:00:00+01:00",
                "updated_at":"2025-01-01T10:00:00+01:00"}}]""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var customers = client.getCustomers();

        assertEquals(1, customers.size());
        assertEquals("ACME", customers.getFirst().name());
        assertEquals(10000, customers.getFirst().hourlyRate());
        assertRequestPath(stub, "/customers.json");
    }

    @Test
    void getArchivedCustomers() {
        var stub = new StubHttpClient("[]");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        client.getArchivedCustomers();
        assertRequestPath(stub, "/customers/archived.json");
    }

    @Test
    void createCustomer() {
        var stub = new StubHttpClient(201, """
                {"customer":{"id":99,"name":"New Customer","note":"","active_hourly_rate":null,
                "hourly_rate":0,"archived":false,
                "created_at":"2025-04-01T10:00:00+02:00","updated_at":"2025-04-01T10:00:00+02:00"}}""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var customer = client.createCustomer(new CreateCustomer("New Customer"));

        assertEquals(99, customer.id());
        assertEquals("New Customer", customer.name());
        assertEquals("POST", stub.lastRequest().method());
    }

    @Test
    void deleteCustomer() {
        var stub = new StubHttpClient("");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        client.deleteCustomer(5);

        assertEquals("DELETE", stub.lastRequest().method());
        assertRequestPath(stub, "/customers/5.json");
    }

    // ---- Services ----

    @Test
    void getServices() {
        var stub = new StubHttpClient("""
                [{"service":{"id":3,"name":"Development","note":"","hourly_rate":10000,
                "billable":true,"archived":false,
                "created_at":"2025-01-01T10:00:00+01:00","updated_at":"2025-01-01T10:00:00+01:00"}}]""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var services = client.getServices();

        assertEquals(1, services.size());
        assertEquals("Development", services.getFirst().name());
        assertTrue(services.getFirst().billable());
        assertRequestPath(stub, "/services.json");
    }

    @Test
    void getArchivedServices() {
        var stub = new StubHttpClient("[]");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        client.getArchivedServices();
        assertRequestPath(stub, "/services/archived.json");
    }

    @Test
    void createService() {
        var stub = new StubHttpClient(201, """
                {"service":{"id":50,"name":"Consulting","note":"","hourly_rate":15000,
                "billable":true,"archived":false,
                "created_at":"2025-04-01T10:00:00+02:00","updated_at":"2025-04-01T10:00:00+02:00"}}""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var service = client.createService(new CreateService("Consulting"));

        assertEquals(50, service.id());
        assertEquals("Consulting", service.name());
    }

    @Test
    void deleteService() {
        var stub = new StubHttpClient("");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        client.deleteService(3);

        assertEquals("DELETE", stub.lastRequest().method());
        assertRequestPath(stub, "/services/3.json");
    }

    // ---- Users ----

    @Test
    void getUsers() {
        var stub = new StubHttpClient("""
                [{"user":{"id":1,"name":"Simon","email":"simon@example.com","note":"","archived":false,
                "role":"admin","language":"de",
                "created_at":"2025-01-01T10:00:00+01:00","updated_at":"2025-01-01T10:00:00+01:00"}}]""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var users = client.getUsers();

        assertEquals(1, users.size());
        assertEquals("Simon", users.getFirst().name());
        assertEquals("admin", users.getFirst().role());
        assertRequestPath(stub, "/users.json");
    }

    @Test
    void getArchivedUsers() {
        var stub = new StubHttpClient("[]");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        client.getArchivedUsers();
        assertRequestPath(stub, "/users/archived.json");
    }

    @Test
    void getUser() {
        var stub = new StubHttpClient("""
                {"user":{"id":1,"name":"Simon","email":"simon@example.com","note":"","archived":false,
                "role":"admin","language":"de",
                "created_at":"2025-01-01T10:00:00+01:00","updated_at":"2025-01-01T10:00:00+01:00"}}""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var user = client.getUser(1);

        assertEquals(1, user.id());
        assertEquals("Simon", user.name());
        assertRequestPath(stub, "/users/1.json");
    }

    // ---- Bookmarks ----

    @Test
    void getBookmarks() {
        var stub = new StubHttpClient("""
                [{"time_entry_bookmark":{"id":7,"name":"Last Week","query":"at=last_week",
                "type":"time_entries","account_id":1234,"user_id":1,
                "created_at":"2025-01-01T10:00:00+01:00","updated_at":"2025-01-01T10:00:00+01:00"}}]""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var bookmarks = client.getBookmarks();

        assertEquals(1, bookmarks.size());
        assertEquals("Last Week", bookmarks.getFirst().name());
        assertEquals("at=last_week", bookmarks.getFirst().query());
        assertRequestPath(stub, "/time_entries/bookmarks.json");
    }

    @Test
    void getBookmark() {
        var stub = new StubHttpClient("""
                {"time_entry_bookmark":{"id":7,"name":"Last Week","query":"at=last_week",
                "type":"time_entries","account_id":1234,"user_id":1,
                "created_at":"2025-01-01T10:00:00+01:00","updated_at":"2025-01-01T10:00:00+01:00"}}""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var bookmark = client.getBookmark(7);

        assertEquals(7, bookmark.id());
        assertRequestPath(stub, "/time_entries/bookmarks/7.json");
    }

    // ---- Error handling ----

    @Test
    void apiErrorThrowsException() {
        var stub = new StubHttpClient(401, """
                {"error":"Authentication failed"}""");
        var client = MiteClient.of("demo", "bad-key", "test", stub);

        var ex = assertThrows(MiteApiException.class, client::getAccount);

        assertEquals(401, ex.statusCode());
        assertTrue(ex.responseBody().contains("Authentication failed"));
    }

    @Test
    void deleteWithErrorThrowsException() {
        var stub = new StubHttpClient(422, """
                {"error":"Time entries exist"}""");
        var client = MiteClient.of("demo", "test-key", "test", stub);

        var ex = assertThrows(MiteApiException.class, () -> client.deleteProject(10));

        assertEquals(422, ex.statusCode());
    }

    // ---- Request headers ----

    @Test
    void requestContainsApiKeyHeader() {
        var stub = new StubHttpClient("""
                {"account":{"id":1,"name":"demo","title":"","currency":"EUR",
                "created_at":"2025-01-01T10:00:00+01:00","updated_at":"2025-01-01T10:00:00+01:00"}}""");
        var client = MiteClient.of("demo", "my-secret-key", "test-app", stub);

        client.getAccount();

        var request = stub.lastRequest();
        assertEquals("my-secret-key", request.headers().firstValue("X-MiteApiKey").orElse(""));
        assertEquals("test-app", request.headers().firstValue("User-Agent").orElse(""));
        assertEquals("application/json", request.headers().firstValue("Accept").orElse(""));
    }

    @Test
    void requestUrlContainsSubdomain() {
        var stub = new StubHttpClient("""
                {"account":{"id":1,"name":"mycompany","title":"","currency":"CHF",
                "created_at":"2025-01-01T10:00:00+01:00","updated_at":"2025-01-01T10:00:00+01:00"}}""");
        var client = MiteClient.of("mycompany", "key", "test", stub);

        client.getAccount();

        assertTrue(stub.lastRequest().uri().toString().startsWith("https://mycompany.mite.de/"));
    }

    // ---- Helper ----

    private static void assertRequestPath(StubHttpClient stub, String expectedPath) {
        assertEquals(expectedPath, stub.lastRequest().uri().getPath());
    }
}
