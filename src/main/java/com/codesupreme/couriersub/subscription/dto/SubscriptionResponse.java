package com.codesupreme.couriersub.subscription.dto;

import com.codesupreme.couriersub.common.enums.SubscriptionStatus;

import java.time.LocalDateTime;

public class SubscriptionResponse {
    public SubscriptionStatus status;
    public String plan;
    public LocalDateTime currentPeriodStart;
    public LocalDateTime currentPeriodEnd;
}
