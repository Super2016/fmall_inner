package cn.fmall.utils;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * 时间格式转换
 */
public class DateTimeUtil {
    //使用joda-time开源包将默认日期格式转为特定日期格式

    //标准时间格式
    public static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";

    //默认日期格式转换成自定义日期格式
    public static Date stringToDate(String defaultDateTime,String customFormat){
        //格式转换器,[选择转换模式]
        DateTimeFormatter formatter = DateTimeFormat.forPattern(customFormat);
        //开始转换格式
        DateTime customDateTime = formatter.parseDateTime(defaultDateTime);
        //返回转换后的日期自字符串
        return customDateTime.toDate();
    }

    //自定义日期转成默认日期格式
    public static String dateToString(Date customDateTime,String currentformat){
        if (customDateTime == null) {
            return StringUtils.EMPTY;
        }
        //将自定义时间格式字符串接受并保存进对象
        DateTime defaultDate = new DateTime(customDateTime);
        //分析自定义时间格式,转换为默认格式后返回
        return defaultDate.toString(currentformat);
    }

    //默认日期格式转换成标准日期格式
    public static Date stringToDate(String defaultDateTime){
        //格式转换器,[选择转换模式]
        DateTimeFormatter formatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        //开始转换格式
        DateTime standardDateTime = formatter.parseDateTime(defaultDateTime);
        //返回转换后的日期自字符串
        return standardDateTime.toDate();
    }

    //标准日期转成默认日期格式
    public static String dateToString(Date standardDateTime){
        if (standardDateTime == null) {
            return StringUtils.EMPTY;
        }
        //将自定义时间格式字符串接受并保存进对象
        DateTime defaultDateTime = new DateTime(standardDateTime);
        //分析标准时间格式,转换为默认格式后并返回
        return defaultDateTime.toString(STANDARD_FORMAT);
    }
}
