package study.querydsl.dto;

import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * study.querydsl.dto MemberDto
 *
 * @author : K
 */
@Setter
@NoArgsConstructor
public class MemberDto {

  private String username;
  private int age;
  
  public MemberDto(String username, int age) {
    this.username = username;
    this.age = age;
  }
}
