package com.seung.weather.service;


import com.seung.weather.WeatherApplication;
import com.seung.weather.domain.DateWeather;
import com.seung.weather.domain.Diary;
import com.seung.weather.error.InvalidDate;
import com.seung.weather.repository.DateWeatherRepository;
import com.seung.weather.repository.DiaryRepository;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class DiaryService {

  @Value("${openweathermap.key}")
  private String apiKey;

  private final DiaryRepository diaryRepository;

  private final DateWeatherRepository dateWeatherRepository;

  private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

  public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository) {
    this.diaryRepository = diaryRepository;
    this.dateWeatherRepository = dateWeatherRepository;
  }

  private DateWeather getWeatherFromApi() {
    // 날씨 데이터 가져오기
    String weatherData = getWeatherString();

    // 가져온 데이터 파싱하기
    Map<String, Object> parsedWeather = parseWeather(weatherData);

    DateWeather dateWeather = new DateWeather();
    dateWeather.setDate(LocalDate.now());
    dateWeather.setWeather(parsedWeather.get("main").toString());
    dateWeather.setIcon(parsedWeather.get("icon").toString());
    dateWeather.setTemperature((Double) parsedWeather.get("temp"));

    return dateWeather;
  }

  @Transactional
  @Scheduled(cron = "0 0 1 * * *") //"초 분 시 일 월 .." 0초 0분 1시 매일 매월 -> 매일 1시마다
  public void saveWeatherDate () {
    logger.info("날씨 정보를 잘 가져왔다.");

    dateWeatherRepository.save(getWeatherFromApi());
  }

  private DateWeather getDateWeather(LocalDate date) {
    List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
    if(dateWeatherListFromDB.size() == 0) {
      // 저장된 것이 없으므로 날씨정보를 가져와야 한다.
      return getWeatherFromApi();
    } else {
      return dateWeatherListFromDB.get(0);
    }
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  public void createDiary(LocalDate date, String text) {

    logger.info("started to create diary");

    // 날씨 데이터 가져오기 (DB 혹은 API 에서)
    DateWeather dateWeather = getDateWeather(date);

    // 날씨 데이터 + 일기 값 DB에 넣기
    Diary nowDiary = new Diary();
    nowDiary.setDateWeather(dateWeather);
    nowDiary.setText(text);
    nowDiary.setDate(date);

    diaryRepository.save(nowDiary);

    logger.info("end to create diary");

  }

  @Transactional(readOnly = true)
  public List<Diary> readDiary(LocalDate date) {
    logger.debug("read diary");

//    if(date.isAfter(LocalDate.ofYearDay(3050,1))){
//      throw new InvalidDate();
//    }
    return diaryRepository.findAllByDate(date);
  }

  public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
    return diaryRepository.findAllByDateBetween(startDate, endDate);
  }

  public void updateDiary(LocalDate date, String text) {
    Diary nowDiary = diaryRepository.getFirstByDate(date);
    nowDiary.setText(text);
    diaryRepository.save(nowDiary);
  }

  public void deleteDiary(LocalDate date) {
    diaryRepository.deleteAllByDate(date);
  }

  private String getWeatherString() {
    String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=cheongju&appid="+apiKey;
    try{

      URL url = new URL(apiUrl);
      // 요청을 보내기 위한 연결
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      // GET 요청 보냄
      connection.setRequestMethod("GET");
      // 보낸 요청에 대한 응답코드 받아옴
      int responseCode = connection.getResponseCode();
      BufferedReader br;
      if(responseCode == 200) {

        //성공시 응답 객체 받아옴
        br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      } else {

        //실패시 오류 메세지 받아옴
        br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
      }

      // -> 아무튼 둘 중 하나를 br에 받음

      String inputLine;
      StringBuilder response = new StringBuilder();
      while ((inputLine = br.readLine()) != null) {

        // response 라는 StringBuilder 에 하나씩 저장
        response.append(inputLine);
      }
      br.close();

      return response.toString();

    } catch (Exception e) {
      return "failed";
    }
  }

  private Map<String, Object> parseWeather(String jsonString) {
    JSONParser jsonParser = new JSONParser();
    JSONObject jsonObject;

    try{
      jsonObject = (JSONObject) jsonParser.parse(jsonString);
    }catch (ParseException e) {
      throw new RuntimeException(e);
    }

    Map<String, Object> resultMap = new HashMap<>();
    JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
    JSONObject weatherData = (JSONObject) weatherArray.get(0);
    JSONObject mainData = (JSONObject) jsonObject.get("main");
    resultMap.put("temp", mainData.get("temp"));
    resultMap.put("main", weatherData.get("main"));
    resultMap.put("icon", weatherData.get("icon"));

    return resultMap;
  }

}
