package com.ningmeng.manage_course.dao;

import com.ningmeng.framework.domain.course.TeachplanMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by wangb on 2020/2/18.
 */

public interface TeachplanMediaRepository extends JpaRepository<TeachplanMedia, String> {
    //从TeachplanMedia查询课程计划媒资信息
    List<TeachplanMedia> findByCourseId(String courseId);
}
