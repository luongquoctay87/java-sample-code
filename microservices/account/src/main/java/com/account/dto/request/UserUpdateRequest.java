package com.account.dto.request;

import com.account.util.UserStatus;
import com.account.util.UserType;
import com.account.util.UserTypeSubset;
import com.account.validator.EnumNamePattern;
import com.account.validator.ValueOfEnum;
import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

import static com.account.util.UserType.*;

@Data
public class UserUpdateRequest implements Serializable {
    @Min(value = 1, message = "Id must be greater than or equal to 1")
    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String password;
    @EnumNamePattern(name = "status", regexp = "ACTIVE|INACTIVE|NONE")
    private UserStatus status;
    @UserTypeSubset(anyOf = {SYSADMIN, ADMIN, MANAGER, USER})
    private UserType userType;
    @ValueOfEnum(name="type", regexp = "(SYSADMIN | ADMIN | MANAGER | USER)", enumClass = UserType.class)
    private String type;
}
