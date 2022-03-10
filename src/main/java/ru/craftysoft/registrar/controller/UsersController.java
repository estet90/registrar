package ru.craftysoft.registrar.controller;

import io.smallrye.mutiny.Uni;
import lombok.NoArgsConstructor;
import ru.craftysoft.registrar.logic.UserCreateOperation;
import ru.craftysoft.registrar.logic.UserFilterOperation;
import ru.craftysoft.registrar.rest.model.UsersCreateRequestData;
import ru.craftysoft.registrar.rest.model.UsersFilterStatus;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

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
        return userCreateOperation.process(usersCreateRequestData)
                .map(responseData -> Response.status(Response.Status.CREATED)
                        .entity(responseData).build());
    }

    @GET
    @Path("/filter")
    @Produces({"application/json;charset=UTF-8"})
    @Override
    public Uni<Response> filter(@QueryParam("status") @DefaultValue("all") UsersFilterStatus status,
                                @Context org.jboss.resteasy.spi.HttpRequest request) {
        return userFilterOperation.process(status)
                .map(filteredUsers -> Response.ok(filteredUsers).build());
    }

}
