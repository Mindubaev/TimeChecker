package com.example.time.checker;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NanoTimer {

    public static long getNano() {
        return System.nanoTime();
    }

}
