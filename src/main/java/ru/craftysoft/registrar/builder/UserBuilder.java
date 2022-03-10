package ru.craftysoft.registrar.builder;

import ru.craftysoft.registrar.dto.User;
import ru.craftysoft.registrar.rest.model.UsersCreateRequestData;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@ApplicationScoped
public class UserBuilder {

    @NotNull
    public User buildCreated(UsersCreateRequestData usersCreateRequestData) {
        var user = new User();
        user.setFirstName(usersCreateRequestData.getFirstName());
        user.setLastName(usersCreateRequestData.getLastName());
        user.setMiddleName(usersCreateRequestData.getMiddleName());
        user.setCitizenship(usersCreateRequestData.getCitizenship());
        user.setCreatedAt(OffsetDateTime.now());
        user.setBirthDate(usersCreateRequestData.getBirthDate());
        user.setEmail(usersCreateRequestData.getEmail());
        user.setPassword(usersCreateRequestData.getPassword());
        user.setPassport(usersCreateRequestData.getPassport());
        user.setPassportExpiryDate(usersCreateRequestData.getPassportExpiryDate());
        var request = usersCreateRequestData.getRequest();
        user.setVisaRequestCities(request.getCities());
        user.setVisaRequestDateFrom(request.getFrom());
        user.setVisaRequestDateTo(request.getTo());
        return user;
    }

}
