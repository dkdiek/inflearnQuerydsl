package study.querydsl;

import static org.assertj.core.api.Assertions.*;
import static study.querydsl.Entity.QMember.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.Entity.Member;
import study.querydsl.Entity.Team;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
  @Autowired EntityManager em;
  JPAQueryFactory queryFactory;

  @BeforeEach
  public void before() {
    JPAQueryFactory queryFactory = new JPAQueryFactory(em);

    Team teamA = new Team("teamA");
    Team teamB = new Team("teamB");
    em.persist(teamA);
    em.persist(teamB);

    Member member1 = new Member("member1", 10, teamA);
    Member member2 = new Member("member2", 20, teamA);
    Member member3 = new Member("member3", 30, teamB);
    Member member4 = new Member("member4", 40, teamB);
    em.persist(member1);
    em.persist(member2);
    em.persist(member3);
    em.persist(member4);
  }

  @Test
  public void startJpql() {
    String qlString = "select m from Member m where m.username = : username";
    Member findMember =
        (Member) em.createQuery(qlString).setParameter("username", "member1").getSingleResult();
    assertThat(findMember.getUsername()).isEqualTo("member1");
  }

  @Test
  public void startQuerydsl() {
    Member findMember =
        queryFactory.selectFrom(member).where(member.username.eq("member1")).fetchOne();

    assertThat(findMember.getUsername()).isEqualTo("member1");
  }

  @Test
  public void serach() {
    Member findMember =
        queryFactory
            .selectFrom(member)
            .where(member.username.eq("member1"), (member.age.eq(10)))
            .fetchOne();

    assertThat(findMember.getUsername()).isEqualTo("member1");
  }

  @Test
  public void resultFetch() {
    List<Member> fetch = queryFactory.selectFrom(member).fetch();
    Member fetchOne = queryFactory.selectFrom(member).fetchOne();
    Member fetchFirst = queryFactory.selectFrom(member).fetchFirst();
    Long totalCount = queryFactory.select(member.count()).from(member).fetchOne();
  }
  
  @Test
  public void sort(){
    em.persist(new Member(null,100));
    em.persist(new Member("member5",100));
    em.persist(new Member("member6",100));

    queryFactory
        .selectFrom(member)
        .where(member.age.eq(100))
        .orderBy(member.age.desc(), member.username.asc().nullsLast())
        .fetch();

  }

}
