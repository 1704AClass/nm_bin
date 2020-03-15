package com.ningmeng.ucenter.dao;


import com.ningmeng.framework.domain.ucenter.NmMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by wangb on 2020/3/13.
 */
public interface NmMenuMapper {
    List<NmMenu> selectPermissionByUserId(@Param("id") String userid);
}
