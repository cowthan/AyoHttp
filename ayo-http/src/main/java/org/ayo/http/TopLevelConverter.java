package org.ayo.http;

/**
 */
public interface TopLevelConverter<T extends StringTopLevelModel> {

    T convert(String s);

}
