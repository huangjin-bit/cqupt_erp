package com.cqupt.mislab.erpn.commons.utils;

import com.cqupt.mislab.erpn.model.VerifyCode;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @program: com.cqupt.mislab.erpn.commons.utils
 * @description:
 * @author: keji0
 * @date: 2020/7/20 10:02
 */
public interface IVerifyCodeGen {


    /**
     * @param width
     * @param height
     * @param os
     * @return
     * @throws IOException
     */
    String generate(int width, int height, OutputStream os) throws IOException;

    /**
     * @param width
     * @param height
     * @return
     * @throws IOException
     */
    VerifyCode generate(int width, int height) throws IOException;
}
