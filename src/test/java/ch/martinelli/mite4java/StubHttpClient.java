package ch.martinelli.mite4java;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * A simple stub HttpClient that returns predefined responses for testing.
 */
final class StubHttpClient extends HttpClient {

    private final int statusCode;
    private final String responseBody;
    private HttpRequest lastRequest;

    StubHttpClient(int statusCode, String responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    StubHttpClient(String responseBody) {
        this(200, responseBody);
    }

    HttpRequest lastRequest() {
        return lastRequest;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler)
            throws IOException, InterruptedException {
        this.lastRequest = request;
        return (HttpResponse<T>) new StubResponse(statusCode, responseBody, request);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> handler) {
        this.lastRequest = request;
        return CompletableFuture.completedFuture(
                (HttpResponse<T>) new StubResponse(statusCode, responseBody, request));
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> handler,
                                                             HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
        return sendAsync(request, handler);
    }

    @Override public Optional<CookieHandler> cookieHandler() { return Optional.empty(); }
    @Override public Optional<Duration> connectTimeout() { return Optional.empty(); }
    @Override public Redirect followRedirects() { return Redirect.NEVER; }
    @Override public Optional<ProxySelector> proxy() { return Optional.empty(); }
    @Override public SSLContext sslContext() { return null; }
    @Override public SSLParameters sslParameters() { return new SSLParameters(); }
    @Override public Optional<Authenticator> authenticator() { return Optional.empty(); }
    @Override public Version version() { return Version.HTTP_2; }
    @Override public Optional<java.util.concurrent.Executor> executor() { return Optional.empty(); }

    private record StubResponse(int statusCode, String body, HttpRequest request) implements HttpResponse<String> {
        @Override public int statusCode() { return statusCode; }
        @Override public HttpRequest request() { return request; }
        @Override public Optional<HttpResponse<String>> previousResponse() { return Optional.empty(); }
        @Override public HttpHeaders headers() { return HttpHeaders.of(java.util.Map.of(), (a, b) -> true); }
        @Override public String body() { return body; }
        @Override public Optional<SSLSession> sslSession() { return Optional.empty(); }
        @Override public URI uri() { return request.uri(); }
        @Override public Version version() { return Version.HTTP_2; }
    }
}
