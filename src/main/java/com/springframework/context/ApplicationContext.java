package com.springframework.context;

import com.springframework.beans.factory.BeanFactory;

/**
 * <p>容器顶层接口</p>
 */
public interface ApplicationContext extends BeanFactory {
    void refresh() throws Exception;
}
