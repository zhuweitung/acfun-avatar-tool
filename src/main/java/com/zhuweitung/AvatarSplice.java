package com.zhuweitung;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.zhuweitung.api.AcFunApi;
import com.zhuweitung.api.AcFunApiHelper;
import com.zhuweitung.model.Friend;
import com.zhuweitung.signin.Cookie;
import com.zhuweitung.signin.ServerVerify;
import com.zhuweitung.util.HttpUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 头像拼接
 * @author zhuweitung
 * @create 2021/6/19 
 */
@Log4j2
public class AvatarSplice {

    /**
     * 纵横比：宽
     */
    public transient static final Integer ASPECT_RATIO_WIDTH = 16;
    /**
     * 纵横比：高
     */
    public transient static final Integer ASPECT_RATIO_HEIGHT = 9;
    /**
     * 头像宽度
     */
    public transient static final Integer AVATAR_WIDTH = 100;
    /**
     * 关注up列表
     */
    private static List<Friend> friends = new ArrayList<>();
    /**
     * 头像缓存
     */
    private static List<BufferedImage> avatars = new ArrayList<>();

    public static void main(String[] args) {

        if (args.length < 2) {
            log.info("任务启动失败");
            log.warn("Cookies参数缺失，需要配置acPassToken和authKey参数");
            return;
        }
        //读取环境变量
        Cookie.init(args[0], args[1]);

        if (args.length > 2) {
            ServerVerify.verifyInit(args[2]);
        }

        //初始化token
        AcFunApiHelper.initToken();

        // 获取关注up列表

        // 缓存up头像

        // 分析头像分布情况（按输出图像纵横比计算）

        // 将头像按规则画到空画布上

        // 缩放（可选）

        // 输出

    }

}
