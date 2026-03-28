package com.prakhar.common.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private String mobile;
    private String role;
    private boolean isVerified;
    private boolean isTwoFactorEnabled;
    private String authProvider;
    
    private boolean signupBonusAvailed;
    private boolean isActive;
    private String profilePicture;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String country;
    private String pinCode;
    private String kycStatus;
    private LocalDateTime kycVerifiedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserDTO() {}

    public UserDTO(Long id, String fullName, String email, String mobile, String role, boolean isVerified, 
                   boolean isTwoFactorEnabled, String authProvider, boolean signupBonusAvailed, boolean isActive, 
                   String profilePicture, LocalDate dateOfBirth, String phoneNumber, String address, String city, 
                   String state, String country, String pinCode, String kycStatus, LocalDateTime kycVerifiedAt, 
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.role = role;
        this.isVerified = isVerified;
        this.isTwoFactorEnabled = isTwoFactorEnabled;
        this.authProvider = authProvider;
        this.signupBonusAvailed = signupBonusAvailed;
        this.isActive = isActive;
        this.profilePicture = profilePicture;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pinCode = pinCode;
        this.kycStatus = kycStatus;
        this.kycVerifiedAt = kycVerifiedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserDTOBuilder builder() {
        return new UserDTOBuilder();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }
    public boolean isTwoFactorEnabled() { return isTwoFactorEnabled; }
    public void setTwoFactorEnabled(boolean twoFactorEnabled) { isTwoFactorEnabled = twoFactorEnabled; }
    public String getAuthProvider() { return authProvider; }
    public void setAuthProvider(String authProvider) { this.authProvider = authProvider; }
    public boolean isSignupBonusAvailed() { return signupBonusAvailed; }
    public void setSignupBonusAvailed(boolean signupBonusAvailed) { this.signupBonusAvailed = signupBonusAvailed; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getPinCode() { return pinCode; }
    public void setPinCode(String pinCode) { this.pinCode = pinCode; }
    public String getKycStatus() { return kycStatus; }
    public void setKycStatus(String kycStatus) { this.kycStatus = kycStatus; }
    public LocalDateTime getKycVerifiedAt() { return kycVerifiedAt; }
    public void setKycVerifiedAt(LocalDateTime kycVerifiedAt) { this.kycVerifiedAt = kycVerifiedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static class UserDTOBuilder {
        private Long id;
        private String fullName;
        private String email;
        private String mobile;
        private String role;
        private boolean isVerified;
        private boolean isTwoFactorEnabled;
        private String authProvider;
        private boolean signupBonusAvailed;
        private boolean isActive;
        private String profilePicture;
        private LocalDate dateOfBirth;
        private String phoneNumber;
        private String address;
        private String city;
        private String state;
        private String country;
        private String pinCode;
        private String kycStatus;
        private LocalDateTime kycVerifiedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        UserDTOBuilder() {}

        public UserDTOBuilder id(Long id) { this.id = id; return this; }
        public UserDTOBuilder fullName(String fullName) { this.fullName = fullName; return this; }
        public UserDTOBuilder email(String email) { this.email = email; return this; }
        public UserDTOBuilder mobile(String mobile) { this.mobile = mobile; return this; }
        public UserDTOBuilder role(String role) { this.role = role; return this; }
        public UserDTOBuilder isVerified(boolean isVerified) { this.isVerified = isVerified; return this; }
        public UserDTOBuilder isTwoFactorEnabled(boolean isTwoFactorEnabled) { this.isTwoFactorEnabled = isTwoFactorEnabled; return this; }
        public UserDTOBuilder authProvider(String authProvider) { this.authProvider = authProvider; return this; }
        public UserDTOBuilder signupBonusAvailed(boolean signupBonusAvailed) { this.signupBonusAvailed = signupBonusAvailed; return this; }
        public UserDTOBuilder isActive(boolean isActive) { this.isActive = isActive; return this; }
        public UserDTOBuilder profilePicture(String profilePicture) { this.profilePicture = profilePicture; return this; }
        public UserDTOBuilder dateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; return this; }
        public UserDTOBuilder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public UserDTOBuilder address(String address) { this.address = address; return this; }
        public UserDTOBuilder city(String city) { this.city = city; return this; }
        public UserDTOBuilder state(String state) { this.state = state; return this; }
        public UserDTOBuilder country(String country) { this.country = country; return this; }
        public UserDTOBuilder pinCode(String pinCode) { this.pinCode = pinCode; return this; }
        public UserDTOBuilder kycStatus(String kycStatus) { this.kycStatus = kycStatus; return this; }
        public UserDTOBuilder kycVerifiedAt(LocalDateTime kycVerifiedAt) { this.kycVerifiedAt = kycVerifiedAt; return this; }
        public UserDTOBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public UserDTOBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public UserDTO build() {
            return new UserDTO(id, fullName, email, mobile, role, isVerified, isTwoFactorEnabled, authProvider, 
                               signupBonusAvailed, isActive, profilePicture, dateOfBirth, phoneNumber, address, 
                               city, state, country, pinCode, kycStatus, kycVerifiedAt, createdAt, updatedAt);
        }
    }
}
