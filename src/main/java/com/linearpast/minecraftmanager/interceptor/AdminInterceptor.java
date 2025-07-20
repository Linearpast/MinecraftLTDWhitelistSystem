package com.linearpast.minecraftmanager.interceptor;

import com.linearpast.minecraftmanager.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession();
        if(
                session == null
                || session.getAttribute("isLoggedIn") == null
                || !((boolean) session.getAttribute("isLoggedIn"))
                || session.getAttribute("adminAccount") == null){
            throw new UnauthorizedException("redirect:/admin/login?error=please login first");
        }
        return true;
    }
}
