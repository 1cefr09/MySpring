package com.springframework.web.servlet;

import java.io.File;

/**
 * <p>模板名称和模板解析引擎的匹配</p>
 */
public class ViewResolver {
    /**
     * <p>默认解析的文件后缀</p>
     */
    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    /**
     * <p>模板文件的目录</p>
     */
    private File templateRootDir;

    /**
     * <p>视图名称</p>
     */
    private String viewName;

    public ViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getResource(templateRoot).getFile();
        this.templateRootDir = new File(templateRootPath);
    }

    /**
     * <p>解析视图名</p>
     */
    public View resolveViewName(String viewName) {
        this.viewName = viewName;
        if (viewName == null || "".equals(viewName.trim())) {
            return null;
        }
        // 如果viewName没有加上后缀则加上
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : viewName + DEFAULT_TEMPLATE_SUFFIX;
        // 获取对应的模板文件
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+", "/"));
        // 通过模板文件返回视图对象
        return new View(templateFile);
    }
}