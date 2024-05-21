package study.querydsl.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.Entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * study.querydsl.repository MemberRepositroy
 *
 * @author : K
 */
@SpringBootTest
@Transactional
@RequiredArgsConstructor
public class MemberRepositoryTest {

  private final MemberRepository memberRepository;

  @Test
  public void basicTest() {
    Member member = new Member("member1", 10);
    memberRepository.save(member);
    
    Member findMember = memberRepository.findById(member.getId()).get();
    assertThat(findMember).isEqualTo(member);
    
    List<Member> result1 = memberRepository.findAll();
    assertThat(result1).containsExactly(member);
    
    List<Member> result2 = memberRepository.findByUsername("member1");
    assertThat(result2).containsExactly(member);
  }

}

