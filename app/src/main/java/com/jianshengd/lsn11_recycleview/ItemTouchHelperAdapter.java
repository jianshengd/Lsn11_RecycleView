package com.jianshengd.lsn11_recycleview;

/**
 * Created by jianshengd on 2018/7/7
 */
public interface ItemTouchHelperAdapter {
    //数据交换
    void onItemMove(int fromPosition,int toPosition);
    //数据删除
    void onItemDissmiss(int position);
}
