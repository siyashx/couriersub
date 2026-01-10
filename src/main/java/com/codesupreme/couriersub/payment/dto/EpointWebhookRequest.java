package com.codesupreme.couriersub.payment.dto;

// Sadə saxlayırıq: ePoint webhook hansı field-lərlə gəlirsə sonra uyğunlaşdırarıq
public class EpointWebhookRequest {
    public String providerOrderId; // bizim order id (və ya ePoint order)
    public String transactionId;   // ePoint trans id
    public String status;          // "SUCCESS" / "FAILED"
}
