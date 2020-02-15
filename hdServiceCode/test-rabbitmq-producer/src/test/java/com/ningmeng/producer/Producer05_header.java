package com.ningmeng.producer;

import com.rabbitmq.client.*;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by wangb on 2020/2/14.
 */
public class Producer05_header {
    //队列名称
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";
    private static final String EXCHANGE_HEADERS_INFORM="exchange_headers_inform";
    public static void main(String[] args) {
        //发送邮件
        Map<String, Object> headers_email = new Hashtable<String, Object>();
        headers_email.put("inform_type", "email");
        //发送短信
        Map<String, Object> headers_sms = new Hashtable<String, Object>();
        headers_sms.put("inform_type", "sms");
        try {
            //创建初始化连接工厂
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("127.0.0.1");
            //浏览器15672  后台5672
            factory.setPort(5672);
            factory.setUsername("guest");
            factory.setPassword("guest");
            //rabbitmq默认虚拟机名称为“/”，虚拟机相当于一个独立的mq服务器
            factory.setVirtualHost("/");
            //创建与RabbitMQ服务的TCP连接
            Connection connection = factory.newConnection();
            //创建与Exchange的通道，每个连接可以创建多个通道，每个通道代表一个会话任务
            Channel channel = connection.createChannel();
            /**
             * 声明队列，如果Rabbit中没有此队列将自动创建
             * param1:队列名称
             * param2:是否持久化
             * param3:队列是否独占此连接
             * param4:队列不再使用时是否自动删除此队列
             * param5:队列参数
             */
            channel.queueDeclare(QUEUE_INFORM_EMAIL,true,false,false,null);
            channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);
            //声明交换机 String exchange, BuiltinExchangeType type
            /**
             * 参数明细
             * 1、交换机名称
             * 2、交换机类型，fanout、topic、direct、headers
             */
            channel.exchangeDeclare(EXCHANGE_HEADERS_INFORM, BuiltinExchangeType.HEADERS);
            //交换机和队列绑定String queue, String exchange, String routingKey
            /**
             * 参数明细
             * 1、队列名称
             * 2、交换机名称
             * 3、路由key
             */
            channel.queueBind(QUEUE_INFORM_EMAIL,EXCHANGE_HEADERS_INFORM,"",headers_email);
            channel.queueBind(QUEUE_INFORM_SMS,EXCHANGE_HEADERS_INFORM,"",headers_sms);

            for(int i=0;i<5;i++){
                /**
                 * 消息发布方法
                 * param1：Exchange的名称，如果没有指定，则使用Default Exchange
                 * param2:routingKey,消息的路由Key，是用于Exchange（交换机）将消息转发到指定的消息队列
                 * param3:消息包含的属性
                 * param4：消息体
                 */
                Map<String,Object> headers = new Hashtable<String, Object>();
                headers.put("inform_type", "email");//匹配email通知消费者绑定的header
                AMQP.BasicProperties.Builder properties = new AMQP.BasicProperties.Builder();
                properties.headers(headers);
                String body="xiaomi 你好  收邮件";
                System.out.println("send"+new Date()+"信息"+body);
                channel.basicPublish(EXCHANGE_HEADERS_INFORM, "", properties.build(), body.getBytes());
            }
            for(int i=0;i<10;i++){
                /**
                 * 消息发布方法
                 * param1：Exchange的名称，如果没有指定，则使用Default Exchange
                 * param2:routingKey,消息的路由Key，是用于Exchange（交换机）将消息转发到指定的消息队列
                 * param3:消息包含的属性
                 * param4：消息体
                 */
                Map<String,Object> headers = new Hashtable<String, Object>();
                headers.put("inform_type", "sms");//匹配sms通知消费者绑定的header
                AMQP.BasicProperties.Builder properties = new AMQP.BasicProperties.Builder();
                properties.headers(headers);
                String body="xiaomi 你好  收短信";
                System.out.println("send"+new Date()+"信息"+body);
                channel.basicPublish(EXCHANGE_HEADERS_INFORM, "", properties.build(), body.getBytes());
            }

            channel.close();
            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }

    }
}
