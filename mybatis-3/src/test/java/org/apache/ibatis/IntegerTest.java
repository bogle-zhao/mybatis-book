package org.apache.ibatis;

import org.junit.Test;

public class IntegerTest {

    @Test
    public void test1() {

        System.out.println(Integer.highestOneBit(10));
        System.out.println(Integer.highestOneBit(16));
        System.out.println(Integer.highestOneBit(6));
    }
}
