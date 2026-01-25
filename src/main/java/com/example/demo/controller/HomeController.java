package com.example.demo.controller;

import com.example.demo.dto.WorkRecordDto;
import com.example.demo.model.UserEntity;
import com.example.demo.model.WorkRecord;
import com.example.demo.model.UserInfoResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkRecordRepository;
import com.example.demo.service.WorkRecordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
//@RequestMapping("/work")
//いずれControllerを分割
public class HomeController {

    private final WorkRecordRepository workRecordRepository;
    private final UserRepository userRepository;
    @Autowired
    private  final WorkRecordService workRecordService;

    public HomeController(WorkRecordRepository repository, UserRepository userRepository,WorkRecordService workRecordService) {
        this.workRecordRepository = repository;
        this.userRepository = userRepository;
        this.workRecordService = workRecordService;
    }

    //初期設定はログイン画面へ遷移
    @GetMapping("/")
    public String Top(Model model) {
        return "redirect:/login";
    }

    //ログイン画面
    @GetMapping("/login")
    public String login(Model model) {
        return "login";
    }
    //@PostMapping("/login")
    //ログイン処理 >>security\CustomAuthenticationSuccessHandlerで処理しredirect:

    //出退勤登録画面
    @GetMapping("/work_submit")
    public String work_submit(HttpSession session, Model model) {
        // Sessionからユーザー情報を取り出す　（ログイン時保存）
        UserEntity user = (UserEntity) session.getAttribute("user");
        String username = user.getUsername();
        if (username == null) {
            return "redirect:/"; // 未ログインならログインページへ
        }
        UserInfoResponse workSubmitDto = workRecordService.workSubmitInit(username);
        model.addAttribute("username", username);
        model.addAttribute("todayRecordId",workSubmitDto.getTodayRecordId());
        model.addAttribute("workPlace", workSubmitDto.getWorkPlace());
        return "work_submit";
    }
    //勤怠記録一覧画面 管理者判定に応じたリダイレクト
    @GetMapping("/work_records/redirect")
    public String redirectWork_records(Authentication auth){
        String userName = auth.getName();
        //管理者判定に応じたリダイレクト
//        if (workRecordService.judgeRoles(userName)){
//            return "redirect:/work_recordsAdmin";
//        } else {
//            return "redirect:/work_records";
//        }
        return "redirect:/work_records";
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
