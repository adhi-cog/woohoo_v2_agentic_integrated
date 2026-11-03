package com.sb.woohoo.utils;

public interface Utterances {
    interface ToDo {
        String NO_TODO = "There are no to-do's added";
        String TODO_FOUND_PREFIX = "You have ";
        String TODO_FOUND_SUFFIX_SINGULAR = " To Do";
        String TODO_FOUND_SUFFIX_PLURAL = " To Do's";
        String TODO_DELETED_ALL = "All To Do's are cleared";
        String TODO_DELETED_SINGLE = "To Do Deleted";
        String TODO_MODIFIED = "To Do Modified";
        String TODO_ADDED = "To Do Added";
    }

    interface CommonError {
        String UNKNOWN = "Something went wrong!";
    }
}