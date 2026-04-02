package ch.martinelli.mite4java;

import ch.martinelli.mite4java.api.*;
import ch.martinelli.mite4java.domain.*;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Java client for the <a href="https://mite.de/api/">mite.de time tracking API</a>.
 *
 * <p>Usage:
 * <pre>{@code
 * var client = MiteClient.of("my-account", "my-api-key");
 * var projects = client.getProjects();
 * }</pre>
 */
public final class MiteClient {

    private final String baseUrl;
    private final String apiKey;
    private final String userAgent;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private MiteClient(String subdomain, String apiKey, String userAgent, HttpClient httpClient) {
        this.baseUrl = "https://%s.mite.de".formatted(subdomain);
        this.apiKey = apiKey;
        this.userAgent = userAgent;
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Creates a new MiteClient with default settings.
     */
    public static MiteClient of(String subdomain, String apiKey) {
        return new MiteClient(subdomain, apiKey, "mite4java", HttpClient.newHttpClient());
    }

    /**
     * Creates a new MiteClient with a custom User-Agent and HttpClient.
     */
    public static MiteClient of(String subdomain, String apiKey, String userAgent, HttpClient httpClient) {
        return new MiteClient(subdomain, apiKey, userAgent, httpClient);
    }

    // ---- Account ----

    public Account getAccount() {
        return get("/account.json", Account.Wrapper.class).account();
    }

    // ---- Myself ----

    public User getMyself() {
        return get("/myself.json", User.Wrapper.class).user();
    }

    // ---- Time Entries ----

    public List<TimeEntry> getTimeEntries() {
        return getTimeEntries(Map.of());
    }

    public List<TimeEntry> getTimeEntries(Map<String, String> params) {
        return getList("/time_entries.json", params, TimeEntry.Wrapper.class, TimeEntry.Wrapper::timeEntry);
    }

    public List<TimeEntryGroup> getTimeEntriesGrouped(String groupBy) {
        return getTimeEntriesGrouped(groupBy, Map.of());
    }

    public List<TimeEntryGroup> getTimeEntriesGrouped(String groupBy, Map<String, String> additionalParams) {
        var params = new LinkedHashMap<>(additionalParams);
        params.put("group_by", groupBy);
        return getList("/time_entries.json", params, TimeEntryGroup.Wrapper.class, TimeEntryGroup.Wrapper::timeEntryGroup);
    }

    public List<TimeEntry> getDaily() {
        return getList("/daily.json", Map.of(), TimeEntry.Wrapper.class, TimeEntry.Wrapper::timeEntry);
    }

    public List<TimeEntry> getDaily(int year, int month, int day) {
        return getList("/daily/%d/%d/%d.json".formatted(year, month, day), Map.of(),
                TimeEntry.Wrapper.class, TimeEntry.Wrapper::timeEntry);
    }

    public TimeEntry getTimeEntry(int id) {
        return get("/time_entries/%d.json".formatted(id), TimeEntry.Wrapper.class).timeEntry();
    }

    public TimeEntry createTimeEntry(CreateTimeEntry command) {
        return post("/time_entries.json", new CreateTimeEntry.Wrapper(command), TimeEntry.Wrapper.class).timeEntry();
    }

    public TimeEntry updateTimeEntry(int id, UpdateTimeEntry command) {
        return patch("/time_entries/%d.json".formatted(id), new UpdateTimeEntry.Wrapper(command), TimeEntry.Wrapper.class).timeEntry();
    }

    public void deleteTimeEntry(int id) {
        delete("/time_entries/%d.json".formatted(id));
    }

    // ---- Tracker ----

    public Tracker getTracker() {
        return get("/tracker.json", Tracker.Wrapper.class).tracker();
    }

    public Tracker startTracker(int timeEntryId) {
        return patch("/tracker/%d.json".formatted(timeEntryId), null, Tracker.Wrapper.class).tracker();
    }

    public Tracker stopTracker(int timeEntryId) {
        return delete("/tracker/%d.json".formatted(timeEntryId), Tracker.Wrapper.class).tracker();
    }

    // ---- Projects ----

    public List<Project> getProjects() {
        return getProjects(Map.of());
    }

    public List<Project> getProjects(Map<String, String> params) {
        return getList("/projects.json", params, Project.Wrapper.class, Project.Wrapper::project);
    }

    public List<Project> getArchivedProjects() {
        return getArchivedProjects(Map.of());
    }

    public List<Project> getArchivedProjects(Map<String, String> params) {
        return getList("/projects/archived.json", params, Project.Wrapper.class, Project.Wrapper::project);
    }

    public Project getProject(int id) {
        return get("/projects/%d.json".formatted(id), Project.Wrapper.class).project();
    }

    public Project createProject(CreateProject command) {
        return post("/projects.json", new CreateProject.Wrapper(command), Project.Wrapper.class).project();
    }

    public Project updateProject(int id, UpdateProject command) {
        return patch("/projects/%d.json".formatted(id), new UpdateProject.Wrapper(command), Project.Wrapper.class).project();
    }

    public void deleteProject(int id) {
        delete("/projects/%d.json".formatted(id));
    }

    // ---- Customers ----

    public List<Customer> getCustomers() {
        return getCustomers(Map.of());
    }

    public List<Customer> getCustomers(Map<String, String> params) {
        return getList("/customers.json", params, Customer.Wrapper.class, Customer.Wrapper::customer);
    }

    public List<Customer> getArchivedCustomers() {
        return getArchivedCustomers(Map.of());
    }

    public List<Customer> getArchivedCustomers(Map<String, String> params) {
        return getList("/customers/archived.json", params, Customer.Wrapper.class, Customer.Wrapper::customer);
    }

    public Customer getCustomer(int id) {
        return get("/customers/%d.json".formatted(id), Customer.Wrapper.class).customer();
    }

    public Customer createCustomer(CreateCustomer command) {
        return post("/customers.json", new CreateCustomer.Wrapper(command), Customer.Wrapper.class).customer();
    }

    public Customer updateCustomer(int id, UpdateCustomer command) {
        return patch("/customers/%d.json".formatted(id), new UpdateCustomer.Wrapper(command), Customer.Wrapper.class).customer();
    }

    public void deleteCustomer(int id) {
        delete("/customers/%d.json".formatted(id));
    }

    // ---- Services ----

    public List<Service> getServices() {
        return getServices(Map.of());
    }

    public List<Service> getServices(Map<String, String> params) {
        return getList("/services.json", params, Service.Wrapper.class, Service.Wrapper::service);
    }

    public List<Service> getArchivedServices() {
        return getArchivedServices(Map.of());
    }

    public List<Service> getArchivedServices(Map<String, String> params) {
        return getList("/services/archived.json", params, Service.Wrapper.class, Service.Wrapper::service);
    }

    public Service getService(int id) {
        return get("/services/%d.json".formatted(id), Service.Wrapper.class).service();
    }

    public Service createService(CreateService command) {
        return post("/services.json", new CreateService.Wrapper(command), Service.Wrapper.class).service();
    }

    public Service updateService(int id, UpdateService command) {
        return patch("/services/%d.json".formatted(id), new UpdateService.Wrapper(command), Service.Wrapper.class).service();
    }

    public void deleteService(int id) {
        delete("/services/%d.json".formatted(id));
    }

    // ---- Users ----

    public List<User> getUsers() {
        return getUsers(Map.of());
    }

    public List<User> getUsers(Map<String, String> params) {
        return getList("/users.json", params, User.Wrapper.class, User.Wrapper::user);
    }

    public List<User> getArchivedUsers() {
        return getArchivedUsers(Map.of());
    }

    public List<User> getArchivedUsers(Map<String, String> params) {
        return getList("/users/archived.json", params, User.Wrapper.class, User.Wrapper::user);
    }

    public User getUser(int id) {
        return get("/users/%d.json".formatted(id), User.Wrapper.class).user();
    }

    // ---- Bookmarks ----

    public List<Bookmark> getBookmarks() {
        return getList("/time_entries/bookmarks.json", Map.of(), Bookmark.Wrapper.class, Bookmark.Wrapper::bookmark);
    }

    public Bookmark getBookmark(int id) {
        return get("/time_entries/bookmarks/%d.json".formatted(id), Bookmark.Wrapper.class).bookmark();
    }

    // ---- HTTP internals ----

    private <T> T get(String path, Class<T> type) {
        var response = execute(buildRequest(path, Map.of()).GET().build());
        return deserialize(response, type);
    }

    private <W, T> List<T> getList(String path, Map<String, String> params, Class<W> wrapperType, Function<W, T> unwrap) {
        var response = execute(buildRequest(path, params).GET().build());
        JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, wrapperType);
        List<W> wrappers = deserializeList(response, listType);
        return wrappers.stream().map(unwrap).toList();
    }

