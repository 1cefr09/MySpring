package com.springframework.web.servlet;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * <p>用于保存URL和Method的对应关系</p>
 */
public class HandlerMapping {
    /**
     * <p>对应的controller对象</p>
     */
    private Object controller;

    /**
     * <p>对应的方法</p>
     */
    private Method method;

    /**
     * <p>URL的封装</p>
     */
    private Pattern pattern;

    public HandlerMapping(Object controller, Method method, Pattern pattern) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}