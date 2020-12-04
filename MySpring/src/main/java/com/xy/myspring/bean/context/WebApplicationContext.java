package com.xy.myspring.bean.context;

import com.xy.myspring.bean.annotation.*;
import com.xy.myspring.bean.utils.AnnotationList;
import com.xy.myspring.bean.utils.XmlParse;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created With IntelliJ IDEA
 * User: yao
 * Date:2020/12/03
 * Description:ioc实现类
 * Version:V1.0
 */
public class WebApplicationContext {
    private String contextConfigLocation;

    //存储类的全路径   保证线程安全
    private Set<String> classNameSet = new CopyOnWriteArraySet<>();
    //ioc容器 存储类的实例化对象  key:class   value:object
    private Map<Class<?>, Object> iocMap = new ConcurrentHashMap<>();
    //key：interface  value object
    private Map<Class<?>, List<Object>> superIocMap = new ConcurrentHashMap<>();
    //根据指定的bean名字来存储 key：name  value：object
    private Map<String, Object> nameIocMap = new ConcurrentHashMap<>();

    public WebApplicationContext(String contextConfigLocation) {
        //解析配置文件  basePackage = com.xy.test
        String basePackage = XmlParse.getBasePackage(contextConfigLocation.split(":")[1]);
        //扫描包
        executeScanPackage(basePackage);
        //初始化ioc容器,把需要注入的对象存到ioc中
        executeInstance();
        //依赖注入
        executeAutoWired();
        System.out.println(iocMap);
        System.out.println(superIocMap);
        System.out.println(nameIocMap);

    }

    /**
     * 依赖注入
     */
    private void executeAutoWired() {
        //获取iocmap中所有的key集合 class对象
        Set<Class<?>> classSet = iocMap.keySet();
        if(classSet==null) return;
        for (Class<?> clazz : classSet) {
            //获取类的所有属性
            Field[] declaredFields = clazz.getDeclaredFields();
            if(declaredFields==null) continue;
            for (Field declaredField : declaredFields) {
                //判断属性上是否有AutoWired注解
                if(declaredField.isAnnotationPresent(AutoWried.class)){
                    //根据类型注入
                    Object bean = this.getBean(declaredField.getType());
                    if(bean ==null){
                        //根据名字来注入
                        bean = this.getBeanByName(declaredField.getName());
                        if(bean==null){
                            //根据接口来注入
                            bean = this.getBeanByInterface(declaredField.getType());
                            if(bean==null){
                                throw new RuntimeException("没有能注入的对象");
                            }
                        }
                    }
                    //开启权限
                    declaredField.setAccessible(true);
                    try {
                        declaredField.set(iocMap.get(clazz),bean);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 根据类型从iocMap中取出对象
     * @param clazz
     * @return
     */
    private Object getBean(Class<?> clazz){
        return iocMap.get(clazz);
    }

    /**
     * 按照接口类型取对象
     * @param clazz
     * @return
     */
    private Object getBeanByInterface(Class<?> clazz){
        List<Object> objects = superIocMap.get(clazz);
        if(objects==null) return null;
        if(objects.size()>1){
            throw new RuntimeException(clazz.getName()+"想找一个，可是有多个");
        }
        return objects.get(0);
    }

    /**
     * 根据名字来取对象
     * @param beanName
     * @return
     */
    public Object getBeanByName(String beanName){
        return nameIocMap.get(beanName);
    }

    /**
     * 把对像存到ioc容器中
     */
    private void executeInstance() {
        if (classNameSet == null) {
            throw new RuntimeException("没有要实例化的对象");
        }
        List<Class<? extends Annotation>> annotationList = AnnotationList.getAnnotationList();
        try {
            for (String className : classNameSet) {
                Class<?> clazz = Class.forName(className);
                for (Class<? extends Annotation> annotation : annotationList) {
                    //如果当前类上有controller等注解
                    if (clazz.isAnnotationPresent(annotation)) {
                        //实例化对象
                        Object instance = clazz.newInstance();
                        //存入ioc
                        iocMap.put(clazz, instance);
                        //获取当前类实现的所有接口
                        Class<?>[] interfaces = clazz.getInterfaces();
                        if (interfaces != null) {
                            //一个接口有多个实现类
                            for (Class<?> anInterface : interfaces) {
                                List<Object> objects = superIocMap.get(anInterface);
                                if (objects.isEmpty()) {
                                    //说明当前接口的对象还没有放过
                                    List<Object> list = new ArrayList<>();
                                    list.add(instance);
                                    superIocMap.put(anInterface, list);
                                } else {
                                    //说明当前对象放过
                                    objects.add(instance);
                                }
                            }
                        }
                        //放入nameIocMap
                        putNameIocMap(clazz,annotation,instance);

                        break;

                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void putNameIocMap(Class<?> clazz,Class<? extends Annotation> annotation, Object instance) {
        if (annotation == Service.class) {
            Service serviceAnnotation = clazz.getAnnotation(Service.class);
            String value = serviceAnnotation.value();
            if (value.equals("")) {
                String simpleName = clazz.getSimpleName();
                String name = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                value = name;
            }
            if(nameIocMap.containsKey(value)){
                throw new RuntimeException("nameIocMap 有相同的name了");
            }
            nameIocMap.put(value,instance);
        }
        if (annotation == Component.class) {
            Component componentAnnotation = clazz.getAnnotation(Component.class);
            String value = componentAnnotation.value();
            if (value.equals("")) {
                String simpleName = clazz.getSimpleName();
                String name = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                value = name;
            }
            if(nameIocMap.containsKey(value)){
                throw new RuntimeException("nameIocMap 有相同的name了");
            }
            nameIocMap.put(value,instance);
        }

        if (annotation == Controller.class) {
            Controller controllerAnnotation = clazz.getAnnotation(Controller.class);
            String value = controllerAnnotation.value();
            if (value.equals("")) {
                String simpleName = clazz.getSimpleName();
                String name = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                value = name;
            }
            if(nameIocMap.containsKey(value)){
                throw new RuntimeException("nameIocMap 有相同的name了");
            }
            nameIocMap.put(value,instance);
        }
        if (annotation == Repository.class) {
            Repository repositoryAnnotation = clazz.getAnnotation(Repository.class);
            String value = repositoryAnnotation.value();
            if (value.equals("")) {
                String simpleName = clazz.getSimpleName();
                String name = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                value = name;
            }
            if(nameIocMap.containsKey(value)){
                throw new RuntimeException("nameIocMap 有相同的name了");
            }
            nameIocMap.put(value,instance);
        }
    }


    /**
     * 扫面 basePackage 下的类 并存入 packageSet
     *
     * @param basePackage
     */
    private void executeScanPackage(String basePackage) {
        URL url = this.getClass().getClassLoader().getResource(basePackage.replace(".", "/"));
        String path = url.getFile();
        File file = new File(path);
        //递归 遍历 存classname
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                executeScanPackage(basePackage + "." + f.getName());
            } else {
                String className = basePackage + "." + f.getName().replaceAll(".class", "");
                classNameSet.add(className);
            }
        }
    }


}
