package com.codesupreme.couriersub.admin.dto;

import com.codesupreme.couriersub.common.enums.VerifyStatus;

import java.time.LocalDateTime;

public class AdminUserListItem {
    public Long id;
    public String firstName;
    public String lastName;
    public String phone;
    public boolean active;
    public VerifyStatus verifyStatus;
    public LocalDateTime createdAt;
}
