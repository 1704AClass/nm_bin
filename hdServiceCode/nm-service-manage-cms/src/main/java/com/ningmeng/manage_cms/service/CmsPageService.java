package com.ningmeng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.ningmeng.framework.domain.cms.CmsPage;
import com.ningmeng.framework.domain.cms.CmsSite;
import com.ningmeng.framework.domain.cms.request.QueryPageRequest;
import com.ningmeng.framework.domain.cms.response.CmsCode;
import com.ningmeng.framework.domain.cms.response.CmsPageResult;
import com.ningmeng.framework.domain.cms.response.CmsPostPageResult;
import com.ningmeng.framework.exception.CustomExceptionCast;
import com.ningmeng.framework.model.response.CommonCode;
import com.ningmeng.framework.model.response.QueryResponseResult;
import com.ningmeng.framework.model.response.QueryResult;
import com.ningmeng.framework.model.response.ResponseResult;
import com.ningmeng.manage_cms.config.RabbitmqConfig;
import com.ningmeng.manage_cms.dao.CmsPageRepository;
import com.ningmeng.manage_cms.dao.CmsSiteRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by wangb on 2020/2/11.
 */
@Service
public class CmsPageService {
    @Autowired
    private CmsPageRepository cmsPageRepository;
    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    public ResponseResult postPage(String pageId){
        boolean flag = createHtml();
        if(!flag){
            CustomExceptionCast.cast(CommonCode.FAIL);
        }
        //查询数据库
        CmsPage cmsPage = this.getById(pageId);
        if(cmsPage == null){
            System.out.print("我是空的");
            CustomExceptionCast.cast(CommonCode.FAIL);
        }
        Map<String,String> msgMap = new HashMap<>();
        msgMap.put("pageId",pageId);
        //消息内容
        String msg = JSON.toJSONString(msgMap);
        //获取站点id作为routingKey
        String siteId = cmsPage.getSiteId();
        //发送jsonpageId
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,msg);
        return new ResponseResult(CommonCode.SUCCESS);
    }
    //创建静态页面
    public boolean createHtml(){
        System.out.println("静态化完成");
        return true;
    }
    /**
     * 页面列表分页查询
     * @param page 当前页码
     * @param size 页面显示个数
     * @param queryPageRequest 查询条件
     * @return 页面列表
     */
    public QueryResponseResult findList(int page,int size,QueryPageRequest queryPageRequest){
        //条件匹配器
        //页面名称模糊查询，需要自定义字符串的匹配器实现模糊查询
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());
        //条件值
        CmsPage cmsPage = new CmsPage();
        //站点ID
        if(StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        } //页面别名
        if(StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        } //创建条件实例
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);
        //页码
        page = page-1;
        //分页对象
        Pageable pageable = new PageRequest(page, size);
        //分页查询
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<CmsPage>();
        cmsPageQueryResult.setList(all.getContent());
        cmsPageQueryResult.setTotal(all.getTotalElements());
        //返回结果
        return new QueryResponseResult(CommonCode.SUCCESS,cmsPageQueryResult);
    }

    //添加页面
    public ResponseResult add(CmsPage cmsPage){
        //校验cmsPage是否为空
        if(cmsPage == null){
            CustomExceptionCast.cast(CommonCode.FAIL);
        }
        //校验页面是否存在，根据页面名称、站点Id、页面webpath查询
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        //校验页面是否存在，已存在则抛出异常
        if(cmsPage1!=null){
            CustomExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }
        cmsPage.setPageId(null);
        //添加页面主键由spring data 自动生成
        CmsPage cmsPage2 = cmsPageRepository.save(cmsPage);
        //返回结果
        ResponseResult responseResult=new ResponseResult();
        responseResult.setCode(10000);
        responseResult.setSuccess(true);
        responseResult.setMessage(JSON.toJSONString(cmsPage2));
       // CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS,cmsPage);
        return responseResult;
    }

    //根据id查询页面
    public CmsPage getById(String id){
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if(optional.isPresent()){
            return optional.get();
        }
        //返回空
        return null;
    }
    //更新页面信息
    public CmsPageResult update(String id,CmsPage cmsPage) {
        //根据id查询页面信息
        CmsPage one = this.getById(id);
        if (one != null) {
            //更新模板id
            one.setTemplateId(cmsPage.getTemplateId());
            //更新所属站点
            one.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());
            //更新页面名称
            one.setPageName(cmsPage.getPageName());
            //更新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());
            //更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
            //执行更新
            CmsPage save = cmsPageRepository.save(one);
            if (save != null) {
                //返回成功
                CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, save);
                return cmsPageResult;
            }
        }
        //返回失败
        return new CmsPageResult(CommonCode.FAIL,null);
    }
    //删除页面
    public ResponseResult delete(String id) {
       CmsPage one= this.getById(id);
       if(one!=null){
           cmsPageRepository.deleteById(id);
           return new ResponseResult(CommonCode.SUCCESS);
       }
       return new ResponseResult(CommonCode.FAIL);
    }

    public String preview(String cmsPageId) {
        //获取数据模型
        Map model = getModelByPageId(cmsPageId);
        if(model==null){
            //根据模型获取不到
            CustomExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //获取页面的模板信息
        String template = getTemplateByPageId(cmsPageId);
        if(StringUtils.isEmpty(template)){
            CustomExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        String html = generateHtml(template, model);
        //CmsPage cmsPage = cmsPageRepository.findById(cmsPageId).get();
        return html;
    }
    //获取数据模型
    private Map getModelByPageId(String pageId){
        //取出页面信息
        CmsPage cmsPage = cmsPageRepository.findById(pageId).get();
        //取出页面的dateUrl
        String dataUrl = cmsPage.getDataUrl();
        //通过restTemplate请求dateUrl获取数据
        RestTemplate restTemplate=new RestTemplate();
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body=forEntity.getBody();
        return body;
    }
    //获取页面的模板信息
    private String getTemplateByPageId(String pageId){
        //取出页面的信息
        CmsPage cmsPage = cmsPageRepository.findById(pageId).get();
        //根据cmspage.getTemplateByPageId();获取GridFS中保存模板文件内容
        String TemplatValue ="asdasdasdasd";
        return TemplatValue;
    }
    private String generateHtml(String temlateContent,Map model){
        return "<html>...</html>";
    }

    //一键发布页面
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        //添加页面
        ResponseResult result = this.add(cmsPage);
        if(!result.isSuccess()){
            return new CmsPostPageResult(CommonCode.FAIL,null);
        }
        CmsPage cmsPage1 = JSON.parseObject(result.getMessage(),CmsPage.class);
        //要布的页面id
        String pageId = cmsPage1.getPageId();
        //发布页面
        ResponseResult responseResult = this.postPage(pageId);
        if(!responseResult.isSuccess()){
            return new CmsPostPageResult(CommonCode.FAIL,null);
        }
        //得到页面的url
        //页面url=站点域名+站点webpath+页面webpath+页面名称
        //站点id
        String siteId = cmsPage1.getSiteId();
        //查询站点信息
        CmsSite cmsSite = findCmsSiteById(siteId);
        //站点域名
        String siteDomain = cmsSite.getSiteDomain();
        //站点web路径
        String siteWebPath = cmsSite.getSiteWebPath();
        //页面web路径
        String pageWebPath = cmsPage1.getPageWebPath();
        //页面名称
        String pageName = cmsPage1.getPageName();
        //页面的web访问地址
        String pageUrl = siteDomain+siteWebPath+pageWebPath+pageName;
        return new CmsPostPageResult(CommonCode.SUCCESS,pageUrl);
    }
    //根据id查询站点信息
    public CmsSite findCmsSiteById(String siteId){
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }
}
