package com.ningmeng.manage_course.dao;

import com.ningmeng.framework.domain.course.CourseMarket;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by Administrator.
 */
@Mapper
public interface CourseMarketMapper {
   CourseMarket findById(String id);
}
