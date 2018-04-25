package cn.fmall.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 读取配置文件
 */
public class PropertiesUtil {
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
    private static Properties properties;

    //需在tomcat启动时即可读取配置文件,使用静态块
    static {
        String propertiesFile = "fmall.properties";
        properties = new Properties();
        try {
            properties.load(
                    new InputStreamReader(
                            Properties.class.getClassLoader().getResourceAsStream(propertiesFile),"UTF-8"
                    )
            );
        } catch (IOException e) {
            logger.error("配置文件读取异常"+e);
        }
    }

    //Method1 通过配置文件中的key取值
    //如value为空则返回null
    public static String getValueByKey(String key){
        String value = properties.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return value.trim();
    }

    //Method2 通过配置文件中的key取值
    //需设置默认值,value为空则取默认值
    public static String getValueByKey(String key,String defaultValue){
        String value = properties.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            value = defaultValue;
        }
        return value.trim();
    }
}
