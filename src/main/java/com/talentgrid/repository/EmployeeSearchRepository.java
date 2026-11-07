package com.talentgrid.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import com.talentgrid.dto.EmployeeSearchCriteria;
import com.talentgrid.entity.Employee;
import com.talentgrid.entity.EmployeeSkill;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Repository
public class EmployeeSearchRepository {

    @PersistenceContext
    private EntityManager em;

    public Page<Employee> search(EmployeeSearchCriteria c, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // ---- 1) data query ----
        CriteriaQuery<Employee> dataQuery = cb.createQuery(Employee.class);
        Root<Employee> root = dataQuery.from(Employee.class);
        root.fetch("department", JoinType.INNER);
        root.fetch("designation", JoinType.INNER);
        root.fetch("manager", JoinType.LEFT);

        dataQuery.select(root)
                 .where(buildPredicates(cb, dataQuery, root, c).toArray(new Predicate[0]));

        List<Order> orders = new ArrayList<>();
        for (Sort.Order o : pageable.getSort()) {
            orders.add(o.isAscending() ? cb.asc(root.get(o.getProperty()))
                                       : cb.desc(root.get(o.getProperty())));
        }
        if (orders.isEmpty()) orders.add(cb.asc(root.get("id")));
        dataQuery.orderBy(orders);

        TypedQuery<Employee> typed = em.createQuery(dataQuery);
        typed.setFirstResult((int) pageable.getOffset());
        typed.setMaxResults(pageable.getPageSize());
        List<Employee> content = typed.getResultList();

        // ---- 2) count query (same filters, no fetch joins) ----
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Employee> countRoot = countQuery.from(Employee.class);
        countQuery.select(cb.count(countRoot))
                  .where(buildPredicates(cb, countQuery, countRoot, c).toArray(new Predicate[0]));
        Long total = em.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, CriteriaQuery<?> query,
                                            Root<Employee> root, EmployeeSearchCriteria c) {
        List<Predicate> list = new ArrayList<>();

        if (c.departmentId() != null)
            list.add(cb.equal(root.get("department").get("id"), c.departmentId()));

        if (c.status() != null)
            list.add(cb.equal(root.get("status"), c.status()));

        if (c.minExperience() != null)
            list.add(cb.greaterThanOrEqualTo(root.<Double>get("experienceYears"), c.minExperience()));

        if (c.name() != null && !c.name().isBlank()) {
            String like = "%" + c.name().trim().toLowerCase() + "%";
            list.add(cb.or(
                    cb.like(cb.lower(root.<String>get("firstName")), like),
                    cb.like(cb.lower(root.<String>get("lastName")), like)));
        }

        // "employee has skill X (at least level N)" using an EXISTS subquery
        if (c.skillId() != null) {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<EmployeeSkill> es = sub.from(EmployeeSkill.class);
            List<Predicate> sp = new ArrayList<>();
            sp.add(cb.equal(es.get("employee"), root));
            sp.add(cb.equal(es.get("skill").get("id"), c.skillId()));
            if (c.minProficiency() != null)
                sp.add(cb.greaterThanOrEqualTo(es.<Integer>get("proficiency"), c.minProficiency()));
            sub.select(cb.literal(1L)).where(sp.toArray(new Predicate[0]));
            list.add(cb.exists(sub));
        }

        return list;
    }
}