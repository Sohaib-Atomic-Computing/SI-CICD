package io.satra.iconnect.service.application;

import io.satra.iconnect.entity.Application;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;

public class ApplicationSpecifications {
    public static Specification<Application> filterApplications(String name, Boolean isActive) {
        return (root, criteriaQuery,criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%"+name+"%"));
            }

            if (isActive != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), isActive));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
