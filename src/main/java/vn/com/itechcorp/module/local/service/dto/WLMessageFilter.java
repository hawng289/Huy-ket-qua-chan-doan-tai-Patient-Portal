package vn.com.itechcorp.module.local.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.com.itechcorp.base.service.filter.BaseFilter;
import vn.com.itechcorp.module.local.entity.WorkListMessage;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class WLMessageFilter extends BaseFilter<WorkListMessage, Long> {
    private boolean succeed;
    private int retryCount;

    public WLMessageFilter(boolean succeed, int retryCount) {
        this.retryCount = retryCount;
        this.succeed = succeed;
    }

    @Override
    public Predicate toPredicate(Root<WorkListMessage> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        final List<Predicate> predicates = new ArrayList<>();
        if (!succeed) predicates.add(builder.isFalse(root.get("succeeded")));
        if (retryCount > 0) predicates.add(builder.lessThan(root.get("requestCount"), retryCount));
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
