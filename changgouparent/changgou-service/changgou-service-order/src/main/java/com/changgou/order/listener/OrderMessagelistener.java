package com.changgou.order.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.order.service.OrderService;
import com.changgou.pay.feign.WeChatPayFeign;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = {"${mq.pay.queue.order}"})
public class OrderMessagelistener {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private WeChatPayFeign weChatPayFeign;

    /***
     * 接收消息
     */
    @RabbitHandler
    public void consumeMessage(String msg){
        //将数据转成Map
        Map<String,String> resultMap = JSON.parseObject(msg,Map.class);
        System.out.println("监听到的支付结果"+resultMap);

        //return_code=SUCCESS
        String return_code = resultMap.get("return_code");
        //业务结果
        String result_code = resultMap.get("result_code");

        //业务结果 result_code=SUCCESS/FAIL，修改订单状态
        if(return_code.equalsIgnoreCase("success") ){
            //获取订单号
            String outtradeno = resultMap.get("out_trade_no");
            //业务结果
            if(result_code.equalsIgnoreCase("success")){
                if(outtradeno != null){
                    //修改订单状态  out_trade_no
                    orderService.updateStatus(outtradeno,resultMap.get("time_end"),resultMap.get("transaction_id"));
                }
            }else{
                //支付失败，调用微信api取消关闭订单
                weChatPayFeign.closeOrder(outtradeno);
                //回滚库存
                orderService.deleteOrder(outtradeno);
            }
        }

    }
}
