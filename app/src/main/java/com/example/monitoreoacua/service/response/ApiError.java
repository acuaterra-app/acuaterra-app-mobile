package com.example.monitoreoacua.service.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ApiError {
    @SerializedName("message")
    private String message;

    @SerializedName("errors")
    private List<ErrorDetail> errors;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ErrorDetail> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorDetail> errors) {
        this.errors = errors;
    }

    public static class ErrorDetail {
        @SerializedName("type")
        private String type;

        @SerializedName("value")
        private String value;

        @SerializedName("msg")
        private String msg;

        @SerializedName("path")
        private String path;

        @SerializedName("location")
        private String location;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
}