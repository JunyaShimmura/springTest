package com.example.demo.controller;

import com.example.demo.model.WorkRecord;
import com.example.demo.repository.WorkRecordRepository;
import com.example.demo.service.WorkRecordService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
//@RestController
//@RequestMapping("/work")
//いずれControllerを分割
public class HomeController {

    private final WorkRecordRepository repository;
    @Autowired
    private WorkRecordService workRecordService;
    public HomeController(WorkRecordRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public String Top(Model model) {

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model) {
        // message というデータを HTML 側に送る
        model.addAttribute("message", "ようこそ！ログイン機能");
        return "login"; // templates/login.html を表示する
    }
    //sesseion にユーザー名を保存
    @PostMapping("/login")
    public String login(@RequestParam("username") String username, HttpSession session) {
        session.setAttribute("username", username);
        session.setAttribute("gpsResult", null);
        return "redirect:/work_submit";
    }
    @GetMapping("/work_submit")
    public String work_submit(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            username = "tmpName";
            //return "redirect:/"; // 未ログインならログインページへ
        }
        model.addAttribute("username", username);
        //   WorkRecord userWorkRecord = repository.findTopByUsernameAndClockOutTimeIsNullOrderByClockInTimeDesc(username);
        //ユーザーの最新のレコードを取得　　　　　　　　 findTopByUsername  findTopByUsernameOrderById
        WorkRecord userWorkRecord = repository.findTopByUsernameOrderByClockInTimeDesc(username);
        //今日のレコードがあるか判定
        boolean isTodayRecorded = false;
        if (userWorkRecord == null) {
            // userWorkRecordがnullならダミーオブジェクトを設定
            userWorkRecord = new WorkRecord();
        } else if (userWorkRecord.getClockInTime() != null) {
            isTodayRecorded = userWorkRecord.getClockInTime().toLocalDate().equals(LocalDate.now());
        }
        //今日のレコードがなければ
        if (! isTodayRecorded) {
            userWorkRecord.setClockOutTime(null);
        }
        model.addAttribute("isTodayRecorded", isTodayRecorded);
        model.addAttribute("userWorkRecord", userWorkRecord);
        model.addAttribute("gpsResult", session.getAttribute("gpsResult"));
        return "work_submit";
    }
    @PostMapping("/clockIn")
    public String clockIn(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/";
        }
        WorkRecord record = new WorkRecord();
        record.setUsername(username);
        record.setClockInTime(LocalDateTime.now());
        repository.save(record);
        return "redirect:/work_submit";
    }
    @PostMapping("/cancel/{id}")
    public String cancel(@PathVariable Long id) {
        WorkRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("レコードがみつかりません id:" + id));
        repository.delete(record);
        return "redirect:/work_submit";
    }

    @PostMapping("/clock-out/{id}")
    public String clockOut(@PathVariable Long id) {
        WorkRecord record = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("レコードがみつかりません id:" + id));
        if (record != null) {
            record.setClockOutTime((LocalDateTime.now()));
            repository.save(record);
        }
        return "redirect:/work_submit";
    }

    @GetMapping("/work_records")
    public String work_records(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");
        List<WorkRecord> userWorkRecords = workRecordService.getUserRecordsByUsernameSort(username);
        model.addAttribute("username", username);
        model.addAttribute("userWorkRecords", userWorkRecords);
        return "work_records";
    }
    public boolean checkGps(double nowLat, double nowLon) {
        final int R = 6371000; // 地球の半径 (メートル)
        double allowedDistance = 500.0; // 許可範囲 (メートル)
        // 会社の位置（例：東京駅） 35.681236 / 139.767125    富士見市 緯度: 35.857869 経度: 139.549208
        double companyLat = 35.857869;
        double companyLon = 139.549208;

        double dLat = Math.toRadians(companyLat - nowLat);
        double dLon = Math.toRadians(companyLon - nowLon);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(nowLat)) * Math.cos(Math.toRadians(nowLon)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;
        return distance <= allowedDistance;
    }

}
