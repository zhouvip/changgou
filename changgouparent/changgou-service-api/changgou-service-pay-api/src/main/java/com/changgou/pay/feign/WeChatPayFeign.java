package com.changgou.pay.feign;

import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient("weixinpay")
@RequestMapping("/weixin/pay")
public interface WeChatPayFeign {

    /**
     * 关闭订单
     * @param outtradeno    商户订单号
     * @return
     * @throws Exception
     */
    @RequestMapping("/cancel/order")
    Result<Map<String,String>> closeOrder(String outtradeno);

}
