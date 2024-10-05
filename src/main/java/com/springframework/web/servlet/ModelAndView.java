package com.springframework.web.servlet;

import java.util.Map;

/**
 * <p>存储视图名称和数据</p>
 */
public class ModelAndView {
    /**
     * <p>视图名称</p>
     */
    private String viewName;

    /**
     * <p>参数</p>
     */
    private Map<String, ?> model;

    public ModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public ModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}