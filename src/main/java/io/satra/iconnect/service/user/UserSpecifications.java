package io.satra.iconnect.service.user;

import io.satra.iconnect.entity.User;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserSpecifications {
    public static Specification<User> filterUsers(String email, String mobile, String firstName, String lastName) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (email != null) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%"+email+"%"));
            }

            if (mobile != null) {
                predicates.add(criteriaBuilder.like(root.get("mobile"), "%"+mobile+"%"));
            }

            if (firstName != null) {
                predicates.add(criteriaBuilder.like(root.get("firstName"), "%"+firstName+"%"));
            }

            if (lastName != null) {
                predicates.add(criteriaBuilder.like(root.get("lastName"), "%"+lastName+"%"));
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
