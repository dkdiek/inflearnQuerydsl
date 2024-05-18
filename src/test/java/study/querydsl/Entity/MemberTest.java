package study.querydsl.Entity;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * study.querydsl.Entity MemberTest
 *
 * @author : K
 */
@SpringBootTest
@Transactional
class MemberTest {
  @PersistenceContext EntityManager em;

  @Test
  public void testEntity() {
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
    
    //초기화
    em.flush();
    em.clear();
    
    List<Member> result = em.createQuery("select m from Member m", Member.class).getResultList();
    
    for (Member member : result) {
      System.out.println(member);
      System.out.println(member.getTeam());
    }
    
  }
}
