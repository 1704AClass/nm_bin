package com.ningmeng.auth;

import com.ningmeng.framework.client.NmServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Created by wangb on 2020/3/10.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestClient {
    @Autowired
    LoadBalancerClient loadBalancerClient;
    @Autowired
    RestTemplate restTemplate;//远程连接使用   httpclient
    @Test
    public void testPasswrodEncoder(){
        String password = "111111";
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        for(int i=0;i<10;i++) {
            //每个计算出的Hash值都不一样
            String hashPass = passwordEncoder.encode(password);
            System.out.println(hashPass);
            //虽然每次计算的密码Hash值不一样但是校验是通过的
            boolean f = passwordEncoder.matches(password, hashPass);
            System.out.println(f);
        }
    }
    @Test
    public void testClient(){
        //采用客户端负载均衡，动态从eureka获取认证服务的ip 和端口
        ServiceInstance serviceInstance = loadBalancerClient.choose(NmServiceList.nm_SERVICE_UCENTER_AUTH);
        URI uri = serviceInstance.getUri();
        String authUrl = uri+"/auth/oauth/token";
        //URI url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType
        // url就是 申请令牌的url /oauth/token
        //method http的方法类型
        //requestEntity请求内容
        //responseType，将响应的结果生成的类型
        //请求的内容分两部分
        //1、header信息，包括了http basic认证信息
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        String httpbasic = httpbasic("NmWebApp", "NmWebApp");
        //"Basic WGNXZWJBcHA6WGNXZWJBcHA="
        headers.add("Authorization", httpbasic);
        //2、包括：grant_type、username、passowrd
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type","password");
        body.add("username","ningmeng");
        body.add("password","1234");
        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = new
                HttpEntity<MultiValueMap<String, String>>(body, headers);
        //指定 restTemplate当遇到400或401响应时候也不要抛出异常，也要正常返回值
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                //当响应的值为400或401时候也要正常响应，不要抛出异常
                if(response.getRawStatusCode()!=400 && response.getRawStatusCode()!=401){
                    super.handleError(response);
                }
            }
        });
        //远程调用申请令牌   1.路径  2.请求类型 3.头部headers和body 4.返回值类型
        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST,
                multiValueMapHttpEntity, Map.class);
        Map body1 = exchange.getBody();
        System.out.println(body1);
        //{access_token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Im5pbmdtZW5nIiwic2NvcGUiOlsiYXBwIl0sIm5hbWUiOm51bGwsInV0eXBlIjpudWxsLCJpZCI6bnVsbCwiZXhwIjoxNTgzODkwMjI3LCJqdGkiOiIyZTY3ZTQwZS0zN2NlLTQ4OWEtYjZlZS1mY2EzZmI2NTg5ODciLCJjbGllbnRfaWQiOiJObVdlYkFwcCJ9.yY8Dk1kU7BuFiMYxsu9cXb2pIudJdymTsbL0ufWQe8MSPBWKUUr-XfE1PkUTpGBEWOk4kXS7f4XP5MhsCb-LV4cBHXJDVp0MNYeim5qSfi4kqw3GdcegZhEQ5fED-cxX_qvT4hmcvl4SVuiBjvz6416jO9w6DKg6jEikojDWdolBEY3VGqY4_5xfRJwNtzBwB3RGDYcul2XjZDx6C2XOe8D1vjD7iBqnYFMXoddeqWqVO_URwskL5dzoZgRU0wqHdLU_HjvvSFXpI3bRADjHBYI-_zzUxY0jldxQ-8s33dGbqznK6H3kqk5znVY-Ka_ZmOCnawfxBvlIHeKEsEJlOg
        // , token_type=bearer
        // , refresh_token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Im5pbmdtZW5nIiwic2NvcGUiOlsiYXBwIl0sImF0aSI6IjJlNjdlNDBlLTM3Y2UtNDg5YS1iNmVlLWZjYTNmYjY1ODk4NyIsIm5hbWUiOm51bGwsInV0eXBlIjpudWxsLCJpZCI6bnVsbCwiZXhwIjoxNTgzODkwMjI3LCJqdGkiOiJhNGQ5MmMzMi02NmQyLTQwY2EtOGRkNC0xMjA0OTI1YzkyMmIiLCJjbGllbnRfaWQiOiJObVdlYkFwcCJ9.wCuZWyMmpQwgEBSwXuj-FK16U6Odi-KDCYwrMValq-bmgNCL94Myb34hsjKnj3dKOJlRHy5HOi4LbVs3YcBVc-uEH1F4p7M3nj17VlAyMpXLYrIImEqNND_LiCff98iit6hd4_uoHSWjMwm2K9JgokEOI6lQzBeLVKE8N-eMYXv-pxO9QQvHzn2CGP1iiOqcSW4P1dkfR2uzxY5COdqeckQqRzgc4wltPUDBO_rdVOdOE1UPjtRdOUW9-LLgRFt6XYHThXOFL_4SaGclZDIooT0Xxy40HxT4rVOqLjd3KyzWn95csW04wpWNJxoSQpSlfAFWH0pV6pFahWXkqKnfeA
        // , expires_in=43199
        // , scope=app
        // , jti=2e67e40e-37ce-489a-b6ee-fca3fb658987}
    }
    private String httpbasic(String clientId,String clientSecret){
        //将客户端id和客户端密码拼接，按“客户端id:客户端密码”
        String string = clientId+":"+clientSecret;
        //进行base64编码
        byte[] encode = Base64Utils.encode(string.getBytes());
        return "Basic "+new String(encode);
    }
}
