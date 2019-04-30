package com.su.multisliderdemo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.su.multisliderlib.MultiSliderLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    MultiSliderLayout multiSliderLayout;
    List<View> topViews;
    List<View> bottomViews;
    List<View> leftViews;
    List<View> rightViews;
    View mainView;

    RecyclerView leftRecycler,rightRecycler,topRecycler,bottomRecycler;
    List<String> datas=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            topViews = new ArrayList<>();
            bottomViews = new ArrayList<>();
            leftViews = new ArrayList<>();
            rightViews = new ArrayList<>();


            RelativeLayout layout1= (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.layout_recycler,null);
            leftRecycler=layout1.findViewById(R.id.recycler);
            RelativeLayout layout2= (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.layout_recycler,null);
            rightRecycler=layout2.findViewById(R.id.recycler);
            RelativeLayout layout3= (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.layout_recycler,null);
            topRecycler=layout3.findViewById(R.id.recycler);
            RelativeLayout layout4= (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.layout_recycler,null);
            bottomRecycler=layout4.findViewById(R.id.recycler);

            for(int i=0;i<30;i++){
                datas.add("item"+i);
            }
            Adapter adapter=new Adapter(this,datas);
            leftRecycler.setAdapter(adapter);
            rightRecycler.setAdapter(adapter);
            topRecycler.setAdapter(adapter);
            bottomRecycler.setAdapter(adapter);

            leftRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
            rightRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
            topRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            bottomRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

            leftRecycler.addItemDecoration(new Decoration(10));
            rightRecycler.addItemDecoration(new Decoration(10));
            topRecycler.addItemDecoration(new Decoration(10));
            bottomRecycler.addItemDecoration(new Decoration(10));

            leftViews.add(layout1);
            rightViews.add(layout2);
            topViews.add(layout3);
            bottomViews.add(layout4);

            mainView=new TextView(this);
            ((TextView) mainView).setText("主");
            ((TextView) mainView).setTextSize(30);
            ((TextView) mainView).setTextColor(Color.BLACK);
            mainView.setBackgroundColor(Color.GRAY);
            ((TextView) mainView).setGravity(Gravity.CENTER);

            multiSliderLayout = findViewById(R.id.multisliderlayout);
            multiSliderLayout.setFragments(topViews, bottomViews, leftViews, rightViews, mainView);
            multiSliderLayout.setRatio(new MultiSliderLayout.Ratio(
                    MultiSliderLayout.Ratio.WRAP_CONTENT,
                    MultiSliderLayout.Ratio.WRAP_CONTENT,
                    MultiSliderLayout.Ratio.WRAP_CONTENT,
                    MultiSliderLayout.Ratio.WRAP_CONTENT
            ));
            multiSliderLayout.setMultiSliderListener(new MultiSliderLayout.MultiSliderListener() {
                @Override
                public void onStateChange(MultiSliderLayout.ScrollState state) {
//                    if(state==MultiSliderLayout.ScrollState.UnScrolling){
//                        System.out.println("滚动停止");
//                    }else if(state==MultiSliderLayout.ScrollState.Scrolling){
//                        System.out.println("开始滚动");
//                    }
                }

                @Override
                public void scrollProgress(float progress) {
//                    System.out.println("滚动进度:"+progress);
                }

                @Override
                public void onMenuSelected(MultiSliderLayout.MenuType type, int index) {
//                    if(type==MultiSliderLayout.MenuType.Left){
//                        System.out.println("选择了左边页面第"+index+"页");
//                    }else if(type==MultiSliderLayout.MenuType.Right){
//                        System.out.println("选择了右边页面第"+index+"页");
//                    }else if(type==MultiSliderLayout.MenuType.Top){
//                        System.out.println("选择了顶部页面第"+index+"页");
//                    }else if(type==MultiSliderLayout.MenuType.Bottom){
//                        System.out.println("选择了底部页面第"+index+"页");
//                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
