package com.ningmeng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangb on 2020/3/10.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestJWT {

    //生成一个jwt令牌
    @Test
    public void testCreateJwt(){
        //证书文件
        String key_location = "nm.keystore";
        //密钥库密码
        String keystore_password ="ningmeng";
        //访问证书路径
        ClassPathResource resource = new ClassPathResource(key_location);
        //密钥工厂
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, keystore_password.toCharArray());
        //密钥的密码，此密码和别名要匹配
        String keypassword = "ningmeng";
        //密钥别名
        String alias = "nmkey";
        //密钥对（密钥和公钥）
        KeyPair keyPair = keyStoreKeyFactory.getKeyPair(alias,keypassword.toCharArray());
        //私钥
        RSAPrivateKey aPrivate = (RSAPrivateKey) keyPair.getPrivate();
        //定义payload信息   负载信息
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", "123");
        tokenMap.put("name", "ningmeng");
        tokenMap.put("roles", "admin,user");
        //生成jwt令牌   根据密钥生成jwt令牌
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(tokenMap), new RsaSigner(aPrivate));
        //取出jwt令牌
        String token = jwt.getEncoded();
        System.out.println("token="+token);
        //token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6ImFkbWluLHVzZXIiLCJuYW1lIjoibmluZ21lbmciLCJpZCI6IjEyMyJ9.r_LLbvdasZaS_smx6rbdpIKVq93kFtTNtLB6nQ5ayuyvVyHXSLA3Q44KY8eWcNiDwkv5c8IF5GlduVNK8brSX01fhBud75uEVQVYwi_BWT9TZUKIxrPI_DjoNiWXuptSG5GB-mYocmhenqoIsgyg3Td4CTiLMRSKiyBcHbSJNoFOIo1fo-YwGF3HoGGk671zo4NRAd_8IJN6cOJNVdzRmRL39JVFBgddJuS7b-htWC15ihuDQqJkxiXzqOTCj5WY-0jXKWx1CTg_LInt055rVc9vj1hIHojxLeKmxfUKQPIiSmQXDMdckCctvgRc2eYj3NabTkA8yqWlZnuVtvAtEQ
    }
    //资源服务使用公钥验证jwt的合法性，并对jwt解码
    @Test
    public void testVerify(){
        //jwt令牌
        String token="eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlcyI6ImFkbWluLHVzZXIiLCJuYW1lIjoibmluZ21lbmciLCJpZCI6IjEyMyJ9.r_LLbvdasZaS_smx6rbdpIKVq93kFtTNtLB6nQ5ayuyvVyHXSLA3Q44KY8eWcNiDwkv5c8IF5GlduVNK8brSX01fhBud75uEVQVYwi_BWT9TZUKIxrPI_DjoNiWXuptSG5GB-mYocmhenqoIsgyg3Td4CTiLMRSKiyBcHbSJNoFOIo1fo-YwGF3HoGGk671zo4NRAd_8IJN6cOJNVdzRmRL39JVFBgddJuS7b-htWC15ihuDQqJkxiXzqOTCj5WY-0jXKWx1CTg_LInt055rVc9vj1hIHojxLeKmxfUKQPIiSmQXDMdckCctvgRc2eYj3NabTkA8yqWlZnuVtvAtEQ";
        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA0JWLscE2/Xz9OcQ9+H4LuP/ifrTdM7dZoga/t1xMH37GEdYOmwRLidiUYHkuTRTaWNgaTthtbyKsByVMOwTc+zpRf2nR9YAde8+ZNysk6gHjtfcEJ2qzx+Gr1SZMC27uuXKg1SktIzpvI5q+eBE+QUVtHG/nMfqEDPFtoyfasi6eSenWvw/MChc2wPEDTW/oTghzS99Jx5wfhUjf3Zf05VotyBjqOgywV6XlOpWjE/P4BV2NKj6TMs5+/gQJnoB9FmGRt7FPr7kBBHRq8YJXaOjOalvGZ9xPaL8F5uKZ571z7fgqCBLhzeHA5B+tYOdedEGx9Y47qYKyW7v+gh/+RQIDAQAB-----END PUBLIC KEY-----";
        //校验jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));
        //获取jwt原始内容
        String claims = jwt.getClaims();
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println("encoded="+encoded);
    }
}
