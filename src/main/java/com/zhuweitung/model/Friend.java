package com.zhuweitung.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 关注up信息对象
 * @author zhuweitung
 * @create 2021/6/19 
 */
@NoArgsConstructor
@Data
public class Friend {

    @SerializedName("groupId")
    private String groupId;
    @SerializedName("groupName")
    private String groupName;
    @SerializedName("gender")
    private Integer gender;
    @SerializedName("userId")
    private String userId;
    @SerializedName("isFriend")
    private Boolean isFriend;
    @SerializedName("followingCount")
    private Integer followingCount;
    @SerializedName("isSignedUpCollege")
    private Boolean isSignedUpCollege;
    @SerializedName("userName")
    private String userName;
    @SerializedName("userImg")
    private String userImg;
    @SerializedName("verifiedType")
    private Integer verifiedType;
    @SerializedName("verifiedText")
    private String verifiedText;
    @SerializedName("nameColor")
    private Integer nameColor;
    @SerializedName("verifiedTypes")
    private List<Integer> verifiedTypes;
    @SerializedName("contributeCount")
    private Integer contributeCount;
    @SerializedName("isFollowing")
    private Boolean isFollowing;
    @SerializedName("followingStatus")
    private Integer followingStatus;
    @SerializedName("fanCount")
    private Integer fanCount;
    @SerializedName("comeFrom")
    private String comeFrom;
    @SerializedName("sexTrend")
    private Integer sexTrend;
    @SerializedName("signature")
    private String signature;
    @SerializedName("followingCountShow")
    private String followingCountShow;
    @SerializedName("fanCountShow")
    private String fanCountShow;
    @SerializedName("contributeCountShow")
    private String contributeCountShow;

}
