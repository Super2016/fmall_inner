package cn.fmall.utils;

import org.springframework.util.StringUtils;
import java.security.MessageDigest;

/**
 * MD5加密算法工具类
 */
public class Md5Utils {

    public static String Md5EncodeUtf8(String origin) {
        origin = origin + PropertiesUtil.getValueByKey("password.salt", "");
        return Md5Encode(origin, "utf-8");
    }

    /**
     * 返回大写MD5码,传入加密前的原字符串、加密使用的字符集
     * @param origin
     * @param charsetname
     * @return
     */
    private static String Md5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            //charsetname为空使用默认字符集,否则使用指定字符集
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString.getBytes(charsetname)));
        } catch (Exception exception) {

        }
        return resultString.toUpperCase();
    }

    //内部加密算法,byte数组转换为16进制字符串
    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    //内部加密算法,byte转换为16进制字符串
    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    //16位进制数组
    private static final String hexDigits[] = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
}
