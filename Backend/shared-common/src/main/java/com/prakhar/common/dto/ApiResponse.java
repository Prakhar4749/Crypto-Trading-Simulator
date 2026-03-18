package com.prakhar.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private ErrorDetails error;

    public ApiResponse() {}

    public ApiResponse(boolean success, String message, T data, ErrorDetails error) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public ErrorDetails getError() { return error; }
    public void setError(ErrorDetails error) { this.error = error; }

    // ═══ Static factory methods ═══

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, null);
    }

    public static <T> ApiResponse<T> error(String message, ErrorDetails errorDetails) {
        return new ApiResponse<>(false, message, null, errorDetails);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetails {
        private String code;
        private int status;
        private String path;
        private String timestamp;
        private List<String> details;

        public ErrorDetails() {}

        public ErrorDetails(String code, int status, String path, String timestamp, List<String> details) {
            this.code = code;
            this.status = status;
            this.path = path;
            this.timestamp = timestamp;
            this.details = details;
        }

        // Getters and Setters
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
        public List<String> getDetails() { return details; }
        public void setDetails(List<String> details) { this.details = details; }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String code;
            private int status;
            private String path;
            private String timestamp;
            private List<String> details;

            public Builder code(String code) { this.code = code; return this; }
            public Builder status(int status) { this.status = status; return this; }
            public Builder path(String path) { this.path = path; return this; }
            public Builder timestamp(String timestamp) { this.timestamp = timestamp; return this; }
            public Builder details(List<String> details) { this.details = details; return this; }
            public ErrorDetails build() {
                return new ErrorDetails(code, status, path, timestamp, details);
            }
        }
    }
}
