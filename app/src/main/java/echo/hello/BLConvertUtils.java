package echo.hello;

import android.content.Context;

/**
 * <pre>
 * 转换工具类
 * hexStr2Bytes  十六进制字符串转为 btye 数组
 * bytes2HexStr 将byte数组转为十六进制字符串
 * dip2px  px2dip sp2px
 * @author YeJing
 * @data 2018/6/21
 * </pre>
 */

public final class BLConvertUtils {
    private BLConvertUtils(){}

    /**
     * 十六进制字符串转为 btye 数组
     *
     * @param dataString 转化的16进制字符串
     * @return byte数组
     */
    public static byte[] hexStr2Bytes(String dataString) {
        int subPosition = 0;
        int byteLenght = dataString.length() / 2;

        byte[] result = new byte[byteLenght];

        try {
            for (int i = 0; i < byteLenght; i++) {
                String s = dataString.substring(subPosition, subPosition + 2);
                result[i] = (byte) Integer.parseInt(s, 16);
                subPosition = subPosition + 2;
            }
        } catch (Exception e) {
            return null;
        }

        return result;
    }

    /**
     * 将byte数组转为十六进制字符串
     *
     * @param byteArray
     *            byte[]
     * @return String 十六进制字符串
     */
    public static String bytes2HexStr(byte[] byteArray) {
        StringBuffer re = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            re.append(to16(byteArray[i]));
        }

        return re.toString();
    }

    public static String to16(int b) {
        String hexString = Integer.toHexString(b);
        int lenth = hexString.length();
        if (lenth == 1) {
            hexString = "0" + hexString;
        }
        if (lenth > 2) {
            hexString = hexString.substring(lenth - 2, lenth);
        }
        return hexString;
    }

    public static String tenTo16_2(long i) {
        String re = "";
        re = Long.toHexString(i);
        if (re.length() % 2 != 0) {
            re = "0" + re;
        }
        re = hexbackrow(re);
        return re;
    }

    /**
     * 十进制转 十六进制
     *
     * @param i
     *
     * @return String DOM对象
     *
     */
    public static String ten2HexString(int i) {
        String tenToHexString = Integer.toHexString(i);
        if (tenToHexString.length() % 2 != 0) {
            tenToHexString = "0" + tenToHexString;
        }

        return tenToHexString;
    }

    /**
     * 16 进制转10进制
     *
     * @return String 10进制
     *
     */
    public static long hexto10(String b) {
        return Long.parseLong(hexbackrow(b), 16);
    }

    /**
     * 16进制顺序颠倒
     *
     *
     */
    public static String hexbackrow(String b) {
        String c = "";
        String d = "";
        int b_len = b.length();
        int b_len_t = b_len / 2;
        if (b_len % 2 != 0) {
            d = "0" + b.substring(0, 1);
        }
        for (int i = 0; i < b_len_t; i++) {
            c = c + b.substring(b_len - 2, b_len);
            b_len = b_len - 2;
        }
        return c + d;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
