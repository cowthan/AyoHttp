package org.ayo.http.stream;

import java.io.File;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/8/15.
 */
public abstract class StreamConverter<T> {
    public abstract T convert(InputStream in);


    public static class ByteArrayConverter extends StreamConverter<byte[]>{
        @Override
        public byte[] convert(InputStream in) {
            return new byte[0];
        }
    }

    public static class FileConverter extends StreamConverter<File>{

        private File outFile;

        public FileConverter(File savedFile){
            outFile = savedFile;
        }

        @Override
        public File convert(InputStream in) {
            return null;
        }
    }

    public static class StringConverter extends StreamConverter<String>{
        @Override
        public String convert(InputStream in) {
            return "";
        }
    }

}
