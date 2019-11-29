package com.bos.pojo.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 角色Role
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "role")
public class Role implements Serializable {

    @TableField(value = "rid")
    private Integer rid;

    @TableField(value = "rname")
    private String rname;
}
