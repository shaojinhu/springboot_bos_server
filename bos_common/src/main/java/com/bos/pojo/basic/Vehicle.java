package com.bos.pojo.basic;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "vehicle")
public class Vehicle implements Serializable {

    @TableId(value = "id",type = IdType.ID_WORKER_STR)
    private String id;

    @TableField(value = "route_type")
    private String routeType;

    @TableField(value = "route_name")
    private String routeName;

    @TableField(value = "shipper")
    private String shipper;

    @TableField(value = "driver")
    private String driver;

    @TableField(value = "vehicle_num")
    private String vehicleNum;

    @TableField(value = "vehicle_type")
    private String vehicleType;

    @TableField(value = "ton")
    private String ton;

    @TableField(value = "remark")
    private String remark;
}
