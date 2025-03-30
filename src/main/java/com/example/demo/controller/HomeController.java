package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.WorkRecord;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WorkRecordRepository;
import com.example.demo.service.WorkRecordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
//@RequestMapping("/work")
//いずれControllerを分割
public class HomeController {

    private final WorkRecordRepository repository;
    private final UserRepository userRepository;
    @Autowired
    private WorkRecordService workRecordService;

    public HomeController(WorkRecordRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
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
        User user = userRepository.findByUsername(username);
        String workPlace = user.getWorkPlace();
        if (username == null) {
            return "redirect:/"; // 未ログインならログインページへ
        }
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
        System.out.println("WorkSubmit時 session ID: " + session.getId());  // 🔹 セッションID確認
        System.out.println("getmap/workSubmit#gpsResult:" + session.getAttribute("gpsResult"));
        System.out.println("getmap/workSubmit#justLogin:" + session.getAttribute("justLogin"));
        model.addAttribute("isTodayRecorded", isTodayRecorded);
        model.addAttribute("userWorkRecord", userWorkRecord);
        model.addAttribute("workPlace", workPlace);
        model.addAttribute("gpsResult", session.getAttribute("gpsResult"));
        model.addAttribute("googleMapsApiKey", googleMapsApiKey);

        model.addAttribute("justLogin", session.getAttribute("justLogin"));
        //justLoginをhtmlに渡してから削除
        session.removeAttribute("justLogin");
        return "work_submit";
    }

    //出勤登録　（セッション、現在値の緯度経度）
    @PostMapping("/clockIn")
    public String clockIn(HttpSession session, @RequestParam(defaultValue = "0.0") double lat, @RequestParam(defaultValue = "0.0") double lon) {
        String userName = (String) session.getAttribute("username");
        if (userName == null) {
            return "redirect:/";
        }
        System.out.println("/clockin 取得lat： :" + lat);
        //位置判定処理（引数の緯度経度とユーザー名）
        boolean gpsResult = checkGps(lat, lon, userName);
        System.out.println("/clockin位置判定： :" + gpsResult);
        session.setAttribute("gpsResult", gpsResult);
        //DBへ出勤記録を登録
        WorkRecord record = new WorkRecord();
        record.setUsername(userName);
        record.setClockInTime(LocalDateTime.now());
        record.setClockInJudge(gpsResult);
        repository.save(record);
        return "redirect:/work_submit";
    }

    //出退勤記録取消(対象の出退勤記録のID)
    @PostMapping("/cancel/{id}")
    public String cancel(@PathVariable Long id, HttpSession session) {
        WorkRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("レコードがみつかりません id:" + id));
        //DBから出退勤記録取消
        repository.delete(record);
        session.setAttribute("gpsResult", false);
        return "redirect:/work_submit";
    }

    //退勤登録（出退勤記録ID、セッション、現在の緯度経度）
    @PostMapping("/clock-out/{id}")
    public String clockOut(@PathVariable Long id, HttpSession session, @RequestParam(defaultValue = "0.0") double lat, @RequestParam(defaultValue = "0.0") double lon) {
        String userName = (String) session.getAttribute("username");
        User user = userRepository.findByUsername(userName);
        WorkRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("レコードがみつかりません id:" + id));
        if (record != null) {
            //位置判定処理（緯度経度、ユーザー名）
            boolean gpsResult = checkGps(lat, lon, userName);
            record.setClockOutJudge(gpsResult);
            //退勤記録をDBへ登録
            session.setAttribute("gpsResult", gpsResult);
            record.setClockOutTime((LocalDateTime.now()));
            repository.save(record);
        }
        return "redirect:/work_submit";
    }

    //勤怠記録一覧画面
    @GetMapping("/work_records")
    public String work_records(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        List<WorkRecord> userWorkRecords = workRecordService.getUserRecordsByUsernameSort(username);
        model.addAttribute("username", username);
        model.addAttribute("userWorkRecords", userWorkRecords);
        return "work_records";
    }

    //位置判定処理　（現在地の緯度、経度、ユーザー名）
    public boolean checkGps(Double nowLat, Double nowLon, String userName) {
        final int R = 6371000; // 地球の半径 (メートル)
        double allowedDistance = 1000.0; // 許可範囲 (メートル)
        // ユーザー名からテーブルにあるユーザー情報を取得
        User user = userRepository.findByUsername(userName);
        double companyLat = user.getCompanyLat();
        double companyLon = user.getCompanyLon();
        System.out.println(userName + "のcompanyLat :" + companyLat);
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
        System.out.println("distance :" + distance);
        //現在地と勤務地の距離と許可範囲の比較結果を返す
        return distance <= allowedDistance;
    }

}
