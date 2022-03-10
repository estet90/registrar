package ru.craftysoft.registrar.builder;

import ru.craftysoft.registrar.rest.model.FilteredUser;
import ru.craftysoft.registrar.rest.model.VisaRequest;
import ru.craftysoft.registrar.rest.model.VisaResponse;
import ru.tsc.crm.customer.model.jooq.tables.records.UsersRecord;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FilteredUserBuilder {

    public List<FilteredUser> build(List<UsersRecord> users) {
        return users.stream()
                .map(user -> new FilteredUser()
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .middleName(user.getMiddleName())
                        .passport(user.getPassport())
                        .passportExpiryDate(user.getPassportExpiryDate())
                        .email(user.getEmail())
                        .citizenship(user.getCitizenship())
                        .createdAt(user.getCreatedAt())
                        .completedAt(user.getCompletedAt())
                        .rejectedAt(user.getRejectedAt())
                        .request(new VisaRequest()
                                .from(user.getVisaRequestDateFrom())
                                .to(user.getVisaRequestDateTo())
                                .cities(Arrays.stream(user.getVisaRequestCities()).collect(Collectors.toSet()))
                        )
                        .response(new VisaResponse()
                                .status(user.getVisaResponseStatus())
                                .body(user.getVisaResponseBody())
                        )
                )
                .toList();
    }

}
