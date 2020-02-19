package com.ningmeng.manage_course.dao;

import com.ningmeng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created by wangb on 2020/2/18.
 */
@Mapper
public interface TeachplanMapper {
    public TeachplanNode selectList(String courseId);
}
