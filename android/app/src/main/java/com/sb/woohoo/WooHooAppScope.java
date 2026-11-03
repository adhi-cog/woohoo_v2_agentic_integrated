package com.sb.woohoo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

/**
 * Created by admin on 2/1/18.
 */

@Scope
@Retention(RetentionPolicy.CLASS)
public @interface WooHooAppScope {
}
