package net.lzzy.practicesonline.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by lzzy_gxy on 2019/4/24.
 * Description:
 */
public class DateTimeUtils {
    public static final SimpleDateFormat DATE_TIME_FORMOT=
            new SimpleDateFormat("yyyy_MM_dd HH:mm:ss", Locale.CHINA);
    public static final SimpleDateFormat DATE_FORMAT=
            new SimpleDateFormat("yyyy_MM_dd",Locale.CHINA);
}
