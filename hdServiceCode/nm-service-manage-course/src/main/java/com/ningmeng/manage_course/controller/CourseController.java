package com.ningmeng.manage_course.controller;

import com.ningmeng.api.courseapi.CourseControllerApi;
import com.ningmeng.framework.domain.course.CourseBase;
import com.ningmeng.framework.domain.course.CourseMarket;
import com.ningmeng.framework.domain.course.CoursePic;
import com.ningmeng.framework.domain.course.Teachplan;
import com.ningmeng.framework.domain.course.ext.CategoryNode;
import com.ningmeng.framework.domain.course.ext.TeachplanNode;
import com.ningmeng.framework.domain.course.response.AddCourseResult;
import com.ningmeng.framework.domain.course.response.CoursePublishResult;
import com.ningmeng.framework.domain.course.response.CourseView;
import com.ningmeng.framework.model.response.QueryResponseResult;
import com.ningmeng.framework.model.response.ResponseResult;
import com.ningmeng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by wangb on 2020/2/18.
 */
@RestController
@RequestMapping("/course")
public class CourseController implements CourseControllerApi {
    @Autowired
    CourseService courseService;

    //查询课程计划
    @Override
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachplanList(String courseId) {
        return courseService.findTeachplanList(courseId);
    }

    @Override
    @PostMapping("/teachplan/add")
    public ResponseResult addTeachplan(@RequestBody Teachplan teachplan) {
        return courseService.addTeachplan(teachplan);
    }

    @Override
    @PostMapping("/teachplan/findCourseList")
    public QueryResponseResult findCourseList(int page, int size, String companyId) {
        return courseService.findCourseList(page,size,companyId);
    }



    @Override
    @GetMapping("/xy/findList")
    public CategoryNode findList() {
        return courseService.findList();
    }
    @Override
    @PostMapping("/xy/add/{courseBase}")
    public AddCourseResult addCourseBase(@RequestBody CourseBase courseBase) {
        return courseService.addCourseBase(courseBase);
    }

    @Override
    @GetMapping("/xy/getCourseBaseById/{id}")
    public CourseBase getCourseBaseById(String id) throws RuntimeException {
        return courseService.getCourseBaseById(id);
    }

    @Override
    @PostMapping("/xy/updateCourseBase/{id}/{courseBase}")
    public ResponseResult updateCourseBase(@PathVariable String id,@RequestBody CourseBase courseBase) {
        return courseService.updateCourseBase(id,courseBase);
    }

    @Override
    @GetMapping("/xy/getCourseMarketById/{id}")
    public CourseMarket getCourseMarketById(String id) {
        return courseService.getCourseMarketById(id);
    }

    @Override
    @PostMapping("/xy/updateCourseMarket/{id}/{courseMarket}")
    public ResponseResult updateCourseMarket(String id, CourseMarket courseMarket) {
        return courseService.updateCourseMarket(id,courseMarket);
    }

    @Override
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(@RequestParam(value = "courseId",required = true) String courseId,@RequestParam(value = "pic",required = true) String pic) {
        //保存课程图片
        return courseService.saveCoursePic(courseId,pic);
    }

    @Override
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic findCoursePic(@PathVariable("courseId") String courseId) {
        return courseService.findCoursepic(courseId);
    }

    @Override
    @DeleteMapping("/coursepic/delete")
    public ResponseResult deleteCoursePic(@RequestParam("courseId") String courseId) {
        return courseService.deleteCoursePic(courseId);
    }

    @Override
    @GetMapping("/courseview/{id}")
    public CourseView courseview(@PathVariable("id")String id) {
        return courseService.getCoruseView(id);
    }
    @Override
    @PostMapping("/preview/{id}")
    public CoursePublishResult preview(@PathVariable("id") String id) {
        return courseService.preview(id);
    }

    @Override
    @PostMapping("/publish/{id}")
    public CoursePublishResult publish(@PathVariable String id) {
        return courseService.publish(id);
    }
}
