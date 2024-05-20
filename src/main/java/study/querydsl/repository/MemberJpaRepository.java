package study.querydsl.repository;

import static study.querydsl.Entity.QMember.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import study.querydsl.Entity.Member;

/**
 * study.querydsl.repository MemberJpaRepository
 *
 * @author : K
 */
@Repository
public class MemberJpaRepository {

  private final EntityManager em;
  private final JPAQueryFactory queryFactory;

  public MemberJpaRepository(EntityManager em) {
    this.em = em;
    this.queryFactory = new JPAQueryFactory(em);
  }

  public void save(Member member) {
    em.persist(member);
  }

  public Optional<Member> findById(Long id) {
    Member findMember = em.find(Member.class, id);
    return Optional.ofNullable(findMember);
  }

  public List<Member> findAll(){
    return em.createQuery("select m from Member m", Member.class)
            .getResultList();
  }
  
  public List<Member> findAll_Querydsl(){
    return queryFactory.selectFrom(member).fetch();
  }

  
  public List<Member> findByUsername(String username){
    return em.createQuery("select m from Member m where m.username = :username", Member.class)
        .setParameter("username", username)
        .getResultList();
  }  public List<Member> findByUsername_Querydsl(String username){
    return queryFactory.selectFrom(member).where(member.username.eq(username)).fetch();
  }

}