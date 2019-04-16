package net.lzzy.practicesonline.constants;

import net.lzzy.practicesonline.utils.AppUtils;

/**
 * Created by lzzy_gxy on 2019/4/15.
 * Description:
 */
public class ApiConstans {
    private static final String IP= AppUtils.loadServerSetting(AppUtils.getContext()).first;
    private static final String PORT=AppUtils.loadServerSetting(AppUtils.getContext()).second;
    private static final String PROTOCOL="http://";

    /**
     * API地址
     */
    public static final String URL_API=PROTOCOL.concat(IP).concat(":").concat(PORT);

}
