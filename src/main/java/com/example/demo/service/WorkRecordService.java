package com.example.demo.service;
import com.example.demo.model.UserEntity;
import com.example.demo.model.WorkRecordEntity;
import com.example.demo.model.WorkRecordDto;
import com.example.demo.model.UserInfoResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class WorkRecordService {
    @Autowired
    private final UserRepository userRepository;
    private final WorkRecordRepository workRecordRepository;

    @Autowired
    public  WorkRecordService (UserRepository userRepository ,WorkRecordRepository workRecordRepository){
        this.userRepository = userRepository;
        this.workRecordRepository = workRecordRepository;
    }
    DateTimeFormatter DTF_HHMM = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter DTF_MMDD = DateTimeFormatter.ofPattern("MM:dd");


    public UserInfoResponse workSubmitInit(String username) {
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        Long todayRecordId = 0L;

        Optional<UserEntity> userRecord = userRepository.findByUsername(username);
        String workPlace = userRecord.get().getWorkPlace();

        //ユーザーの最新のレコードを取得　　　　　　　　
        WorkRecordEntity userWorkRecordEntity = workRecordRepository.findTopByUsernameOrderByClockInTimeDesc(username);
        if ((!(userWorkRecordEntity == null)) && (userWorkRecordEntity.getClockInTime() != null)) {
            //当日のレコードがあるか判定
            if (userWorkRecordEntity.getClockInTime().toLocalDate().equals(LocalDate.now())) {
                todayRecordId = userWorkRecordEntity.getId();
            }
        }
        userInfoResponse.setUsername(username);
        userInfoResponse.setTodayRecordId(todayRecordId);
        userInfoResponse.setWorkPlace(workPlace);
        return userInfoResponse;
    }
    public WorkRecordDto handleGetWorkRecord(UserEntity userEntity, Long id){
        WorkRecordDto res = new WorkRecordDto();
        WorkRecordEntity userWorkRecordEntity;
        if (userEntity == null) {
            throw new RuntimeException("SESSION_TIMEOUT");
        }
        try {
            //ユーザーの最新のレコードを取得
            userWorkRecordEntity = workRecordRepository.findById(id)
                    .orElseThrow(() -> null);
        } catch (Exception e) {
            throw new RuntimeException("ERROR_FETCH_RECORD");
        }
        if (userWorkRecordEntity != null){
            if (userWorkRecordEntity.getClockInTime() != null){
                res.setClockInTime(userWorkRecordEntity.getClockInTime());
            }
            if(userWorkRecordEntity.getClockOutTime() != null){
                res.setClockOutTime(userWorkRecordEntity.getClockOutTime());
            }
        }
        return res;
    }

    public WorkRecordDto handleClockIn(UserEntity userEntity){
        WorkRecordDto workRecordDto = new WorkRecordDto();
        WorkRecordEntity userWorkRecordEntity;
        boolean gpsResult = false;
        if (userEntity == null) {
            throw new RuntimeException("SESSION_TIMEOUT"); //コントローラでMg設定
        }
        //位置判定結果を取得
//        gpsResult = checkGps(lat,lon,userName);
        //出勤登録
        try {
            registerClockIn(userEntity.getUsername(),gpsResult);
        } catch (Exception e) {
            throw new RuntimeException("clockInResult エラーが発生しました");
        }
        //ユーザーの最新のレコードを取得
        try {
            userWorkRecordEntity = workRecordRepository.findTopByUsernameOrderByClockInTimeDesc(userEntity.getUsername());
        } catch (Exception e) {
            throw new RuntimeException("レコードを取得エラーが発生しました");
        }
        //レスポンス設定
        workRecordDto.setId(userWorkRecordEntity.getId());
        workRecordDto.setMessage("出勤打刻しました");
        workRecordDto.setClockInTime(userWorkRecordEntity.getClockInTime());
        return workRecordDto;
    }

    //出勤登録
    public void registerClockIn(String userName,boolean gpsResult){
        //DBへ出勤記録を登録
        WorkRecordEntity record = new WorkRecordEntity();
        record.setUsername(userName);
        record.setClockInTime(LocalDateTime.now());
        record.setClockInJudge(gpsResult);
        workRecordRepository.save(record);
    }

    //退勤登録
    public WorkRecordDto handleClockOut(Long id, String userName, double lat, double lon){
        WorkRecordDto workRecordDto = new WorkRecordDto();
        WorkRecordEntity userWorkRecordEntity;

        boolean gpsResult = false;
        //位置判定処理の結果
        gpsResult = checkGps (lat,lon,userName);
        //DBへ退勤記録を登録
        WorkRecordEntity record = workRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("レコードがみつかりません id:" + id));
        record.setClockOutTime(LocalDateTime.now());
        record.setClockOutJudge(gpsResult);
        workRecordRepository.save(record);
        //ユーザーの最新のレコードを取得
        try {
            userWorkRecordEntity = workRecordRepository.findTopByUsernameOrderByClockInTimeDesc(userName);
        } catch (Exception e) {
            throw new RuntimeException("レコード取得エラーが発生しました");
        }
        //レスポンス設定
        workRecordDto.setClockOutTime(userWorkRecordEntity.getClockOutTime());
        workRecordDto.setMessage("退勤打刻しました");
        return workRecordDto;
    }
    //DBから出退勤記録取消
    public WorkRecordDto deleteWorkRecord(long id){
        WorkRecordEntity record = workRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("レコードがみつかりません id:" + id));
        workRecordRepository.delete(record);
        WorkRecordDto workRecordDto = new WorkRecordDto();
        workRecordDto.setMessage("取り消しました");
        return workRecordDto;
    }
    //ユーザーの勤務記録を取得しソートして返す
    public List<WorkRecordDto> getUserRecordsDto(String username){
        List<WorkRecordEntity> userRecordsList = workRecordRepository.findByUsername(username, Sort.by(Sort.Order.asc("clockInTime")) );
        List<WorkRecordDto> workRecordDtoList = new ArrayList<>();
        for ( WorkRecordEntity workRecordEntity :userRecordsList) {
            WorkRecordDto dto = new WorkRecordDto();
            if (workRecordEntity.getClockInTime() != null){
                //日付計算用のため　フォーマットなしの日時を渡す
                dto.setClockInTime(workRecordEntity.getClockInTime());
                dto.setClockOutTime(workRecordEntity.getClockOutTime());
            }
            workRecordDtoList.add(dto);
        }
        return workRecordDtoList;
    }

    //ユーザーの勤務記録を取得しソートして返す
    public List<WorkRecordEntity> getUserRecordsByUsernameSort(String username){
        return workRecordRepository.findByUsername(username, Sort.by(Sort.Order.asc("clockInTime")) );
    }

    //管理者判定
    public boolean judgeRoles(String userName){
        Optional<UserEntity> user = userRepository.findByUsername(userName);
        boolean isAdmin = user.get().getRoles().contains("ADMIN");
        System.out.println("work_records 管理者判定　："+userName+" : "+isAdmin);
        return isAdmin;
    }

    //位置判定処理　（現在地の緯度、経度、ユーザー名）
    public boolean checkGps(Double nowLat, Double nowLon, String userName) {
        final int R = 6371000; // 地球の半径 (メートル)
        double allowedDistance = 1000.0; // 許可範囲 (メートル)
        // ユーザー名からテーブルにあるユーザー情報を取得
        Optional<UserEntity> userEntity = userRepository.findByUsername(userName);
        double companyLat = userEntity.get().getCompanyLat();
        double companyLon = userEntity.get().getCompanyLon();
        // 緯度、経度の差をラジアンに変換
        double dLat = Math.toRadians(companyLat - nowLat);
        double dLon = Math.toRadians(companyLon - nowLon);
        // ハーサイン距離の計算
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(nowLat)) * Math.cos(Math.toRadians(companyLat)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        //　現在地と登録勤務地の距離
        double distance = R * c;
        System.out.println("現在地 :" + nowLat+"・"+nowLon);
        System.out.println("distance :" + distance);
        //現在地と勤務地の距離と許可範囲の比較結果を返す
        return distance <= allowedDistance;
    }
    public List<String> getAllUserName(){
        return userRepository.findAllUserName();
    }

    public WorkRecordEntity saveWorkRecord(WorkRecordEntity workRecordEntity){
        return workRecordRepository.save(workRecordEntity);
    }


    //全ての勤務記録を取得
    public List<WorkRecordEntity> getAllWorkRecords(){
        return workRecordRepository.findAll();
    }
    public List<UserEntity> getAllUserEntity(){
        return userRepository.findAll();
    }


//        public  List<WorkRecord> getAllWorkRecords(){
//        return workRecordRepository.findAll();
//    }
//    public Optional<WorkRecord> getWorkRecordById(long id){
//        return workRecordRepository.findById(id);
//    }


}
