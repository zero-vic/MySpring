package com.xy.myspring.bean.utils;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

/**
 * Created With IntelliJ IDEA
 * User: yao
 * Date:2020/12/03
 * Description:
 * Version:V1.0
 */
public class XmlParse {
    
    public static String  getBasePackage(String contextConfigLocation){
        //使用dom4j解析配置文件 返回basePackage
        //创建SAXReader对象
        SAXReader saxReader = new SAXReader();
        //获取xml 输入流
        InputStream in = XmlParse.class.getClassLoader().getResourceAsStream(contextConfigLocation);
        //获取XML文档对象
        Document document = null;
        try {
            document = saxReader.read(in);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        //获取root节点
        Element rootElement = document.getRootElement();
        //获取元素节点
        Element element = rootElement.element("component-scan");
        //获取base-package的值
        Attribute attribute = element.attribute("base-package");
        String basePackage = attribute.getText();
        //basePackage com.xy.test
        return basePackage;
    }

//    public static void main(String[] args) {
//        String basePackage = getBasePackage("application.xml");
//        System.out.println(basePackage);
//        }
}
