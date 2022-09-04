package com.seung.weather;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.seung.weather.domain.Memo;
import com.seung.weather.repository.JdbcMemoRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest //이건 테스트입니다
@Transactional //테스트 코드를 돌려도 그 결과를 다시 원상복구 시키는 어노테이션
public class JdbcMemoRepositoryTest {

  @Autowired
  JdbcMemoRepository jdbcMemoRepository;

  @Test
  void insertMemoTest() {

    //테스트 코드 양식

    //given 주어진 것에
    Memo newMemo = new Memo(2, "inset memo test");

    //when 아래의 것을 했을 때
    jdbcMemoRepository.save(newMemo);

    //then 이렇게 된다
    Optional<Memo> result = jdbcMemoRepository.findById(2);
    assertEquals(result.get().getText(), "inset memo test");
  }

  @Test
  void findAllMemoTest() {
    List<Memo> memoList = jdbcMemoRepository.findAll();
    System.out.println(memoList);
    assertNotNull(memoList);
  }



}
