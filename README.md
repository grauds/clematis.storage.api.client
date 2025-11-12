# Clematis Storage API Client (Spring Boot 3 Library)

[![Gradle Package](https://github.com/grauds/clematis.storage.api.client/actions/workflows/gradle-publish.yml/badge.svg?branch=master)](https://github.com/grauds/clematis.storage.api.client/actions/workflows/gradle-publish.yml)

A lightweight Spring Cloud OpenFeign client for integrating with the **Clematis Storage API** service.

## Features
- Type-safe Feign client with DTOs
- Unit and integration tests (MockWebServer)
- Dockerized runtime support
- Reusable Spring Boot 3 library (Java 17+)
- CI/CD via GitHub Actions

## Usage

Add as dependency (replace coordinates if you publish elsewhere):

```groovy
implementation 'org.clematis.storage:storage-api-client:1.0.0'
```

In your `application.yml`:

```yaml
storage:
  api:
    url: https://api.yourdomain.com
```

Then inject and use:

```java
@Service
@RequiredArgsConstructor
public class FileService {
    private final StorageApiClient storageApiClient;

    public List<FileMetadata> getFiles() {
        return storageApiClient.getAllFiles();
    }
}
```

## Docker

```bash
docker-compose up --build
```

## Run tests

```bash
./gradlew test
```
