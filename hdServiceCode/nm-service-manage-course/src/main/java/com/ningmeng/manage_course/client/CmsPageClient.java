package com.ningmeng.manage_course.client;

import com.ningmeng.framework.client.NmServiceList;
import com.ningmeng.framework.domain.cms.CmsPage;
import com.ningmeng.framework.domain.cms.response.CmsPageResult;
import com.ningmeng.framework.model.response.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Created by wangb on 2020/2/22.
 */
@FeignClient(value = NmServiceList.nm_SERVICE_MANAGE_CMS)
public interface CmsPageClient {
    //feignClient接口 有参数在参数必须加@PathVariable("XXX")和@RequestParam("XXX")
    //feignClient返回值为复杂对象时其类型必须有无参构造函数
    @GetMapping("/cms/findById/{id}")
    public CmsPage findById(@PathVariable("id") String id);
    @PutMapping("/cms/update/{id}")//这里使用put方法，http 方法中put表示更新
    public CmsPageResult update(@PathVariable("id") String id, @RequestBody CmsPage cmsPage);
    @DeleteMapping("/cms/delete/{id}")
    public ResponseResult delete(@PathVariable("id") String id);
}
