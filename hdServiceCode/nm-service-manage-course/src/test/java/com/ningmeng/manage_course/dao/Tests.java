package com.ningmeng.manage_course.dao;

import com.ningmeng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

/**
 * Created by wangb on 2020/2/22.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class Tests {
    @Autowired
    RestTemplate restTemplate;
    //负载均衡调用
    @Test
    public void testRibbon() {
        //服务id
        String serviceId = "NM-SERVICE-MANAGE-CMS";
        String id="5abefd525b05aa293098fca6";
        for(int i=0;i<5;i++){
            //通过服务id调用
            ResponseEntity<CmsPage> forEntity = restTemplate.getForEntity("http://" + serviceId
                    + "/cms/findById/"+id, CmsPage.class);
            CmsPage cmsPage = forEntity.getBody();
            System.out.println("--------------------------"+cmsPage);
        }
    }
}
