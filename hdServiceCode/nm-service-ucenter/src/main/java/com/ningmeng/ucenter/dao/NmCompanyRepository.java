package com.ningmeng.ucenter.dao;

import com.ningmeng.framework.domain.ucenter.NmCompany;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wangb on 2020/3/13.
 */
public interface NmCompanyRepository extends JpaRepository<NmCompany, String> {
}
