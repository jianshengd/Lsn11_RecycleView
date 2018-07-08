package com.jianshengd.lsn11_recycleview;

import android.app.Service;
import android.arch.lifecycle.ViewModelProvider;
import android.graphics.Color;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rv_role;
    private RoleAdapter roleAdapter;
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv_role = findViewById(R.id.rv_role);
            rv_role.setLayoutManager(new LinearLayoutManager(this));
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        list.add("e");
        list.add("f");
        list.add("g");
        list.add("h");
        list.add("i");
        roleAdapter = new RoleAdapter();
        rv_role.setAdapter(roleAdapter);
        ItemTouchHelper.Callback callback=new SimpleItemTouchHelperCallback(roleAdapter,this);
         itemTouchHelper=new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rv_role);
    }
    ItemTouchHelper itemTouchHelper;

    class RoleAdapter extends RecyclerView.Adapter<RoleViewHolder> implements ItemTouchHelperAdapter{

        @NonNull
        @Override
        public RoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item, parent, false);
            return new RoleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final  RoleViewHolder holder, int position) {
            holder.tv_name.setText(list.get(position));
//            holder.tv_name.setBackgroundColor(Color.RED|(0xff<<(position*8)));
            final int fposition=position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, list.get(fposition), Toast.LENGTH_SHORT).show();
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    //如果item不是最后一个，则执行拖拽
                    if (fposition!=list.size()-1) {
                        itemTouchHelper.startDrag(holder);
                    }
                    return true;
                }
            });


        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            //交换位置
//            Collections.swap(list,fromPosition,toPosition);
            if (fromPosition<toPosition){
                list.add(toPosition+1,list.get(fromPosition));
                list.remove(fromPosition);
            }else{
                list.add(toPosition,list.get(fromPosition));
                list.remove(fromPosition+1);
            }

            notifyItemMoved(fromPosition,toPosition);

//            new Handler().postDelayed(new Runnable(){
//                @Override
//                public void run() {
//                    notifyDataSetChanged();
//                }
//            }, 500);
        }

        @Override
        public void onItemDissmiss(int position) {
            //移除数据
            list.remove(position);
            notifyItemRemoved(position);

//            new Handler().postDelayed(new Runnable(){
//                @Override
//                public void run() {
//                    notifyDataSetChanged();
//                }
//            }, 500);

        }
    }

    public static class  RoleViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name;
        public ImageView iv_img;
        public TextView tv_text;
        public CardView cardview;
        RoleViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.item);
            tv_text = itemView.findViewById(R.id.tv_text);
            iv_img = itemView.findViewById(R.id.iv_img);
            cardview= itemView.findViewById(R.id.cardview);
        }

        public void setBackground(int color){
            cardview.setBackgroundColor(color);
        }
    }
}
