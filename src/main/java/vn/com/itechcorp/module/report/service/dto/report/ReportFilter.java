package vn.com.itechcorp.module.report.service.dto.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.filter.BaseFilter;
import vn.com.itechcorp.module.report.persitance.Report;
import vn.com.itechcorp.module.report.persitance.Report_;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReportFilter extends BaseFilter<Report, Long> {

    private Boolean reCall;

    private Boolean hisStatus;

    private Integer numOfRetry;

    @Override
    public Predicate toPredicate(Root<Report> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        final List<Predicate> predicates = new ArrayList<>();

        if (reCall != null) predicates.add(builder.isFalse(root.get(Report_.RE_CALL)));

        if (hisStatus != null) predicates.add(builder.isFalse(root.get(Report_.HIS_STATUS)));

        if (numOfRetry != null) predicates.add(builder.lessThanOrEqualTo(root.get(Report_.NUM_OF_RETRIES),numOfRetry));

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
