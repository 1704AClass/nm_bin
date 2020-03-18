package com.ningmeng.learning.service;

import com.ningmeng.framework.domain.course.TeachplanMediaPub;
import com.ningmeng.framework.domain.learning.NmLearningCourse;
import com.ningmeng.framework.domain.learning.response.GetMediaResult;
import com.ningmeng.framework.domain.task.NmTask;
import com.ningmeng.framework.domain.task.NmTaskHis;
import com.ningmeng.framework.exception.CustomExceptionCast;
import com.ningmeng.framework.model.response.CommonCode;
import com.ningmeng.framework.model.response.ResponseResult;
import com.ningmeng.learning.client.CourseSearchClient;
import com.ningmeng.learning.dao.NmLearningCourseRepository;
import com.ningmeng.learning.dao.NmTaskHisRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * Created by wangb on 2020/3/6.
 */
@Service
public class LearningService {
    @Autowired
    CourseSearchClient courseSearchClient;
    @Autowired
    NmTaskHisRepository nmTaskHisRepository;
    @Autowired
    NmLearningCourseRepository nmLearningCourseRepository;
    //获取课程
    public GetMediaResult getMedia(String courseId, String teachplanId) {
        //校验学生的学习权限。。是否资费等
        //调用搜索服务查询
        TeachplanMediaPub teachplanMediaPub = courseSearchClient.getmedia(teachplanId);
        if(teachplanMediaPub == null || StringUtils.isEmpty(teachplanMediaPub.getMediaUrl())){
            //获取视频播放地址出错
            CustomExceptionCast.cast(CommonCode.FAIL);
        }
        return new GetMediaResult(CommonCode.SUCCESS,teachplanMediaPub.getMediaUrl());
    }
    //完成选课
    @Transactional
    public ResponseResult addcourse(String userId, String courseId, String valid, Date startTime, Date endTime, NmTask nmTask){
        if (StringUtils.isEmpty(courseId)) {
            CustomExceptionCast.cast(CommonCode.FAIL);
        }
        if (StringUtils.isEmpty(userId)) {
            CustomExceptionCast.cast(CommonCode.FAIL);
        }
        if(nmTask == null || StringUtils.isEmpty(nmTask.getId())){
            CustomExceptionCast.cast(CommonCode.FAIL);
        }
       //查询历史任务
        Optional<NmTaskHis> optional =nmTaskHisRepository.findById(nmTask.getId());
        if(optional.isPresent()){//已经有历史了
            return new ResponseResult(CommonCode.SUCCESS);
        }
        NmLearningCourse nmLearningCourse = nmLearningCourseRepository.findNmLearningCourseByUserIdAndCourseId(userId, courseId);
        if (nmLearningCourse == null) {//没有选课记录则添加
            nmLearningCourse = new NmLearningCourse();
            nmLearningCourse.setUserId(userId);
            nmLearningCourse.setCourseId(courseId);
            nmLearningCourse.setValid(valid);
            nmLearningCourse.setStartTime(startTime);
            nmLearningCourse.setEndTime(endTime);
            nmLearningCourse.setStatus("501001");
            nmLearningCourseRepository.save(nmLearningCourse);
        } else {//有选课记录则更新日期
            nmLearningCourse.setValid(valid);
            nmLearningCourse.setStartTime(startTime);
            nmLearningCourse.setEndTime(endTime);
            nmLearningCourse.setStatus("501001");
            nmLearningCourseRepository.save(nmLearningCourse);
        }
        //向历史任务表播入记录
        Optional<NmTaskHis> optional1 = nmTaskHisRepository.findById(nmTask.getId());
        if(!optional1.isPresent()){//!取反  为空
            //添加历史任务
            NmTaskHis nmTaskHis = new NmTaskHis();
            BeanUtils.copyProperties(nmTask,nmTaskHis);
            nmTaskHisRepository.save(nmTaskHis);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
