package ru.craftysoft.registrar.builder;

import ru.craftysoft.registrar.rest.model.UsersCreateRequestData;
import ru.tsc.crm.customer.model.jooq.tables.records.UsersRecord;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@ApplicationScoped
public class UserBuilder {

    @NotNull
    public UsersRecord buildCreated(UsersCreateRequestData usersCreateRequestData) {
        var user = new UsersRecord();
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
        user.setVisaRequestCities(request.getCities().toArray(new String[]{}));
        user.setVisaRequestDateFrom(request.getFrom());
        user.setVisaRequestDateTo(request.getTo());
        return user;
    }

}
