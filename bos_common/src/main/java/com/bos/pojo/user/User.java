package com.bos.pojo.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户User
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName(value = "user")
public class User implements Serializable {

    @TableId(value = "userid")
    private String userid;

    @TableField(value = "username")
    private String username;

    @TableField(value = "password")
    private String password;

    @TableField(value = "nikename")
    private String nikename;
}
