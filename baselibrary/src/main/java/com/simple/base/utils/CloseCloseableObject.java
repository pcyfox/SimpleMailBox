package com.simple.base.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * 关闭实现Closeable接口的对象，例如：FileOutputStream、 InputStream 等实例。
 */
public class CloseCloseableObject {

    public static void close(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
