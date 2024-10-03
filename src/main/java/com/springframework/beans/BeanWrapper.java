package com.springframework.beans;

/**
 * <p>bean的包装类</p>
 * @author Bosen
 * @date 2021/9/10 14:48
 */
public class BeanWrapper {
    /**
     * <p>回由该对象包装的bean实例</p>
     */
    private Object wrappedInstance;

    public BeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    /**
     * <p>返回包装的bean实例的类型</p>
     */
    private Class<?> wrappedClass;

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    public void setWrappedInstance(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Class<?> getWrappedClass() {
        return wrappedClass;
    }

    public void setWrappedClass(Class<?> wrappedClass) {
        this.wrappedClass = wrappedClass;
    }
}