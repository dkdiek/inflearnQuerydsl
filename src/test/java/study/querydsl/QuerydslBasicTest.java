package study.querydsl;

import static com.querydsl.jpa.JPAExpressions.*;
import static org.assertj.core.api.Assertions.*;
import static study.querydsl.Entity.QMember.*;
import static study.querydsl.Entity.QTeam.*;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.Entity.Member;
import study.querydsl.Entity.QMember;
import study.querydsl.Entity.Team;

@SpringBootTest
@Transactional
public class QuerydslBasicTest {
  @Autowired EntityManager em;
  JPAQueryFactory queryFactory;

  @BeforeEach
  public void before() {
    queryFactory = new JPAQueryFactory(em);

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
  public void sort() {
    em.persist(new Member(null, 100));
    em.persist(new Member("member5", 100));
    em.persist(new Member("member6", 100));

    queryFactory
        .selectFrom(member)
        .where(member.age.eq(100))
        .orderBy(member.age.desc(), member.username.asc().nullsLast())
        .fetch();
  }

  @Test
  public void paging1() {
    List<Member> result =
        queryFactory.selectFrom(member).orderBy(member.username.desc()).offset(1).limit(2).fetch();
  }

  @Test
  public void paging2() {
    Long totalCount =
        queryFactory.select(member.count()).from(member).offset(1).limit(2).fetchOne();
  }

  @Test
  public void aggregation() {
    List<Tuple> result =
        queryFactory
            .select(
                member.count(),
                member.age.sum(),
                member.age.avg(),
                member.age.max(),
                member.age.min())
            .from(member)
            .fetch();

    Tuple tuple = result.get(0);
    assertThat(tuple.get(member.count())).isEqualTo(4);
    assertThat(tuple.get(member.age.sum())).isEqualTo(100);
    assertThat(tuple.get(member.age.avg())).isEqualTo(25);
    assertThat(tuple.get(member.age.max())).isEqualTo(40);
    assertThat(tuple.get(member.age.min())).isEqualTo(10);
  }

  @Test
  void group() throws Exception {
    List<Tuple> result =
        queryFactory
            .select(team.name, member.age.avg())
            .from(member)
            .join(member.team, team)
            .groupBy(team.name)
            .fetch();
    Tuple teamA = result.get(0);
    Tuple teamB = result.get(1);

    assertThat(teamA.get(team.name)).isEqualTo("teamA");
    assertThat(teamA.get(member.age.avg())).isEqualTo(15);

    assertThat(teamB.get(team.name)).isEqualTo("teamB");
    assertThat(teamB.get(member.age.avg())).isEqualTo(35);
  }

  @Test
  void join() throws Exception {
    List<Member> result =
        queryFactory
            .selectFrom(member)
            .join(member.team, team)
            .where(team.name.eq("teamA"))
            .fetch();

    assertThat(result).extracting("username").containsExactly("member1", "member2");
  }

  @Test
  void theta_join() throws Exception {
    em.persist(new Member("teamA"));
    em.persist(new Member("teamB"));

    List<Member> result =
        queryFactory.select(member).from(member, team).where(member.username.eq(team.name)).fetch();
  }

  @Test
  void join_on_filtering() throws Exception {
    List<Tuple> result =
        queryFactory
            .select(member, team)
            .from(member)
            .leftJoin(member.team, team)
            .on(team.name.eq("teamA"))
            .fetch();
    for (Tuple tuple : result) {
      System.out.println(tuple);
    }
  }

  @Test
  void join_on_no_relation() throws Exception {
    em.persist(new Member("teamA"));
    em.persist(new Member("teamB"));
    em.persist(new Member("teamC"));

    /// *    List<Tuple> result =
    //        queryFactory
    //            .select(member, team)
    //            .from(member)
    //            .join(team)
    //            .on(member.username.eq(team.name))
    //            .fetch();*/
    List<Tuple> result =
        queryFactory
            .select(member, team)
            .from(member)
            .leftJoin(team)
            .on(member.username.eq(team.name))
            .fetch();

    for (Tuple tuple : result) {
      System.out.println("tuple= " + tuple.toString());
    }
  }

  @PersistenceUnit EntityManagerFactory emf;

  @Test
  void fetchJoinNo() throws Exception {
    em.flush();
    em.clear();

    Member findMember =
        queryFactory.selectFrom(member).where(member.username.eq("member1")).fetchOne();
    boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
    assertThat(loaded).as("페치 조인 미적용").isFalse();
  }

  @Test
  void fetchJoinUse() throws Exception {
    em.flush();
    em.clear();

    Member findMember =
        queryFactory
            .selectFrom(member)
            .join(member.team, team)
            .fetchJoin()
            .where(member.username.eq("member1"))
            .fetchOne();

    boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
    assertThat(loaded).as("페치 조인 미적용").isTrue();
  }

  @Test
  void subQuery() throws Exception {

    QMember memberSub = new QMember("memberSub");

    List<Member> result =
        queryFactory
            .selectFrom(member)
            .where(member.age.eq(select(memberSub.age.max()).from(memberSub)))
            .fetch();

    assertThat(result).extracting("age").containsExactly(40);
  }

  @Test
  void subQueryGoe() throws Exception {

    QMember memberSub = new QMember("memberSub");

    List<Member> result =
        queryFactory
            .selectFrom(member)
            .where(member.age.goe(select(memberSub.age.avg()).from(memberSub)))
            .fetch();

    assertThat(result).extracting("age").containsExactly(30, 40);
  }

  @Test
  void subQueryIn() throws Exception {

    QMember memberSub = new QMember("memberSub");

    List<Member> result =
        queryFactory
            .selectFrom(member)
            .where(
                member.age.in(
                    select(memberSub.age)
                        .from(memberSub)
                        .where(memberSub.age.gt(10))))
            .fetch();

    assertThat(result).extracting("age").containsExactly(20, 30, 40);
  }

  @Test
  void selectSubQuery() throws Exception {

    QMember memberSub = new QMember("memberSub");

    List<Tuple> fetch =
        queryFactory
            .select(member.username,
                    select(
                                    memberSub.age.avg())
                            .from(memberSub)
            )
            .from(member)
            .fetch();
    for (Tuple tuple : fetch) {
      System.out.println("username = " + tuple.get(member.username));
      System.out.println(
          "age = " + tuple.get(select(memberSub.age.avg()).from(memberSub)));
    }
  }
}
