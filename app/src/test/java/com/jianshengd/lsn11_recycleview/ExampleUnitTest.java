package com.jianshengd.lsn11_recycleview;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        List<Integer> list=new ArrayList<>();
        list.add(0);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        //1插入到3之后
        int first=list.get(1);;
        for (int i=1;i<=3;i++){
            if (i!=3){
                list.set(i,list.get(i+1));
            }else{
                list.set(3,first);
            }
        }
        for (Integer integer : list) {
            System.out.println(integer);
        }
    }
}