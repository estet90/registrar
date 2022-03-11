package ru.craftysoft.registrar.provider;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.core.interception.jaxrs.SuspendableContainerRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.annotation.Priority;
import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static ru.craftysoft.registrar.constant.MdcKey.SPAN_ID;
import static ru.craftysoft.registrar.constant.MdcKey.TRACE_ID;

@Priority(Integer.MIN_VALUE)
@Provider
@Slf4j
@Singleton
public class RequestLoggingFilter implements ContainerRequestFilter {

    private static final Logger requestLogger = LoggerFactory.getLogger("ru.craftysoft.registrar.server.response");

    @Override
    public void filter(ContainerRequestContext requestContext) {
        var suspendableContainerRequestContext = (SuspendableContainerRequestContext) requestContext;
        suspendableContainerRequestContext.suspend();
        if (requestContext.getUriInfo().getPath().contains("/swagger")) {
            suspendableContainerRequestContext.resume();
            return;
        }
        var mdc = Map.of(
                TRACE_ID, UUID.randomUUID().toString(),
                SPAN_ID, UUID.randomUUID().toString()
        );
        requestContext.setProperty("mdc", mdc);
        if (requestLogger.isDebugEnabled()) {
            MDC.setContextMap(mdc);
            try {
                var headers = suspendableContainerRequestContext.getHeaders();
                if (requestLogger.isTraceEnabled()) {
                    try (var bodyStream = suspendableContainerRequestContext.getEntityStream()) {
                        var byteStream = new ByteArrayOutputStream();
                        IOUtils.copy(bodyStream, byteStream);
                        var body = byteStream.toString(StandardCharsets.UTF_8.name());
                        if (nonNull(body) && body.length() > 0) {
                            requestLogger.trace("""
                                            URL={}
                                            Headers={}
                                            Body={}""",
                                    resolveUrl(suspendableContainerRequestContext), headers, body);
                        } else {
                            requestLogger.trace("""
                                            URL={}
                                            Headers={}""",
                                    resolveUrl(suspendableContainerRequestContext), headers);
                        }
                        suspendableContainerRequestContext.setEntityStream(new ByteArrayInputStream(byteStream.toByteArray()));
                    } catch (Exception e) {
                        log.error("RequestLoggingFilter.filter.thrown", e);
                    }
                } else {
                    requestLogger.debug("""
                                    URL={}
                                    Headers={}""",
                            resolveUrl(suspendableContainerRequestContext), headers);
                }
            } finally {
                MDC.clear();
            }
        }
        suspendableContainerRequestContext.resume();
    }

    private String resolveUrl(SuspendableContainerRequestContext suspendableContainerRequestContext) {
        var url = suspendableContainerRequestContext.getUriInfo().getAbsolutePath().toString();
        var queryParameters = suspendableContainerRequestContext.getUriInfo().getQueryParameters().entrySet().stream()
                .map(entry -> {
                    if (entry.getValue().size() == 1) {
                        return entry.getKey() + "=" + entry.getValue().get(0);
                    }
                    return entry.getValue().stream()
                            .map(value -> entry.getKey() + "=" + value)
                            .collect(Collectors.joining("&"));
                })
                .collect(Collectors.joining("&"));
        if (!queryParameters.isEmpty()) {
            return url + "?" + queryParameters;
        }
        return url;
    }
}
