package com.jianshengd.lsn11_recycleview;

import android.app.Service;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

/**
 * Created by jianshengd on 2018/7/7
 */
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    //限制ImageView长度所能增加的最大值
    private double ICON_MAX_SIZE = 100;
    //ImageView的初始长宽
    private int fixedWidth = 150;

    private ItemTouchHelperAdapter mAdapter;
    private Context mContext;
    SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter,Context context) {
        mAdapter = adapter;
        mContext=context;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN
                    | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;        //允许上下左右的拖动
            return makeMovementFlags(dragFlags, 0);

        } else {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;        //允许上下的拖动
            int swipeFlags = ItemTouchHelper.LEFT;   //只允许从右向左侧滑
            return makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    /**
     * 获取删除方块的宽度
     */
    public int getSlideLimitation(RecyclerView.ViewHolder viewHolder) {
        ViewGroup viewGroup = (ViewGroup) viewHolder.itemView;
        return viewGroup.getChildAt(1).getLayoutParams().width;
    }
    /**
     * 针对swipe和drag状态，整个过程中一直会调用这个函数,
     * 随手指移动的view就是在super里面做到的(和ItemDecoration里面的onDraw()函数对应)
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//仅对侧滑状态下的效果做出改变
        Log.e("js========", dX + "");

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            //如果dX小于等于删除方块的宽度，那么我们把该方块滑出来
            if (Math.abs(dX) <= getSlideLimitation(viewHolder)) {
                viewHolder.itemView.scrollTo(-(int) dX, 0);
            }
            //如果dX还未达到能删除的距离，此时慢慢增加“眼睛”的大小，增加的最大值为ICON_MAX_SIZE
            else if (Math.abs(dX) <= recyclerView.getWidth() / 2) {
                double distance = (recyclerView.getWidth() / 2 - getSlideLimitation(viewHolder));
                double factor = ICON_MAX_SIZE / distance;
                double diff = (Math.abs(dX) - getSlideLimitation(viewHolder)) * factor;
                if (diff >= ICON_MAX_SIZE) {
                    diff = ICON_MAX_SIZE;
                }
                ((MainActivity.RoleViewHolder) viewHolder).tv_text.setText("");   //把文字去掉
                ((MainActivity.RoleViewHolder) viewHolder).iv_img.setVisibility(View.VISIBLE);  //显示眼睛
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ((MainActivity.RoleViewHolder) viewHolder).iv_img.getLayoutParams();
                params.width = (int) (fixedWidth + diff);
                params.height = (int) (fixedWidth + diff);
                ((MainActivity.RoleViewHolder) viewHolder).iv_img.setLayoutParams(params);
            }
        } else {
            //拖拽状态下不做改变，需要调用父类的方法
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    /**
     * 针对swipe和drag状态，整个过程中一直会调用这个函数(和ItemDecoration里面的onDrawOver()函数对应)
     * 这个函数提供给我们可以在RecyclerView的上面再绘制一层东西，比如绘制一层蒙层啥的
     */
    @Override
    public void onChildDrawOver(Canvas c,
                                RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder,
                                float dX,
                                float dY,
                                int actionState,
                                boolean isCurrentlyActive) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
    /**
     * 针对swipe和drag状态，当手指离开之后，view回到指定位置动画的持续时间(swipe可能是回到原位，也有可能是swipe掉)
     */
    @Override
    public long getAnimationDuration(RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
        return super.getAnimationDuration(recyclerView, animationType, animateDx, animateDy);
    }

    /**
     * 针对swipe和drag状态，当一个item view在swipe、drag状态结束的时候调用
     * drag状态：当手指释放的时候会调用
     * swipe状态：当item从RecyclerView中删除的时候调用，一般我们会在onSwiped()函数里面删除掉指定的item view
     */
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        Log.e("js========", "clearView");


        ((MainActivity.RoleViewHolder) viewHolder).setBackground(Color.WHITE);

        //重置改变，防止由于复用而导致的显示问题
        viewHolder.itemView.setScrollX(0);
        ((MainActivity.RoleViewHolder) viewHolder).tv_text.setText("左滑删除");
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ((MainActivity.RoleViewHolder) viewHolder).iv_img.getLayoutParams();
        params.width = 150;
        params.height = 150;
        ((MainActivity.RoleViewHolder) viewHolder).iv_img.setLayoutParams(params);
        ((MainActivity.RoleViewHolder) viewHolder).iv_img.setVisibility(View.INVISIBLE);

//        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            ((MainActivity.RoleViewHolder) viewHolder).setBackground(Color.LTGRAY);
            //选中时处理
            //获取系统震动服务
            Vibrator vib = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
            //震动70毫秒
            vib.vibrate(60);
        }
        super.onSelectedChanged(viewHolder, actionState);

    }
    //TODO ====================================================================

    // TODO =====================drag 上下拖拉处理===============================
    /**
     * 针对drag状态，在canDropOver()函数返回true的情况下，
     * 会调用该函数让我们去处理拖动换位置的逻辑(需要重写自己处理变换位置的逻辑)
     * 如果有位置变换返回true，否则发挥false
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //onItemMove是接口方法
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }
    /**
     * 针对drag状态,当item长按的时候是否允许进入drag(拖动)状态
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }
    /**
     * 针对drag状态，当drag itemView和底下的itemView重叠的时候,
     * 可以给drag itemView设置额外的margin，让重叠更加容易发生。
     * 相当于增大了drag itemView的区域
     */
    @Override
    public int getBoundingBoxMargin() {
        return 0;
    }
    /**
     * 针对drag状态，滑动超过百分之多少的距离可以可以调用onMove()函数
     * (注意哦，这里指的是onMove()函数的调用，并不是随手指移动的那个view哦)
     */
    @Override
    public float getMoveThreshold(RecyclerView.ViewHolder viewHolder) {
        return .5f;
    }

    /**
     * 针对drag状态，在drag的过程中获取drag itemView底下对应的ViewHolder(一般不用我们处理直接super就好了)
     */
    @Override
    public RecyclerView.ViewHolder chooseDropTarget(RecyclerView.ViewHolder selected,
                                                    List<RecyclerView.ViewHolder> dropTargets,
                                                    int curX,
                                                    int curY) {
        Log.e("------selected------",((MainActivity.RoleViewHolder)selected).tv_name.getText().toString());
        for (RecyclerView.ViewHolder dropTarget : dropTargets) {
            Log.e("------dropTarget------",((MainActivity.RoleViewHolder)dropTarget).tv_name.getText().toString());
        }
        return super.chooseDropTarget(selected, dropTargets, curX, curY);
    }
    /**
     * 针对drag状态，当itemView滑动到RecyclerView边界的时候(比如下面边界的时候),RecyclerView会scroll，
     * 同时会调用该函数去获取scroller距离(不用我们处理 直接super)
     */
    @Override
    public int interpolateOutOfBoundsScroll(RecyclerView recyclerView,
                                            int viewSize,
                                            int viewSizeOutOfBounds,
                                            int totalSize,
                                            long msSinceStartScroll) {
        return super.interpolateOutOfBoundsScroll(recyclerView, viewSize, viewSizeOutOfBounds, totalSize, msSinceStartScroll);
    }
    // TODO =====================swipe 左右滑动处理===============================
    /**
     * 针对swipe状态，swipe 到达滑动消失的距离回调函数,一般在这个函数里面处理删除item的逻辑
     * 确切的来讲是swipe item滑出屏幕动画结束的时候调用
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //onItemDissmiss是接口方法
        mAdapter.onItemDissmiss(viewHolder.getAdapterPosition());
    }


    /**
     *针对swipe状态,当item长按的时候是否允许进入swipe(滑动)状态
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }


    /**
     * 针对swipe状态，swipe滑动的位置超过了百分之多少就消失
     */
    @Override
    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
        return 0.5f;
    }
    /**
     * 针对swipe状态，swipe的逃逸速度，换句话说就算没达到getSwipeThreshold设置的距离，
     * 达到了这个逃逸速度item也会被swipe消失掉
     */
    @Override
    public float getSwipeEscapeVelocity(float defaultValue) {
        return defaultValue;
    }



}
