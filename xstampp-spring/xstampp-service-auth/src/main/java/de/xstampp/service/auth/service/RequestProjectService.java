package de.xstampp.service.auth.service;

import de.xstampp.common.errors.ErrorsAuth;
import de.xstampp.common.service.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.UUID;

@Service
public class RequestProjectService {
    private static final String PROJECT_SERVICE_CONFIG_KEY = "services.project-manager";
    private static final String PATH_INTERNAL_PROJECT = "/internal/project/";

    private ConfigurationService configurationService;

    private HttpClient httpClient;
    private Logger logger;

    @Autowired
    public RequestProjectService(ConfigurationService configurationService) {
        this.httpClient = HttpClient.newHttpClient();
        this.configurationService = configurationService;
        this.logger = LoggerFactory.getLogger(RequestProjectService.class);
    }

    public void createProjectRemote(UUID projectId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(BodyPublishers.noBody())
                    .uri(endpointFor(projectId))
                    .build();

            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw ErrorsAuth.FAILED_PROJ_ADD.exc("http status code " + response.statusCode());
            }
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw ErrorsAuth.FAILED_PROJ_ADD.exc(e);
        }
    }

    public void deleteProjectRemote(UUID projectId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .DELETE()
                    .uri(endpointFor(projectId))
                    .build();

            logger.debug("Contacting project service to delete {}.", projectId);
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                throw ErrorsAuth.FAILED_PROJ_DEL.exc("http status code " + response.statusCode());
            }
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw ErrorsAuth.FAILED_PROJ_DEL.exc(e);
        }
    }

    public void exampleProjectRemote(UUID newProjectId, UUID clonedProjectId) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .POST(BodyPublishers.noBody())
                    .uri(new URI(endpointFor(newProjectId).toString() + "/clone/" + clonedProjectId))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
            if (httpResponse.statusCode() >= 400) {
                throw ErrorsAuth.FAILED_PROJ_ADD.exc("http status code " + httpResponse.statusCode());
            }
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw ErrorsAuth.FAILED_PROJ_ADD.exc(e);
        }
    }

    public void importProjectRemote(UUID projectId, String importString) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .POST(BodyPublishers.ofString(importString))
                    .uri(new URI(endpointFor(projectId).toString() + "/import/"))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
            if (httpResponse.statusCode() >= 400) {
                throw ErrorsAuth.FAILED_PROJ_ADD.exc("http status code " + httpResponse.statusCode());
            }
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw ErrorsAuth.FAILED_PROJ_ADD.exc(e);
        }
    }

    public void importEclipseProjectRemote(UUID projectId, String importString) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .POST(BodyPublishers.ofString(importString))
                    .uri(new URI(endpointFor(projectId).toString() + "/import/eclipse"))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
            if (httpResponse.statusCode() >= 400) {
                throw ErrorsAuth.FAILED_PROJ_ADD.exc("http status code " + httpResponse.statusCode());
            }
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw ErrorsAuth.FAILED_PROJ_ADD.exc(e);
        }
    }

    public void exampleProjectRemote(UUID projectId) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .POST(BodyPublishers.noBody())
                    .uri(new URI(endpointFor(projectId).toString() + "/example"))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
            if (httpResponse.statusCode() >= 400) {
                throw ErrorsAuth.FAILED_PROJ_ADD.exc("http status code " + httpResponse.statusCode());
            }
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw ErrorsAuth.FAILED_PROJ_ADD.exc(e);
        }
    }

    private URI endpointFor(UUID projectId) throws URISyntaxException {
        String endpoint = "http://" + configurationService.getStringProperty(PROJECT_SERVICE_CONFIG_KEY)
                + PATH_INTERNAL_PROJECT + projectId;
        return new URI(endpoint);
    }
}
