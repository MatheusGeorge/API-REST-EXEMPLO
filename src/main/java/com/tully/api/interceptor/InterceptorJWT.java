package com.tully.api.interceptor;

import com.auth0.jwt.JWTVerifier;
import com.tully.api.controller.UsuarioRestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by jonathan on 16/02/2017.
 */

public class InterceptorJWT extends HandlerInterceptorAdapter{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            // cai aqui se vc tentar acessar qualquer rota da api
            HandlerMethod method = (HandlerMethod) handler;

            // pergunto se o método que ele ta tentando acessar é o /login
            if (method.getMethod().getName().equals("login"))
                return true;

            String token = request.getHeader("Authorization");

            try {
                JWTVerifier verifier = new JWTVerifier(UsuarioRestController.SECRET);
                Map<String, Object> claims = verifier.verify(token);

                return true;
            } catch (Exception e) {
                if (token == null) {
                    response.sendError(HttpStatus.UNAUTHORIZED.value());
                } else {
                    response.sendError(HttpStatus.FORBIDDEN.value());
                }
            }
            return false;
        } else {
            return true;
        }
    }
}
