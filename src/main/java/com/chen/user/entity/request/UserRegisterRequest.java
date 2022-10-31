package com.chen.user.entity.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Galaxy
 * @version v1.0
 * @date 2022/5/24
 */
@Data
public class UserRegisterRequest implements Serializable     {
    private static final long serialVersionUID = -7033069688476394327L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;
}
