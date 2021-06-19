package com.zhuweitung;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
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
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.collections4.CollectionUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.List;

/**
 * 头像拼接
 * @author zhuweitung
 * @create 2021/6/19 
 */
@Log4j2
public class AvatarSplice {

    /**
     * 纵横比
     */
    public transient static final double ASPECT_RATIO = 3;
    /**
     * 头像宽度
     */
    public transient static final Integer AVATAR_WIDTH = 100;
    /**
     * 输出图片宽度
     */
    public transient static final Integer OUTPUT_WIDTH = 1920;
    /**
     * 关注up列表
     */
    private static List<Friend> friends = new ArrayList<>();
    /**
     * 头像缓存
     */
    private static List<BufferedImage> avatars = new ArrayList<>();
    /**
     * 头像保存路径
     */
    private static final File AVATAR_DIR = new File("avatars");
    static {
        if (!AVATAR_DIR.exists()) {
            AVATAR_DIR.mkdirs();
        }
    }
    private static final File OUTPUT_DIR = new File("output");
    static {
        if (!OUTPUT_DIR.exists()) {
            OUTPUT_DIR.mkdirs();
        }
    }

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
        getUpList();

        // 缓存up头像
        cacheUpAvatar();

        // 分析头像分布情况（按输出图像纵横比计算）
        int avatarNum = avatars.size();
        int colNum = (int) Math.floor(Math.sqrt(avatarNum * 1.0 / ASPECT_RATIO) * ASPECT_RATIO);
        int rowNum = (int) Math.ceil(avatarNum * 1.0 / colNum);

        // 缩放
        scale(AVATAR_WIDTH);

