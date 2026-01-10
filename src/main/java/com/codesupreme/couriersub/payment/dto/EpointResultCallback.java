package com.codesupreme.couriersub.payment.dto;

public class EpointResultCallback {
    public String data;       // base64 json
    public String signature;  // base64 sha1(private+data+private)
}
