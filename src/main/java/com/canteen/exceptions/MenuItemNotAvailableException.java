package com.canteen.exceptions;

public class MenuItemNotAvailableException extends RuntimeException {
    public MenuItemNotAvailableException(String message) { super(message); }
}