        // 将头像按规则画到空画布上
        draw(colNum, rowNum, AVATAR_WIDTH);

    }

    /**
     * 获取关注up列表
     * @param
     * @return void
     * @author zhuweitung
     * @date 2021/6/19
     */
    private static void getUpList() {
        friends = new ArrayList<>();
        log.info("-------开始获取关注up列表-------");
        Map<String, String> params = new HashMap<>();
        int page = 1;
        params.put("count", "100");
        params.put("page", page + "");
        params.put("groupId", "-1");
        params.put("action", "7");
        boolean end = false;
        while (!end) {
            JsonObject responseJson = HttpUtil.doGet(AcFunApi.GET_FOLLOWUPLIST.getUrl(), params);
            int responseCode = responseJson.get("result").getAsInt();
            if (responseCode == 0) {
                int totalCount = responseJson.get("totalCount").getAsInt();
                List<Friend> list = new Gson().fromJson(responseJson.getAsJsonArray("friendList"), new TypeToken<ArrayList<Friend>>() {
                }.getType());
                if (CollectionUtils.isNotEmpty(list)) {
                    friends.addAll(list);
                    if (friends.size() != totalCount) {
                        params.put("page", (++page) + "");
                    } else {
                        end = true;
                    }
                } else {
                    end = true;
                }
            } else {
                log.debug("请求{}接口出错，请稍后重试。错误请求信息：{}", AcFunApi.GET_FOLLOWUPLIST.getName(), responseJson);
                break;
            }
        }
        log.info("共获取{}个up信息", friends.size());
        log.info("-------结束获取关注up列表-------");
    }

    /**
     * 缓存up头像
     * @return void
     * @author zhuweitung
     * @date 2021/6/19
     */
    private static void cacheUpAvatar() {
        avatars = new ArrayList<>();
        log.info("-------开始缓存up头像-------");

        int index = 1;
        int total = friends.size();
        for (Friend friend : friends) {
            if (StrUtil.isNotBlank(friend.getUserImg())) {
                BufferedImage image = getBufferedImageFromUrl(friend.getUserId(), friend.getUserImg());
                if (image != null) {
                    avatars.add(image);
                    log.info("进度{}/{}，缓存up{}的头像成功，url={}", index, total, friend.getUserName(), friend.getUserImg());
                } else {
                    log.warn("进度{}/{}，缓存up{}的头像失败，url={}", index, total, friend.getUserName(), friend.getUserImg());
                }
            } else {
                log.warn("进度{}/{}，up{}的头像地址不存在，url={}", index, total, friend.getUserName());
            }
            index++;
        }

        log.info("共成功缓存{}个up头像，失败{}个", avatars.size(), total - avatars.size());
        log.info("-------结束缓存up头像-------");
    }

    /**
     * 缩放图片
     * @param width 图片宽高
     * @return void
     * @author zhuweitung
     * @date 2021/6/19
     */
    private static void scale(int width) {
        log.info("-------开始缩放缓存图像-------");

        for (BufferedImage image : avatars) {
            try {
                image = Thumbnails.of(image).size(width, width).asBufferedImage();
            } catch (IOException e) {
                log.error("图片缩放出错，{}", e.getMessage());
            }
        }

        log.info("-------结束缩放缓存图像-------");
    }

    /**
     * 将头像按规则画到空画布上
     * @param colNum 列数
     * @param rowNum 行数
     * @param width 图片宽高
     * @return void
     * @author zhuweitung
     * @date 2021/6/19
     */
    private static void draw(int colNum, int rowNum, int width) {

        log.info("-------开始绘制图像-------");

        // 补缺
        BufferedImage defaultAvatar = getDefaultAvatar();
        if (avatars.size() < colNum * rowNum) {
            int num = colNum * rowNum - avatars.size();
            for (int i = 0; i < num; i++) {
                avatars.add(defaultAvatar);
            }
        }
        // 打乱顺序
        Collections.shuffle(avatars);

        BufferedImage newImage = new BufferedImage(width * colNum, width * rowNum, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = newImage.createGraphics();
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                int index = i * colNum + j;
                BufferedImage avatar = avatars.get(index);
                g2d.drawImage(avatar, j * width, i * width, width, width, null);
            }
        }
        g2d.dispose();

        // 输出
        /*// 输出灰度图
        newImage = (BufferedImage) ImgUtil.gray(newImage);*/
        // 缩放
        try {
            newImage = Thumbnails.of(newImage).size(OUTPUT_WIDTH, OUTPUT_WIDTH).asBufferedImage();
        } catch (IOException e) {
            log.error("图片缩放出错，{}", e.getMessage());
        }
        File file = new File(StrUtil.format("output/bg-{}.jpg", DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN)));
        try {
            ImageIO.write(newImage, "jpg", file);
        } catch (IOException e) {
            log.error("绘制图像失败，url={}, {}", file.getAbsoluteFile(), e.getMessage());
        }

        log.info("-------结束绘制图像-------");
    }

    /**
     * 读取默认头像
     * @param
     * @return java.awt.image.BufferedImage
     * @author zhuweitung
     * @date 2021/6/19
     */
    public static BufferedImage getDefaultAvatar() {
        BufferedImage defaultAvatar = null;
        InputStream inputStream = AvatarSplice.class.getClass().getResourceAsStream("/default.jpg");
        try {
            defaultAvatar = ImageIO.read(inputStream);
        } catch (IOException e) {
            log.error("读取resources目录下default.jpg文件出错");
        }
        if (defaultAvatar == null) {
            defaultAvatar = new BufferedImage(AVATAR_WIDTH, AVATAR_WIDTH, BufferedImage.TYPE_INT_RGB);
        }
        return defaultAvatar;
    }

    /**
     * 远程图片转BufferedImage
     * @param userId 用户id
     * @param imgUrl 图片地址
     * @return java.awt.image.BufferedImage
     * @author zhuweitung
     * @date 2021/6/19
     */
    public static BufferedImage getBufferedImageFromUrl(String userId, String imgUrl) {

        BufferedImage image = null;

        File file = new File(StrUtil.format("avatars/{}.jpg", userId));
        // 判断是否已下载
        if (file.exists()) {
            try {
                image = ImageIO.read(file);
                return image;
            } catch (IOException e) {
                log.error("获取图片出错, url={}, ", file.getAbsoluteFile(), e.getMessage());
            }
        }
        HttpURLConnection conn = null;
        try {
            URL url = new URL(imgUrl);
            conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() == 200) {
                image = ImageIO.read(conn.getInputStream());
                ImageIO.write(image, "jpg", file);
                return image;
            }
        } catch (Exception e) {
            log.error("获取网络图片出错, url={}, ", imgUrl, e.getMessage());
        } finally {
            conn.disconnect();
        }
        return null;
    }

}
