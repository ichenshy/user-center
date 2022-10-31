package com.chen.user.entity.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Galaxy
 * @version v1.0
 * @date 2022/5/24
 */
@Data
public class UserLoginRequest implements Serializable     {
    private static final long serialVersionUID = -3564195431317902614L;
    private String userAccount;
    private String userPassword;
}
