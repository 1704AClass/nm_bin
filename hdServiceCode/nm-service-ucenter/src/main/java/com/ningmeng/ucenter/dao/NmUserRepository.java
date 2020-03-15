package com.ningmeng.ucenter.dao;

import com.ningmeng.framework.domain.ucenter.NmUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wangb on 2020/3/11.
 */
public interface NmUserRepository extends JpaRepository<NmUser, String> {
    NmUser findNmUserByUsername(String username);
}
