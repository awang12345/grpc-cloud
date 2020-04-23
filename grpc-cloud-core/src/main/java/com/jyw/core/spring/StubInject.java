package com.jyw.core.spring;

import com.jyw.core.GrpcConfig;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractStub;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * bean属性有{@link GrpcStub}注解时进行注入处理
 */
public class StubInject implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class objClass = AopUtils.getTargetClass(bean);
        if (objClass == null) {
            objClass = bean.getClass();
        }
        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            if (isStubFiled(field)) {
                ReflectionUtils.makeAccessible(field);
                ReflectionUtils.setField(field, bean, buildStub(field));
            }
        }
        return bean;
    }


    private Object buildStub(Field field) {
        //获取com.jyw.stub.GreeterGrpc.GreeterBlockingStub中的com.jyw.stub.GreeterGrpc
        Class clazz = field.getType().getEnclosingClass();
        String newStubMethodName = getNewStubMethodName(field);
        Method newStubMethod = ReflectionUtils.findMethod(clazz, newStubMethodName, Channel.class);
        Assert.notNull(newStubMethod, "Not find method:" + newStubMethodName + " of class:" + clazz.getName());
        return ReflectionUtils.invokeMethod(newStubMethod, null, getChannel(clazz));
    }


    private String getNewStubMethodName(Field field) {
        String stubName = field.getType().getSimpleName();
        if (stubName.endsWith("BlockingStub")) {
            return "newBlockingStub";
        } else if (stubName.endsWith("FutureStub")) {
            return "newFutureStub";
        } else {
            return "newStub";
        }
    }


    private Channel getChannel(Class clazz) {
        String serviceName = getServiceName(clazz);
        String target = StringUtils
                .join(GrpcConfig.getInstance().getRegistryURL(), "?serviceName=", serviceName, "&env=", GrpcConfig.getInstance().getEnv());
        return ManagedChannelBuilder.forTarget(target).usePlaintext(true).build();
    }


    private String getServiceName(Class clazz) {
        Field serviceName = ReflectionUtils.findField(clazz, "SERVICE_NAME");
        Assert.notNull(serviceName, "Not fund field:SERVICE_NAME of class:" + clazz.getName());
        serviceName.setAccessible(true);
        return (String) ReflectionUtils.getField(serviceName, null);
    }


    private boolean isStubFiled(Field field) {
        return field.getDeclaredAnnotation(GrpcStub.class) != null && AbstractStub.class.isAssignableFrom(field.getType());
    }

}
