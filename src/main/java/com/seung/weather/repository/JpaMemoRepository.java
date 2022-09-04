package com.seung.weather.repository;

import com.seung.weather.domain.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
//<> 안의 Integer 는 key 의 형식을 뜻함
public interface JpaMemoRepository extends JpaRepository<Memo, Integer> {
  //헉 이거면 된다구????????????????????????

}
