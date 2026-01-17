package com.example.demo.controller;

import com.example.demo.dto.WorkRecordDto;
import com.example.demo.model.UserEntity;
import com.example.demo.model.WorkRecord;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkRecordRepository;
import com.example.demo.service.WorkRecordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
//@RequestMapping("/work")
//いずれControllerを分割
public class HomeController {

    private final WorkRecordRepository repository;
    private final UserRepository userRepository;
    @Autowired
    private  final WorkRecordService workRecordService;

    public HomeController(WorkRecordRepository repository, UserRepository userRepository,WorkRecordService workRecordService) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.workRecordService = workRecordService;
    }

    @Value("${googleMapsApiKey}")
    private String googleMapsApiKey;

    //初期設定はログイン画面へ遷移
    @GetMapping("/")
    public String Top(Model model) {
        return "redirect:/login";
    }

    //ログイン画面
    @GetMapping("/login")
    public String login(Model model) {
        return "login"; // templates/login.html を表示する
    }

    //@PostMapping("/login")
    //ログイン処理 >>security\CustomAuthenticationSuccessHandlerで処理しredirect:

    //出退勤登録画面
    @GetMapping("/work_submit")
    public String work_submit(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/"; // 未ログインならログインページへ
        }
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        String workPlace = userEntity.get().getWorkPlace();
        model.addAttribute("username", username);
        //ユーザーの最新のレコードを取得　　　　　　　　
        WorkRecord userWorkRecord = repository.findTopByUsernameOrderByClockInTimeDesc(username);
        //当日のレコードがあるか判定
        boolean isTodayRecorded = false;
        if (userWorkRecord == null) {
            // userWorkRecordがnullならダミーオブジェクトを設定
            userWorkRecord = new WorkRecord();
        } else if (userWorkRecord.getClockInTime() != null) {
            isTodayRecorded = userWorkRecord.getClockInTime().toLocalDate().equals(LocalDate.now());
        }
        //今日のレコードがなければ
        if (!isTodayRecorded) {
            userWorkRecord.setClockOutTime(null);
        }
        System.out.println("getmap/workSubmit#gpsResult:" + session.getAttribute("gpsResult"));
        model.addAttribute("isTodayRecorded", isTodayRecorded);
        model.addAttribute("userWorkRecord", userWorkRecord);
        model.addAttribute("workPlace", workPlace);
        model.addAttribute("gpsResult", session.getAttribute("gpsResult"));
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);
        //justLoginをhtmlに渡してから削除
        model.addAttribute("justLogin", session.getAttribute("justLogin"));
        session.removeAttribute("justLogin");
        model.addAttribute("recordMg",session.getAttribute("recordMg"));
        session.removeAttribute("recordMg");
        return "work_submit";
    }
    //出勤登録　（セッション、現在値の緯度経度）
    @PostMapping("/clockIn")
    public String clockIn(HttpSession session, @RequestParam(defaultValue = "0.0") double lat, @RequestParam(defaultValue = "0.0") double lon) {
        String userName = (String) session.getAttribute("username");
        if (userName == null) {
            return "redirect:/";
        }
        //出勤登録し位置判定結果を取得
        boolean gpsResult = workRecordService.clockInResult(userName,lat,lon);
        session.setAttribute("gpsResult", gpsResult);
        session.setAttribute("recordMg","出勤記録を登録しました。");
        return "redirect:/work_submit";
    }
    //退勤登録（出退勤記録ID、セッション、現在の緯度経度）
    @PostMapping("/clock-out/{id}")
    public String clockOut(@PathVariable Long id, HttpSession session, @RequestParam(defaultValue = "0.0") double lat, @RequestParam(defaultValue = "0.0") double lon) {
        String userName = (String) session.getAttribute("username");
        // 退勤登録 位置判定結果　workRecordService
        boolean gpsResult = workRecordService.clockOutResult(id,userName,lat,lon);
        session.setAttribute("gpsResult", gpsResult);
        session.setAttribute("recordMg","退勤記録を登録しました。");
        return "redirect:/work_submit";
    }
    //出退勤記録取消(対象の出退勤記録のID、セッション)
    @PostMapping("/cancel/{id}")
    public String cancel(@PathVariable Long id, HttpSession session) {
        //DBから出退勤記録取消
        workRecordService.deleteWorkRecord(id);
        session.setAttribute("gpsResult", false);
        session.setAttribute("recordMg","出退勤記録を取消しました。");
        return "redirect:/work_submit";
    }
    //勤怠記録一覧画面 管理者判定に応じたリダイレクト
    @GetMapping("/work_records/redirect")
    public String redirectWork_records(Authentication auth){
        String userName = auth.getName();
        //管理者判定に応じたリダイレクト
        if (workRecordService.judgeRoles(userName)){
            return "redirect:/work_recordsAdmin";
        } else {
            return "redirect:/work_records";
        }
    }
    //勤怠記録一覧画面
    @GetMapping("/work_records")
    public String work_records(Model model, HttpSession session ,Authentication auth) {
        //Authentication  auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        model.addAttribute("username", userName);
        //usernameの全ての勤怠記録を取得
        //List<WorkRecord> userWorkRecords = workRecordService.getUserRecordsByUsernameSort(userName);
        //model.addAttribute("userWorkRecords", userWorkRecords);
        session.setAttribute("showUserName",userName);

        return "work_records";
    }
    @GetMapping("/work_recordsAdmin")
    public String work_recordsAdmin(Model model,HttpSession session,Authentication auth){
        String loginUserName = auth.getName();
        model.addAttribute("username", loginUserName);
        //DBにある全ユーザー名を渡す  ラベル表示
        List<String> allUserName = workRecordService.getAllUserName();
        model.addAttribute("allUserName",allUserName);
        //表示するユーザー
        String showUserName =(String) session.getAttribute("showUserName");
        session.setAttribute("showUserName",showUserName);
        model.addAttribute("showUserName",showUserName);
        //test用
        List<WorkRecordDto> workRecordsList = workRecordService.getUserRecordsDto(loginUserName);
        model.addAttribute("workRecordsList",workRecordsList);
        return "work_recordsAdmin";
    }
    @PostMapping("/work_recordsAdmin/cancel/{id}")
    public String work_recordsAdmin_cancel(@PathVariable Long id ){
        workRecordService.deleteWorkRecord(id);
        return "redirect:/work_recordsAdmin";
    }
    @PostMapping("/work_recordsAdmin")
    public String work_recordsAdmin(HttpSession session,@RequestParam("userName") String showUserName){
        session.setAttribute("showUserName",showUserName);
        return "redirect:/work_recordsAdmin";
    }
    @GetMapping("/work_recordsAdmin/work_recordAdd")
    public String work_recordAdd (Model model,HttpSession session){
        //DBにある全ユーザー名を渡す  ラベル表示
        List<String> allUserName = workRecordService.getAllUserName();
        model.addAttribute("allUserName",allUserName);
        //表示するユーザー
        String showUserName =(String) session.getAttribute("showUserName");
        session.setAttribute("showUserName",showUserName);
        model.addAttribute("showUserName",showUserName);
        return "work_recordAdd";
    }
    @PostMapping("/work_recordsAdmin/work_recordAdd")
    public String work_recordAdd(HttpSession session,@RequestParam("date") LocalDate date ,@RequestParam("checkInTime") LocalTime checkInTime,@RequestParam("checkOutTime") LocalTime checkOutTime ){
        //日付と時間を結合＞＞LocalDateTime
        LocalDateTime checkInDateTime = LocalDateTime.of(date,checkInTime);
        LocalDateTime checkOutDateTime = LocalDateTime.of(date,checkOutTime);
        String user = (String) session.getAttribute("showUserName");
        //DBへ追加
        WorkRecord record = new WorkRecord();
        record.setUsername(user);
        record.setClockInTime(checkInDateTime);
        record.setClockOutTime(checkOutDateTime);
        workRecordService.saveWorkRecord(record);
        return "redirect:/work_recordsAdmin";

    }


}
