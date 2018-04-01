package com.zmall.task;

import com.zmall.common.Const;
import com.zmall.util.PropertiesUtil;
import com.zmall.util.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author:ztian
 * @Description:
 * @CreateTime: 2018/4/1  17:55
 */
@Slf4j
@Component
public class OrderCloseTask {
    //@Scheduled(cron = "0/5 * * * * ? ")
    private void closeOrderTask1(){
        log.info("关闭订单定时任务启动");
        long timeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout","5000"));
        Long setnxResult = RedisPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
                String.valueOf(System.currentTimeMillis() + timeout));
        if(setnxResult != null && setnxResult.intValue() == 1){
            //分布式锁任务
        }else{
            log.info("没有获取到分布式锁:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }
        log.info("关闭订单定时任务结束");
    }
    //@Scheduled(cron = "0 0/30 * * * ?")
    private void closeOrderTask2(){
        log.info("关闭订单定时任务启动");
        long timeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout","5000"));
        Long setnxResult = RedisPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
                String.valueOf(System.currentTimeMillis() + timeout));
        // 成功设置锁，直接执行分布式锁任务
        if(setnxResult != null && setnxResult.intValue() == 1){
            log.info("获取到分布式锁:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            //分布式锁任务
            closeOrderTask(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }else{
            // 判断分布式锁是否过期，若已过期，也可执行分布式锁任务
            String lockValueStr = RedisPoolUtil.get(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            if(lockValueStr != null && System.currentTimeMillis() > Long.parseLong(lockValueStr)){
                String returnOldValue = RedisPoolUtil.getset(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis() + timeout));
                if(returnOldValue == null || StringUtils.equals(lockValueStr,returnOldValue)){
                    log.info("获取到分布式锁:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                    //分布式锁任务
                    closeOrderTask(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }else{
                    log.info("没有获取到分布式锁:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }
            }else {
                log.info("没有获取到分布式锁:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            }
        }
        log.info("关闭订单定时任务结束");
    }
    private void closeOrderTask(String lockName){
        // 设置锁的有效期
        RedisPoolUtil.expire(lockName,5);
        // TODO:删除订单的任务
        // 删除锁
        RedisPoolUtil.del(lockName);
    }
}
