package com.codesupreme.couriersub.user.dto;

import jakarta.validation.constraints.NotBlank;

public class SubmitDocsRequest {

    @NotBlank
    private String phone;

    @NotBlank
    private String profileImageUrl;

    @NotBlank
    private String idFrontUrl;

    @NotBlank
    private String idBackUrl;

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getIdFrontUrl() { return idFrontUrl; }
    public void setIdFrontUrl(String idFrontUrl) { this.idFrontUrl = idFrontUrl; }

    public String getIdBackUrl() { return idBackUrl; }
    public void setIdBackUrl(String idBackUrl) { this.idBackUrl = idBackUrl; }
}
