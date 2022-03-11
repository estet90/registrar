package ru.craftysoft.registrar.provider;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.core.interception.jaxrs.SuspendableContainerResponseContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import ru.craftysoft.registrar.util.Jackson;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Priority(Integer.MIN_VALUE)
@Provider
@NoArgsConstructor
@Singleton
@Slf4j
public class ResponseLoggingFilter implements ContainerResponseFilter {

    private static final Logger responseLogger = LoggerFactory.getLogger("ru.craftysoft.registrar.server.response");

    private Jackson jackson;

    @Inject
    public ResponseLoggingFilter(Jackson jackson) {
        this.jackson = jackson;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        var suspendableContainerResponseContext = (SuspendableContainerResponseContext) responseContext;
        suspendableContainerResponseContext.suspend();
        if (requestContext.getUriInfo().getPath().contains("/swagger")) {
            suspendableContainerResponseContext.resume();
            return;
        }
        if (responseLogger.isDebugEnabled()) {
            try {
                ofNullable(requestContext.getProperty("mdc"))
                        .map(mdc -> {
                            try {
                                return (Map<String, String>) mdc;
                            } catch (Exception e) {
                                return null;
                            }
                        })
                        .ifPresentOrElse(MDC::setContextMap, MDC::clear);
                int status = responseContext.getStatus();
                var headers = responseContext.getHeaders();
                if (responseLogger.isTraceEnabled()) {
                    var body = extractBody(responseContext);
                    if (body != null) {
                        responseLogger.trace("""
                                        Status={}
                                        Headers={}
                                        Body={}""",
                                status, headers, body);
                    } else {
                        responseLogger.trace("""
                                        Status={}
                                        Headers={}""",
                                status, headers);
                    }
                } else {
                    responseLogger.debug("""
                                    Status={}
                                    Headers={}""",
                            status, headers);
                }
            } finally {
                MDC.clear();
            }
        }
        suspendableContainerResponseContext.resume();
    }

    private Object extractBody(ContainerResponseContext responseContext) {
        try {
            return ofNullable(responseContext.getEntity())
                    .map(entity -> responseContext.getHeaders().getOrDefault(CONTENT_TYPE, List.of())
                            .stream()
                            .map(MediaType.class::cast)
                            .filter(contentType -> contentType.toString().contains(APPLICATION_JSON))
                            .findFirst()
                            .map(contentType -> {
                                try {
                                    return jackson.toString(responseContext.getEntity());
                                } catch (Exception e) {
                                    log.error("ResponseLoggingFilter.filter.thrown невозможно преобразовать {} в JSON", responseContext.getEntity().getClass(), e);
                                    return null;
                                }
                            })
                            .map(Object.class::cast)
                            .orElse(entity)
                    ).orElse(null);
        } catch (Exception e) {
            log.error("ResponseLoggingFilter.filter.thrown", e);
            return null;
        }
    }
}
