package io.satra.iconnect.service.promotion;

import io.satra.iconnect.entity.Promotion;
import io.satra.iconnect.utils.TimeUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;

public class PromotionSpecifications {
    public static Specification<Promotion> filterPromotions(String name, Boolean status, String startDateFrom,
                                                              String startDateTo, String endDateFrom,
                                                              String endDateTo) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%"+name+"%"));
            }

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), status));
            }

            if (startDateFrom != null && startDateTo != null) {
                predicates.add(criteriaBuilder.between(root.get("startDate"), TimeUtils.convertStringToLocalDateTime(startDateFrom),
                        TimeUtils.convertStringToLocalDateTime(startDateTo)));
            }

            if (endDateFrom != null && endDateTo != null) {
                predicates.add(criteriaBuilder.between(root.get("endDate"), TimeUtils.convertStringToLocalDateTime(endDateFrom),
                        TimeUtils.convertStringToLocalDateTime(endDateTo)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
