package com.seung.weather.repository;

import com.seung.weather.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Integer> {

}
