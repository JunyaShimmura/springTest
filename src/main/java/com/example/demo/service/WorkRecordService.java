package com.example.demo.service;
import com.example.demo.model.UserEntity;
import com.example.demo.model.WorkRecord;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkRecordRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
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

    //出勤登録し位置判定結果を返す
    public boolean clockInResult(String userName,double lat,double lon){
        //位置判定結果を取得
        boolean gpsResult = checkGps(lat,lon,userName);
        //DBへ出勤記録を登録
        WorkRecord record = new WorkRecord();
        record.setUsername(userName);
        record.setClockInTime(LocalDateTime.now());
        record.setClockInJudge(gpsResult);
        workRecordRepository.save(record);
        return gpsResult;
    }

    //退勤登録し位置判定結果を返す
    public boolean clockOutResult(Long id, String userName,double lat,double lon){
        WorkRecord record = workRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("レコードがみつかりません id:" + id));
        //位置判定処理の結果
        boolean gpsResult = checkGps (lat,lon,userName);
        //DBへ退勤記録を登録
        record.setClockOutTime(LocalDateTime.now());
        record.setClockOutJudge(gpsResult);
        workRecordRepository.save(record);
        return gpsResult;
    }
    //ユーザーの勤務記録を取得しソートして返す
    public List<WorkRecord> getUserRecordsByUsernameSort(String username){
        return workRecordRepository.findByUsername(username, Sort.by(Sort.Order.asc("clockInTime")) );
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

//        public  List<WorkRecord> getAllWorkRecords(){
//        return workRecordRepository.findAll();
//    }
//    public Optional<WorkRecord> getWorkRecordById(long id){
//        return workRecordRepository.findById(id);
//    }
//    public WorkRecord saveWorkRecord(WorkRecord workRecord){
//        return workRecordRepository.save(workRecord);
//    }
//    public void deleteWorkRecord(long id){
//        workRecordRepository.deleteById(id);
//    }

}
