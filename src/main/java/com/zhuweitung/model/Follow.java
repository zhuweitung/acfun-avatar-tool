package com.zhuweitung.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 关注up列表接口返回对象
 * @author zhuweitung
 * @create 2021/6/19 
 */
@NoArgsConstructor
@Data
public class Follow {

    @SerializedName("result")
    private Integer result;
    @SerializedName("pcursor")
    private String pcursor;
    @SerializedName("friendList")
    private List<Friend> friendList;
    @SerializedName("host-name")
    private String hostname;
    @SerializedName("totalCount")
    private Integer totalCount;
    
}
