package com.ningmeng.manage_course.dao;

import com.ningmeng.framework.domain.course.ext.CategoryNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by wangb on 2020/2/19.
 */
@Mapper
public interface CategoryMapper {
    CategoryNode selectList();
}
