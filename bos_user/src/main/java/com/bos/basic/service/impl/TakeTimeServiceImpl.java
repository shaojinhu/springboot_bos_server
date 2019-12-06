package com.bos.basic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bos.basic.mapper.TakeTimeMapper;
import com.bos.basic.service.TakeTimeService;
import com.bos.pojo.basic.TakeTime;
import com.bos.response.PageResult;
import com.bos.response.Result;
import com.bos.response.ResultCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@SuppressWarnings("all")
public class TakeTimeServiceImpl implements TakeTimeService {

    @Resource
    private TakeTimeMapper takeTimeMapper;

    /**
     * 获得TakeTime的列表，有分页
     * @param map
     * @return
     */
    @Override
    public Result getTakeTimeList(Map<String, String> map) {
        Integer page = Integer.parseInt(map.get("page"));
        Integer size = Integer.parseInt(map.get("size"));
        IPage<TakeTime> iPage  = new Page<>(page,size);
        IPage<TakeTime> iPageResult = takeTimeMapper.selectPage(iPage, null);
        PageResult<TakeTime> pageResult = new PageResult<>(iPageResult.getTotal(),iPageResult.getRecords());
        return new Result(ResultCode.SUCCESS,pageResult);
    }

    /**
     * 添加TakeTime，收派时间管理
     * @param takeTime
     * @return
     */
    @Override
    public Result addTakeTime(TakeTime takeTime,Map<String,String> map) {
        takeTime.setOperator(map.get("nikename"));
        takeTime.setOperatingCompany(map.get("company"));
        takeTime.setOperatingTime(map.get("operatingTime"));
        //首次添加设置收派时间管理状态为禁用，需要手动启用  0启用   1禁用
        takeTime.setStatus("1");
        int insert = takeTimeMapper.insert(takeTime);
        if(insert > 0){
            return Result.SUCCESS();
        }
        return Result.FAIL();
    }

    /**
     * 修改收派时间管理
     * @param takeTime
     * @param baseController
     * @return
     */
    @Override
    public Result updateTakeTime(TakeTime takeTime, Map<String, String> map) {
        if(takeTime.getStatus().equals("0")){//为0是启用状态，不可随意修改，应先禁用
            return new Result(ResultCode.DISABLED_IS_NOT_UPDATE);
        }
        takeTime.setOperator(map.get("nikename"));
        takeTime.setOperatingCompany(map.get("company"));
        takeTime.setOperatingTime(map.get("operatingTime"));
        int i = takeTimeMapper.updateById(takeTime);
        if(i > 0){
            return Result.SUCCESS();
        }
        return Result.FAIL();
    }

    /**
     * 收派时间管理启禁
     * @param takeTime
     * @param map
     * @return
     */
    @Override
    public Result revTakeTime(TakeTime takeTime, Map<String, String> map) {
        takeTime.setOperator(map.get("nikename"));
        takeTime.setOperatingCompany(map.get("company"));
        takeTime.setOperatingTime(map.get("operatingTime"));
        if(takeTime.getStatus().equals("0")){
            takeTime.setStatus("1");
        }else{
            takeTime.setStatus("0");
        }
        int i = takeTimeMapper.updateById(takeTime);
        if(i > 0){
            return Result.SUCCESS();
        }
        return Result.FAIL();
    }



}
