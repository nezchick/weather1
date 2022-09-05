package com.seung.weather.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="memo")
public class Memo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  // strategy GenerationType 종류
  // auto 니 알아 해줘라
  // identity 기본적인 키생성은 db가 알아서 해줘라 스프링부트는 키생성을 하지 않음 그저 해준걸 가져올뿐
  private int id ;
  private String text ;

}
