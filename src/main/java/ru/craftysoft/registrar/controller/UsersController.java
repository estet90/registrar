package ru.craftysoft.registrar.controller;

import io.smallrye.mutiny.Uni;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.spi.HttpRequest;
import org.slf4j.MDC;
import ru.craftysoft.registrar.logic.UserCreateOperation;
import ru.craftysoft.registrar.logic.UserFilterOperation;
import ru.craftysoft.registrar.rest.model.UsersCreateRequestData;
import ru.craftysoft.registrar.rest.model.UsersFilterStatus;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static ru.craftysoft.registrar.constant.MdcKey.OPERATION_NAME;

@Path("/users")
@NoArgsConstructor
public class UsersController implements UsersApi {

    private UserCreateOperation userCreateOperation;
    private UserFilterOperation userFilterOperation;

    @Inject
    public UsersController(UserCreateOperation userCreateOperation, UserFilterOperation userFilterOperation) {
        this.userCreateOperation = userCreateOperation;
        this.userFilterOperation = userFilterOperation;
    }

    @POST
    @Consumes({"application/json;charset=UTF-8"})
    @Produces({"application/json;charset=UTF-8"})
    @Override
    public Uni<Response> create(@Valid UsersCreateRequestData usersCreateRequestData,
                                @Context org.jboss.resteasy.spi.HttpRequest request) {
        try {
            mdcInit(request, "create");
            return userCreateOperation.process(usersCreateRequestData)
                    .map(responseData -> Response.status(Response.Status.CREATED)
                            .entity(responseData).build());
        } finally {
            MDC.clear();
        }
    }

    @GET
    @Path("/filter")
    @Produces({"application/json;charset=UTF-8"})
    @Override
    public Uni<Response> filter(@QueryParam("status") @DefaultValue("all") UsersFilterStatus status,
                                @Context org.jboss.resteasy.spi.HttpRequest request) {
        try {
            mdcInit(request, "filter");
            return userFilterOperation.process(status)
                    .map(filteredUsers -> Response.ok(filteredUsers).build());
        } finally {
            MDC.clear();
        }
    }

    private void mdcInit(HttpRequest request, String operationName) {
        ofNullable(request.getAttribute("mdc"))
                .map(mdc -> {
                    try {
                        return (Map<String, String>) mdc;
                    } catch (Exception e) {
                        return null;
                    }
                })
                .ifPresentOrElse(mdc -> {
                    var newMdc = new HashMap<>(mdc);
                    newMdc.put(OPERATION_NAME, operationName);
                    MDC.setContextMap(newMdc);
                }, MDC::clear);
    }

}
