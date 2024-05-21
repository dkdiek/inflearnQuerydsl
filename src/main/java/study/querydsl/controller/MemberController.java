package study.querydsl.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.querydsl.dto.MemberSearchCondition;
import study.querydsl.dto.MemberTeamDto;
import study.querydsl.repository.MemberJpaRepository;
import study.querydsl.repository.MemberRepository;

@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberJpaRepository memberJpaRepository;
  private final MemberRepository memberRepository;

  @GetMapping("/v1/members")
  public List<MemberTeamDto> searchMemberV1(MemberSearchCondition condition) {
    return memberJpaRepository.search(condition);
  }
  
  @GetMapping("/v2/members")
  public Page<MemberTeamDto> searchMemberV2(MemberSearchCondition condition, Pageable pageable) {
    final var start = System.currentTimeMillis();
    Page<MemberTeamDto> result = memberRepository.searchPageSimple(condition, pageable);
    final var end = System.currentTimeMillis();
    System.out.println("걸린시간" + (end-start));
    return result;
  }
  @GetMapping("/v3/members")
  public Page<MemberTeamDto> searchMemberV3(MemberSearchCondition condition, Pageable pageable) {
    final var start = System.currentTimeMillis();
    Page<MemberTeamDto> result = memberRepository.searchPageComplex(condition, pageable);
    final var end = System.currentTimeMillis();
    System.out.println("걸린시간" + (end-start));
    return result;  }
}
