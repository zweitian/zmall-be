package com.zmall.controller.common.interceptor;

import com.google.common.collect.Maps;
import com.zmall.common.Const;
import com.zmall.common.ServerResponse;
import com.zmall.pojo.User;
import com.zmall.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by geely
 */
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    /**
     * 放行的UriMap
     * key为请求uri
     * value为请求方式(GET、POST、PUT、DELETE)
     */
    private Map<String, String> releaseUriMap;

    /**
     * 执行/backend下方法时的拦截器
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle");
        String requestURI = request.getRequestURI();
        String requestMethod = request.getMethod();
        // 是否放行以Method请求的该URI
        if (MapUtils.isNotEmpty(releaseUriMap)) {
            boolean releaseUri = releaseUriMap.containsKey(requestURI) && releaseUriMap.get(requestURI).equalsIgnoreCase(requestMethod);
            if (releaseUri) {
                return true;
            }
        }
        //请求中Controller中的方法名
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        //解析HandlerMethod
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();
        //解析参数,具体的参数key以及value是什么，我们打印日志
        StringBuffer requestParamBuffer = new StringBuffer();
        Map paramMap = request.getParameterMap();
        Iterator it = paramMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String mapKey = (String) entry.getKey();
            String mapValue = StringUtils.EMPTY;
            //request这个参数的map，里面的value返回的是一个String[]
            Object obj = entry.getValue();
            if (obj instanceof String[]) {
                String[] strs = (String[]) obj;
                mapValue = Arrays.toString(strs);
            }
            requestParamBuffer.append(mapKey).append("=").append(mapValue);
        }
        log.info("权限拦截器拦截到请求,className:{},methodName:{},param:{}", className, methodName, requestParamBuffer.toString());

        Object user = request.getSession().getAttribute(Const.CURRENT_USER);
        if (user == null) {
            response.reset();//geelynote 这里要添加reset，否则报异常 getWriter() has already been called for this response.
            response.setCharacterEncoding("UTF-8");//这里要设置编码，否则会乱码
            response.setContentType("application/json;charset=UTF-8");//这里要设置返回值的类型，因为全部是json接口。
            PrintWriter out = response.getWriter();
            out.print(JsonUtil.objToString(ServerResponse.createByErrorMessage("拦截器拦截,用户未登录")));
            out.flush();
            out.close();
            return false;
        }
        User currUser = (User) user;
        if (currUser.getRole().intValue() != Const.Role.ROLE_ADMIN) {
            response.reset();//geelynote 这里要添加reset，否则报异常 getWriter() has already been called for this response.
            response.setCharacterEncoding("UTF-8");//这里要设置编码，否则会乱码
            response.setContentType("application/json;charset=UTF-8");//这里要设置返回值的类型，因为全部是json接口。
            PrintWriter out = response.getWriter();
            out.print(JsonUtil.objToString(ServerResponse.createByErrorMessage("非管理员用户，无法登录")));
            out.flush();
            out.close();
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("afterCompletion");
    }

    public void setReleaseUriMap(Map<String, String> releaseUriMap) {
        this.releaseUriMap = releaseUriMap;
    }
}
