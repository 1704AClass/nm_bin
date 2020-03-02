package com.ningmeng.manage_cms_client.service;

import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @version 1.0
 **/
@Service
public class PageService {
    //将页面html保存到页面物理路径
    public void PageTest(String pageId){
        System.out.println("我被执行了");
    }
    //保存html页面到服务器物理路径
    public void savaPageToServerPath(String pageId){
        //根据pageId查询cmsPage
        //得到html的文件id，从cmsPage中获取htmlFileId内容
        //从gridFS中查询html文件
        //得到站点id
        //得到站点的信息
        //得到站点的物理路径
        //得到页面的物理路径（页面物理路径=站点物理路径+页面物理路径+页面名称。）
        //讲html文件保存到服务器物理路径上（把文件放在nginx能接管的位置）
    }
}
