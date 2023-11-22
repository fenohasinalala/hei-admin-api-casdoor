package school.hei.haapi.repository.dao;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;
import school.hei.haapi.model.AwardedCourse;
import school.hei.haapi.model.Course;
import school.hei.haapi.model.User;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;


@Repository
@AllArgsConstructor
public class AwardedCourseDao {
  private final EntityManager entityManager;
  //todo: to review

  public List<AwardedCourse> findByCriteria(String teacher_id, String course_id, Pageable pageable) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<AwardedCourse> query = builder.createQuery(AwardedCourse.class);
    Root<AwardedCourse> root = query.from(AwardedCourse.class);
    Join<AwardedCourse, User> teacher = root.join("mainTeacher", JoinType.LEFT);
    Join<AwardedCourse, Course> Courses = root.join("course", JoinType.LEFT);

    List<Predicate> predicates = new ArrayList<>();

    if (teacher_id != null) {
      predicates.add(builder.or(builder.like(builder.lower(teacher.get("id")), "%" + teacher_id + "%"),
          builder.like(teacher.get("id"), "%" + teacher_id + "%")));
    }

    if (course_id != null) {
      predicates.add(builder.or(builder.like(builder.lower(Courses.get("id")), "%" + course_id + "%"),
              builder.like(Courses.get("id"), "%" + course_id + "%")));
    }

    query.where(builder.and(predicates.toArray(new Predicate[0]))).distinct(true);

    return entityManager
        .createQuery(query)
        .setFirstResult((pageable.getPageNumber()) * pageable.getPageSize())
        .setMaxResults(pageable.getPageSize()).getResultList();
  }
}
