package com.hxr.springrediskafka.util;

import java.util.Random;

public class CommonUtil {

    public static Boolean generateFailCase() {
        Random failCase = new Random();
        return failCase.nextInt(3) > 1;
    }
}
