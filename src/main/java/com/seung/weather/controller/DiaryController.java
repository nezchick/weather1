package com.seung.weather.controller;

import com.seung.weather.domain.Diary;
import com.seung.weather.service.DiaryService;
import java.time.LocalDate;
import java.util.List;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
public class DiaryController {

  private final DiaryService diaryService;

  public DiaryController(DiaryService diaryService) {
    this.diaryService = diaryService;
  }

  //get 조회 post 저장
  @ApiOperation("날짜와 일기 내용을 받아 일기를 생성하는 api")
  @PostMapping("/create/diary")
  void createDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text) {
    diaryService.createDiary(date, text);
  }

  @ApiOperation("입력한 날짜의 모든 일기를 가져온다.")
  @GetMapping("/read/diary")
  List<Diary> readDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    return diaryService.readDiary(date);
  }

  @ApiOperation("입력한 두 날짜 사이의 모든 일기를 가져온다.")
  @GetMapping("/read/diaries")
  List<Diary> readDiaries(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "조회할 기간의 첫번째 날", example = "2022-09-09") LocalDate startDate,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @ApiParam(value = "조회할 기간의 마지막 날", example = "2022-09-09") LocalDate endDate) {

    return diaryService.readDiaries(startDate,endDate);

  }

  @ApiOperation("입력한 날짜의 첫번째 일기의 내용을 입력한 내용으로 업데이트한다.")
  @PutMapping("/update/diary")
  void updateDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, @RequestBody String text) {
    diaryService.updateDiary(date, text);
  }

  @ApiOperation("입력한 날짜의 모든 일기를 삭제한다.")
  @DeleteMapping("/delete/diary")
  void deleteDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)  {
    diaryService.deleteDiary(date);
  }


}
