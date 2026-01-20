package com.example.demo.api;

import com.example.demo.model.UserEntity;
import com.example.demo.model.WorkRecord;
import com.example.demo.repository.WorkRecordRepository;
import com.example.demo.service.WorkRecordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceApiController {

    @Autowired
    private WorkRecordService workRecordService;
    @Autowired
    private WorkRecordRepository workRecordRepository;

    DateTimeFormatter DTF_HHMM = DateTimeFormatter.ofPattern("HH:mm");

    @PostMapping("/getWorkRecord/{id}")
    public ResponseEntity<?> getWorkRecord (@PathVariable Long id, HttpSession session) {
        // セッションから丸ごと保存したユーザー情報を取り出す
        UserEntity user = (UserEntity) session.getAttribute("user");
        WorkRecord userWorkRecord;
        String clockInTimeRecord ="";
        String clockOutTimeRecord ="";
        if (user == null) {
            return ResponseEntity.status(401).body("セッションが切れました。再ログインしてください。");
        }
        try {
            //ユーザーの最新のレコードを取得
            userWorkRecord = workRecordRepository.findById(id)
                    .orElseThrow(() -> null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("レコードを取得エラーが発生しました");
        }
        if (userWorkRecord != null){
            if (userWorkRecord.getClockInTime() != null){
                clockInTimeRecord = userWorkRecord.getClockInTime().format(DTF_HHMM);
            }
            if(userWorkRecord.getClockOutTime() != null){
                clockOutTimeRecord = userWorkRecord.getClockOutTime().format(DTF_HHMM);
            }
        }
        return ResponseEntity.ok(Map.of(
                "message", "レコードを取得",
                "clockInTime",clockInTimeRecord,
                "clockOutTime",clockOutTimeRecord
        ));
    }

    @PostMapping("/punch-in")
    public ResponseEntity<?> punchIn(HttpSession session) {
        // セッションから丸ごと保存したユーザー情報を取り出す
        UserEntity user = (UserEntity) session.getAttribute("user");
        WorkRecord userWorkRecord;
        boolean gpsResult;
        if (user == null) {
            return ResponseEntity.status(401).body("セッションが切れました。再ログインしてください。");
        }
        try {
            //出勤登録し位置判定結果を返す
            gpsResult = workRecordService.clockInResult(user.getUsername(),1.0,1.0);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("clockInResult エラーが発生しました");
        }
        try {
            //ユーザーの最新のレコードを取得
            userWorkRecord = workRecordRepository.findTopByUsernameOrderByClockInTimeDesc(user.getUsername());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("レコードを取得エラーが発生しました");
        }
        String clockInTimeRecord = userWorkRecord.getClockInTime().format(DTF_HHMM);

        return ResponseEntity.ok(Map.of(
                "message", "出勤打刻しました",
                "id",userWorkRecord.getId(),
                "clockInTime",clockInTimeRecord
        ));
    }
    @PostMapping("/punch-out/{id}")
    public ResponseEntity<?> punchOut(@PathVariable Long id,HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        WorkRecord userWorkRecord ;
        try {
            //ユーザーの最新のレコードを取得
            // 退勤登録 位置判定結果　workRecordService
            boolean gpsResult = workRecordService.clockOutResult(id,user.getUsername(),1.0,1.0);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("エラーが発生しました");
        }
        try {
            //ユーザーの最新のレコードを取得
            userWorkRecord = workRecordRepository.findTopByUsernameOrderByClockInTimeDesc(user.getUsername());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("レコードを取得エラーが発生しました");
        }
        String clockOutTimeRecord = userWorkRecord.getClockOutTime().format(DTF_HHMM);

        return ResponseEntity.ok(Map.of(
                "message", "退勤打刻しました",
                "clockOutTime",clockOutTimeRecord
        ));
    }
    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id, HttpSession session) {
        try {
            //DBから出退勤記録取消
            workRecordService.deleteWorkRecord(id);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("打刻エラーが発生しました");
        }
        // 成功したら「新しい状態」を返してあげる
        return ResponseEntity.ok(Map.of(
                "message", "取り消しました"
        ));
    }
}