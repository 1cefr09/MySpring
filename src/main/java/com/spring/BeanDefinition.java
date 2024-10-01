package com.spring;

public class BeanDefinition {
    private Class clazz;

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    private String scope;
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }


}
