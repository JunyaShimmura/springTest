package com.example.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component  // Spring コンテナで管理できるようにするためのアノテーション
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    @Override
    public void onAuthenticationSuccess(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Authentication authentication) throws IOException, jakarta.servlet.ServletException {
        // 認証成功後にセッションに情報を保存
        request.getSession().setAttribute("username", authentication.getName());  // ログインユーザーの名前をセッションに格納

        // ログイン後に遷移する先を指定
        response.sendRedirect("/work_submit");  // ここでは "/home" にリダイレクト

    }
}
