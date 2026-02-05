package com.canteen.dto;

public final class Result<T> {
    private final boolean success;
    private final T data;
    private final String error;

    private Result(boolean success, T data, String error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(true, data, null);
    }

    public static <T> Result<T> fail(String error) {
        return new Result<>(false, null, error);
    }

    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public String getError() { return error; }
}
