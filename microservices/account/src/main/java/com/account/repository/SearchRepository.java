package com.account.repository;

import com.account.dto.response.UserDetailResponse;
import com.account.model.User;
import com.account.repository.criteria.SearchCriteria;
import com.account.repository.criteria.UserSearchQueryCriteriaConsumer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Component
public class SearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Search user by criteria
     *
     * @param params list of filter conditions
     * @return list of users
     */
    public Page<User> findAllUsersByCriteria(Pageable pageable, List<SearchCriteria> params) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> query = builder.createQuery(User.class);
        final Root<User> r = query.from(User.class);

        Predicate predicate = builder.conjunction();
        UserSearchQueryCriteriaConsumer searchConsumer = new UserSearchQueryCriteriaConsumer(predicate, builder, r);
        params.forEach(searchConsumer);
        predicate = searchConsumer.getPredicate();
        query.where(predicate);

        // This query fetches the Users as per the Page Limit
         List<User> userList = entityManager.createQuery(query)
                 .setFirstResult((int) pageable.getOffset())
                 .setMaxResults(pageable.getPageSize())
                 .getResultList();

        // Create count query
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<User> usersRootCount = countQuery.from(User.class);
        countQuery.select(builder.count(usersRootCount)).where(predicate);

        // Fetches the count of all User as per given criteria
        Long count = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(userList, pageable, count);
    }

    /**
     * Find users by customize JPA query
     *
     * @param firstName
     * @param lastName
     * @param gender
     * @param pageNo
     * @param pageSize
     * @return list of users
     */
    public Page<UserDetailResponse> findAllUsersByCustomizeQuery(String firstName, String lastName, Integer gender, int pageNo, int pageSize) {
        StringBuilder where = new StringBuilder(" WHERE 1=1");

        if (StringUtils.hasLength(firstName)) {
            where.append(" AND u.firstName=:firstName");
        }
        if (StringUtils.hasLength(lastName)) {
            where.append(" AND u.lastName=:lastName");
        }
        if (null != gender) {
            where.append(" AND u.gender=:gender");
        }

        // Get list of users
        Query selectQuery = entityManager.createQuery(String.format("SELECT new com.account.dto.response.UserDetailResponse(u.id, u.firstName, u.lastName, u.email, u.phone) FROM User u %s ORDER BY u.id DESC", where));
        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);
        if (where.toString().contains("AND")) {
            if (StringUtils.hasLength(firstName)) {
                selectQuery.setParameter(":firstName", firstName);
            }
            if (StringUtils.hasLength(lastName)) {
                selectQuery.setParameter(":lastName", lastName);
            }
            if (null != gender) {
                selectQuery.setParameter(":gender", gender);
            }
        }
        List<UserDetailResponse> userList = selectQuery.getResultList();

        // Count users
        Long count = (Long) entityManager.createQuery(String.format("SELECT COUNT(*) FROM User u %s", where)).getSingleResult();

        return new PageImpl<>(userList, PageRequest.of(pageNo,pageSize), count);
    }
}
