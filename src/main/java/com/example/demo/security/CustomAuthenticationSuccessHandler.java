package com.example.demo.security;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component  // Spring コンテナで管理できるようにするためのアノテーション
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    // 認証成功後の処理
    @Override
    public void onAuthenticationSuccess(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Authentication authentication) throws IOException, jakarta.servlet.ServletException {
        // 既存のセッションを無効化し、新しいセッションを発行 （セッションの初期化）
        request.getSession().invalidate();
        // 新しいセッションを作成（Spring Bootが自動で生成する）
        HttpSession session = request.getSession(true);
        session.setAttribute("username", authentication.getName());  // ログインユーザーの名前を保存
        session.setAttribute("gpsResult", false); //gpsResult初期化
        session.setAttribute("justLogin",true);//ログイン認証後flg
        // ログイン後に遷移する先
        response.sendRedirect("/work_submit");
    }
}
