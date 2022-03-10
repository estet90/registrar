package ru.craftysoft.registrar.error;

import lombok.extern.slf4j.Slf4j;
import ru.craftysoft.registrar.rest.model.ErrorResponseData;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Slf4j
public class ExceptionHandler implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        log.error("ExceptionHandler.toResponse.thrown", exception);
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponseData().message(exception.getMessage()))
                .build();
    }
}
