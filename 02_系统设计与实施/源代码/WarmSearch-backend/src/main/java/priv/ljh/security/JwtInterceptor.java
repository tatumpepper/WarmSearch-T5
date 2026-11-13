package priv.ljh.security;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import priv.ljh.common.Result;
import priv.ljh.common.ResultCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * JWT 拦截器：解析令牌并依据注解做登录/角色校验
 */
@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Value("${warmsearch.jwt.header}")
    private String header;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 若携带 token，尝试解析并放入上下文
        String token = request.getHeader(header);
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token != null && !token.isEmpty()) {
            try {
                LoginUser user = parseToken(token);
                if (user != null) {
                    UserContext.set(user);
                }
            } catch (Exception e) {
                log.debug("令牌解析失败：{}", e.getMessage());
            }
        }

        // 2. 注解鉴权（仅对 Controller 方法生效）
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Class<?> beanType = handlerMethod.getBeanType();

            RequireLogin requireLogin = handlerMethod.getMethodAnnotation(RequireLogin.class);
            if (requireLogin == null) {
                requireLogin = AnnotationUtils.findAnnotation(beanType, RequireLogin.class);
            }
            RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
            if (requireRole == null) {
                requireRole = AnnotationUtils.findAnnotation(beanType, RequireRole.class);
            }

            boolean needLogin = requireLogin != null || requireRole != null;
            LoginUser current = UserContext.get();

            if (needLogin && current == null) {
                writeJson(response, Result.error(ResultCode.UNAUTHORIZED));
                return false;
            }
            if (requireRole != null) {
                boolean allowed = current != null && Arrays.asList(requireRole.value()).contains(current.getRole());
                if (!allowed) {
                    writeJson(response, Result.error(ResultCode.FORBIDDEN));
                    return false;
                }
            }
        }
        return true;
    }

    @javax.annotation.Resource
    private JwtUtils jwtUtils;

    private LoginUser parseToken(String token) {
        return jwtUtils.parse(token);
    }

    private void writeJson(HttpServletResponse response, Result<?> result) throws Exception {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSONUtil.toJsonStr(result));
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
