package com.ningmeng.manage_cms.dao;

import com.ningmeng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by wangb on 2020/2/25.
 */
public interface CmsSiteRepository extends MongoRepository<CmsSite,String> {
}
