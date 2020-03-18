package com.ningmeng.order.mq;

import com.ningmeng.framework.domain.task.NmTask;
import com.ningmeng.order.config.RabbitMQConfig;
import com.ningmeng.order.service.TaskService;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by wangb on 2020/3/17.
 */
@Component
public class ChooseCourseTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);
    @Autowired
    TaskService taskService;
    //每隔1分钟扫描消息表，向mq发送消息
    @Scheduled(fixedDelay = 60000)
    public void sendChoosecourseTask(){
        //取出当前时间1分钟之前的时间
        Calendar calendar =new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.MINUTE,-1);
        Date time = calendar.getTime();
        List<NmTask> taskList = taskService.findTaskList(time, 1000);

        //遍历任务列表
        for(NmTask nmTask:taskList){
            //任务id
            String taskId=nmTask.getId();
            //版本号
            Integer version = nmTask.getVersion();
            //调用乐观锁方法校验任务是否可以执行
            if(taskService.getTask(taskId, version)>0){
                //发送选课消息
                taskService.publish(nmTask, nmTask.getMqExchange(),nmTask.getMqRoutingkey());
                LOGGER.info("send choose course task id:{}",taskId);
            }
            //发送选课消息
            taskService.publish(nmTask, nmTask.getMqExchange(),nmTask.getMqRoutingkey());
            LOGGER.info("send choose course task id:{}",taskId);
        }
        LOGGER.info("时间："+time);
        System.out.println("时间："+time);
    }

    /**
     * 接收选课响应结果
     */
    @RabbitListener(queues = {RabbitMQConfig.NM_LEARNING_FINISHADDCHOOSECOURSE})
    public void receiveFinishChoosecourseTask(NmTask task, Message message, Channel channel) throws IOException {
        LOGGER.info("receiveChoosecourseTask...{}",task.getId());
        //接收到 的消息id
        String id = task.getId();
        //删除任务，添加历史任务
        taskService.finishTask(id);
    }
}
