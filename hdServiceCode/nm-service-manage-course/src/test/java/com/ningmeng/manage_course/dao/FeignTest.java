package com.ningmeng.manage_course.dao;

import com.ningmeng.framework.domain.cms.CmsPage;
import com.ningmeng.manage_course.client.CmsPageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by wangb on 2020/2/22.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class FeignTest {
    @Autowired
    CmsPageClient cmsPageClient;
    @Test
    public void testFeign() {
        //通过服务id调用cms的查询页面接口
        CmsPage cmsPage = cmsPageClient.findById("5abefd525b05aa293098fca6");
        System.out.println("-----------------------------------------------------"+cmsPage);
    }
    @Test
    public void update() {
        //通过服务id调用cms的查询页面接口
        CmsPage cmsPage=new CmsPage();
        cmsPage.setPageId("5e45399e59989e3fa00c629e");
        cmsPage.setPageName("1");
        cmsPage.setPageAliase("1");
        cmsPage.setPageWebPath("1");
        cmsPage.setPageParameter("1");
        cmsPage.setPagePhysicalPath("1");
        cmsPage.setPageType("1");
        cmsPage.setPageTemplate("1");
        cmsPage.setPageHtml("1");
        cmsPage.setPageStatus("1");
        cmsPage.setTemplateId("1");
        cmsPage.setSiteId("1");
        cmsPageClient.update(cmsPage.getPageId(),cmsPage);
        System.out.println("-------------------------------------------------添加成功");
    }
    @Test
    public void del() {
        //通过服务id调用cms的查询页面接口
        cmsPageClient.delete("5e527fc0b76de4082c8ef775");
        System.out.println("-----------------------------------------------------删除成功");
    }
}
