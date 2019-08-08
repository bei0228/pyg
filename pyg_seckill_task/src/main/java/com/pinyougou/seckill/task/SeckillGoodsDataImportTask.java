package com.pinyougou.seckill.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class SeckillGoodsDataImportTask {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Scheduled(cron = "0/20 * * * * ?")
    public void importSeckillGoodsFromDBToRedis(){
        //1.查询所有审核通过（status=1），剩余库存大于0（stockCount>0），开始时间<= 当前时间 <结束时间
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        criteria.andStockCountGreaterThan(0);
        Date date = new Date();
        criteria.andStartTimeLessThanOrEqualTo(date);
        criteria.andEndTimeGreaterThan(date);
        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
        //2.循环将数据以goodsId为key保存到redis的hash中
        for(TbSeckillGoods goods : seckillGoodsList){
            redisTemplate.boundHashOps(TbSeckillGoods.class.getSimpleName()).put(goods.getId(),goods);
        }
    }
}
