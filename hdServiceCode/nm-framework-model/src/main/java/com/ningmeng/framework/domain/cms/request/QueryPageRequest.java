package com.ningmeng.framework.domain.cms.request;

import com.ningmeng.framework.model.request.RequestData;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by wangb on 2020/2/11.
 * 使用页面查询时候自己封装的类
 * 条件查询只需要传递 一个查询对象就行了。
 */
@Data
public class QueryPageRequest extends RequestData{
    //站点id
    @ApiModelProperty("站点id")
    private String siteId;
    //页面id
    @ApiModelProperty("页面ID")
    private String pageId;
    //页面名称
    @ApiModelProperty("页面名称")
    private String pageName;
    //别名
    @ApiModelProperty("别名")
    private String pageAliase;
    //模板Id
    @ApiModelProperty("模板ID")
    private String templateId;
}
