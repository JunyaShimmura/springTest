package com.example.demo.api;

import com.example.demo.model.UserEntity;
import com.example.demo.model.WorkRecordDto;
import com.example.demo.repository.WorkRecordRepository;
import com.example.demo.service.WorkRecordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

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

        WorkRecordDto res = new WorkRecordDto();
        // セッションから丸ごと保存したユーザー情報を取り出す
        UserEntity userEntity = (UserEntity) session.getAttribute("user");
        try {
            res = workRecordService.handleGetWorkRecord(userEntity, id);
        } catch (Exception e){
            if ("SESSION_TIMEOUT".equals(e.getMessage())) {
                return ResponseEntity.status(401).body("セッションが切れました。再ログインしてください。");
            }
            if ("ERROR_FETCH_RECORD".equals(e.getMessage())){
                return ResponseEntity.status(500).body("レコードを取得エラーが発生しました");
            }
            return ResponseEntity.status(500).body("レコードを取得エラーが発生しました");
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping("/punch-in")
    public ResponseEntity<?> punchIn(HttpSession session) {

        UserEntity userEntity = (UserEntity) session.getAttribute("user");
        WorkRecordDto res;
        try {
            res = workRecordService.handleClockIn(userEntity);
        } catch (Exception e) {
            // メッセージの内容によってステータスを出し分ける
            if ("SESSION_TIMEOUT".equals(e.getMessage())) {
                return ResponseEntity.status(401).body("セッションが切れました。再ログインしてください。");
            }
            return ResponseEntity.status(500).body(e.getMessage());
        }
        return ResponseEntity.ok(res);
    }
    @PostMapping("/punch-out/{id}")
    public ResponseEntity<?> punchOut(@PathVariable Long id,HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        WorkRecordDto res;
        try {
           res = workRecordService.handleClockOut(id,user.getUsername(),1.0,1.0);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }

        return ResponseEntity.ok(res);
    }
    @PostMapping("/cancel/{id}")
    public ResponseEntity<?> cancel(@PathVariable Long id, HttpSession session) {
        WorkRecordDto res;
        try {
            //DBから出退勤記録取消
            res = workRecordService.deleteWorkRecord(id);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("打刻エラーが発生しました");
        }
        return ResponseEntity.ok(res);
    }
    @GetMapping("/work-records")
    public List<WorkRecordDto> getAllWorkRecords(HttpSession session) {
        String userName = (String) session.getAttribute("showUserName");
        return workRecordService.getUserRecordsDto(userName);
    }
}