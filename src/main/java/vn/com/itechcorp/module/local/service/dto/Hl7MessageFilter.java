package vn.com.itechcorp.module.local.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.filter.BaseFilter;
import vn.com.itechcorp.module.local.entity.Hl7Message;
import vn.com.itechcorp.util.Util;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Hl7MessageFilter extends BaseFilter<Hl7Message, Long> {


    private String fromDate;

    private String toDate;

    private String message;

    private String messageType;


    @Override
    public Predicate toPredicate(Root<Hl7Message> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        final List<Predicate> predicates = new ArrayList<>();
        if (fromDate != null) {
            try {
                Date fromDateParsed = Util.yyyyMMddHHmmss.get().parse(fromDate);
                predicates.add(builder.greaterThanOrEqualTo(root.<Date>get("messageTime"), fromDateParsed));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        if (toDate != null) {
            try {
                Date toDateParsed = Util.yyyyMMddHHmmss.get().parse(toDate);
                predicates.add(builder.greaterThanOrEqualTo(root.<Date>get("messageTime"), toDateParsed));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        if (message != null) predicates.add(builder.like(root.get("hl7"), "%" + message + "%"));

        if (messageType != null)
            predicates.add(builder.like(root.get("messageType"), "%" + messageType.toUpperCase() + "%"));
        return builder.and(predicates.toArray(new Predicate[0]));
    }

    @Override
    public String toString() {
        return "Hl7MessageFilter{" +
                "fromDate='" + fromDate + '\'' +
                ", toDate='" + toDate + '\'' +
                ", message='" + message + '\'' +
                ", messageType='" + messageType + '\'' +
                '}';
    }
}
