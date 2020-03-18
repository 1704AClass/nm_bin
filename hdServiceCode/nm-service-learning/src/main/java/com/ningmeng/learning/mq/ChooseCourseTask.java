package com.ningmeng.learning.mq;

import com.alibaba.fastjson.JSON;
import com.ningmeng.framework.domain.task.NmTask;
import com.ningmeng.framework.model.response.ResponseResult;
import com.ningmeng.learning.config.RabbitMQConfig;
import com.ningmeng.learning.service.LearningService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by wangb on 2020/3/17.
 */
@Component
public class ChooseCourseTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);
    @Autowired
    LearningService learningService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    /**
     * 接收选课任务
     */
    @RabbitListener(queues = {RabbitMQConfig.NM_LEARNING_ADDCHOOSECOURSE})
    public void receiveChoosecourseTask(NmTask nmTask, Message message, Channel channel) throws IOException {
        LOGGER.info("receive choose course task,taskId:{}",nmTask.getId());
        //接收到 的消息id
        String id = nmTask.getId();
        //添加选课
        try {
            String requestBody = nmTask.getRequestBody();
            Map map = JSON.parseObject(requestBody, Map.class);
            String userId = (String) map.get("userId");
            String courseId = (String) map.get("courseId");
            String valid = (String) map.get("valid");
            Date startTime = null;
            Date endTime = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            if(map.get("startTime")!=null){
                startTime =dateFormat.parse((String) map.get("startTime"));
            }
            if(map.get("endTime")!=null){
                endTime =dateFormat.parse((String) map.get("endTime"));
            }
            //添加选课
            ResponseResult addcourse = learningService.addcourse(userId, courseId, valid,startTime, endTime,nmTask);
            //选课成功发送响应消息
            if(addcourse.isSuccess()){
                //发送响应消息
                rabbitTemplate.convertAndSend(RabbitMQConfig.EX_LEARNING_ADDCHOOSECOURSE,
                        RabbitMQConfig.NM_LEARNING_FINISHADDCHOOSECOURSE_KEY, nmTask );
                LOGGER.info("send finish choose course taskId:{}",id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("send finish choose course taskId:{}", id);
        }
    }
}
