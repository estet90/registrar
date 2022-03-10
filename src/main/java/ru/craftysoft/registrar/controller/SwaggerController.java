package ru.craftysoft.registrar.controller;

import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

@Path("/swagger")
@Produces("text/yaml;charset=UTF-8")
public class SwaggerController {

    private final byte[] swagger;

    public SwaggerController() throws IOException {
        this.swagger = requireNonNull(getClass().getResourceAsStream("/openapi/registrar.yaml")).readAllBytes();;
    }

    @GET
    @Path("/registrar.yaml")
    public Uni<byte[]> companySwagger() {
        return Uni.createFrom().item(swagger);
    }
}
