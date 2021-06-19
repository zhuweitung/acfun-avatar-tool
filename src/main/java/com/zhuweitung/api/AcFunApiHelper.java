package com.zhuweitung.api;

import com.google.gson.JsonObject;
import com.zhuweitung.signin.Token;
import com.zhuweitung.util.HttpUtil;
import lombok.extern.log4j.Log4j2;

/**
 * AcFun API常用工具类
 * @author zhuweitung
 * @create 2021/4/18 
 */
@Log4j2
public class AcFunApiHelper {

    /**
     * @description 初始化token
     * @param
     * @return void
     * @author zhuweitung
     * @date 2021/4/18
     */
    public static void initToken() {
        JsonObject responseJson = HttpUtil.doPost(AcFunApi.GET_TOKEN.getUrl(), "sid=acfun.midground.api");
        int responseCode = responseJson.get("result").getAsInt();
        if (responseCode == 0) {
            String ssecurity = responseJson.get("ssecurity").getAsString();
            Integer userId = responseJson.get("userId").getAsInt();
            String acfunMidgroundApiSt = responseJson.get("acfun.midground.api_st").getAsString();
            String acfunMidgroundApiAt = responseJson.get("acfun.midground.api.at").getAsString();
            Token.init(ssecurity, userId, acfunMidgroundApiSt, acfunMidgroundApiAt);
        } else {
            log.debug("请求{}接口出错，请稍后重试。错误请求信息：{}", AcFunApi.GET_TOKEN.getName(), responseJson);
        }
    }

}
