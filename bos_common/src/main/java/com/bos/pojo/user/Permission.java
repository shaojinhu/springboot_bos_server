package com.bos.pojo.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 权限Permission
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "permission")
public class Permission implements Serializable {

    @TableId(value = "pid")
    private Integer pid;

    @TableField(value = "pname")
    private String pname;

    @TableField(value = "ppermiss")
    private String ppermiss;

    @TableField(value = "ptype")
    private String ptype;

    @TableField(value = "parentid")
    private String parentid;

    @TableField(value = "pdesc")
    private String pdesc;

    @TableField(value = "is_show")
    private String isShow;

    @TableField(exist = false)
    private List<Permission> childPerList;
}