    private <T> T post(String path, Object body, Class<T> type) {
        var json = serialize(body);
        var response = execute(buildRequest(path, Map.of())
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build());
        return deserialize(response, type);
    }

    private <T> T patch(String path, Object body, Class<T> type) {
        var json = body != null ? serialize(body) : "";
        var response = execute(buildRequest(path, Map.of())
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build());
        return deserialize(response, type);
    }

    private void delete(String path) {
        var response = execute(buildRequest(path, Map.of()).DELETE().build());
        if (response.statusCode() >= 400) {
            throw new MiteApiException(response.statusCode(), response.body());
        }
    }

    private <T> T delete(String path, Class<T> type) {
        var response = execute(buildRequest(path, Map.of()).DELETE().build());
        return deserialize(response, type);
    }

    private HttpRequest.Builder buildRequest(String path, Map<String, String> params) {
        var uri = baseUrl + path;
        if (!params.isEmpty()) {
            var query = params.entrySet().stream()
                    .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "="
                            + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                    .reduce((a, b) -> a + "&" + b)
                    .orElse("");
            uri += "?" + query;
        }
        return HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("X-MiteApiKey", apiKey)
                .header("User-Agent", userAgent)
                .header("Accept", "application/json");
    }

    private HttpResponse<String> execute(HttpRequest request) {
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new MiteApiException("Failed to communicate with mite API", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MiteApiException("Request interrupted", e);
        }
    }

    private <T> T deserialize(HttpResponse<String> response, Class<T> type) {
        if (response.statusCode() >= 400) {
            throw new MiteApiException(response.statusCode(), response.body());
        }
        try {
            return objectMapper.readValue(response.body(), type);
        } catch (IOException e) {
            throw new MiteApiException("Failed to parse response: " + response.body(), e);
        }
    }

    private <T> List<T> deserializeList(HttpResponse<String> response, JavaType listType) {
        if (response.statusCode() >= 400) {
            throw new MiteApiException(response.statusCode(), response.body());
        }
        try {
            return objectMapper.readValue(response.body(), listType);
        } catch (IOException e) {
            throw new MiteApiException("Failed to parse response: " + response.body(), e);
        }
    }

    private String serialize(Object body) {
        try {
            return objectMapper.writeValueAsString(body);
        } catch (IOException e) {
            throw new MiteApiException("Failed to serialize request body", e);
        }
    }
}
