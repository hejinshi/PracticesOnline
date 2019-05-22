package net.lzzy.practicesonline.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.models.Question;
import net.lzzy.practicesonline.models.QuestionFactory;
import net.lzzy.practicesonline.models.UserCookies;
import net.lzzy.practicesonline.models.view.BarChartView;
import net.lzzy.practicesonline.models.view.LineChartView;
import net.lzzy.practicesonline.models.view.QuestionResult;
import net.lzzy.practicesonline.models.view.WrongType;
import net.lzzy.practicesonline.utils.AppUtils;
import net.lzzy.practicesonline.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzzy_gxy on 2019/5/13.
 * Description:
 */
public class ChartFragment extends BaseFragment{
    private static final String[] WRONG_TYPE= {WrongType.EXTRA_OPTIONS.toString(),
    WrongType.MISS_OPTIONS.toString(),WrongType.RIGHT_OPTIONS.toString(),
            WrongType.WRONG_OPTIONS.toString()};
    public static final String ARG_C_PRACTICE_ID = "arg_c_practiceId";
    public static final int MIN_DISTANCE = 100;
    private String practiceId;

    public static final String ARG_C_RESULT = "arg_c_result";
    private List<QuestionResult> results;
    private OnChartBackListener onChartBackListener;
    private int chartIndex=0;
    private View[] dot;
    private float touchX1=0;
    private Chart[] charts;


    public static ChartFragment newInstance(List<QuestionResult> result,String practiceId){
        ChartFragment fragment=new ChartFragment();
        Bundle args=new Bundle();
        args.putParcelableArrayList(ARG_C_RESULT,(ArrayList<? extends Parcelable>) result);
        args.putString(ARG_C_PRACTICE_ID,practiceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            results=getArguments().getParcelableArrayList(ARG_C_RESULT);
            practiceId=getArguments().getString(ARG_C_PRACTICE_ID);
        }


    }

    @Override
    protected void populate() {
        //BarChartView barChartView=find(R.id.fragment_chart_bar_chart);
        //barChartView.setHorizontalAxis(WRONG_TYPE);
        int ro=0,wo=0,mo=0,eo=0;
        for (int i=0;i<results.size();i++){
            switch (results.get(i).getType()){
                case MISS_OPTIONS:
                    mo=mo+1;
                    break;
                case EXTRA_OPTIONS:
                    eo=eo+1;
                    break;
                case RIGHT_OPTIONS:
                    ro=ro+1;
                    break;
                case WRONG_OPTIONS:
                    wo=wo+1;
                    break;
                default:
                    break;
            }
        }
        float[] DATA={eo,mo,ro,wo};
        float max=ro;
        for (float f:DATA){
            if (f>max){
                max=f;
            }
        }
        //barChartView.setDataList(DATA, (int) max);

        int[] lineData={eo,mo,ro,wo};
        //LineChartView lineChartView=find(R.id.fragment_chart_line_ch);
        //lineChartView.setHorizontalAxis(WRONG_TYPE);
        //lineChartView.setDataList(lineData,(int) max);


        int rightCount=0;
        for (QuestionResult qr:results){

            if (qr.isRight()){
                rightCount++;
            }
        }


        //barChartView.setVisibility(View.GONE);
        //lineChartView.setVisibility(View.GONE);

        int wrongCount=results.size()-rightCount;
        List<PieEntry> yVals = new ArrayList<>();
        //rightCount=rightCount/results.size();

        yVals.add(new PieEntry(rightCount,"正确"));
        yVals.add(new PieEntry(wrongCount,"错误"));

        List<Integer> colors = new ArrayList<>();
        colors.add(Color.parseColor("#4DB34D"));
        colors.add(Color.parseColor("#EE1169"));

        PieChart pieChart=find(R.id.fragment_chart_pie_chart);
        PieDataSet pieDataSet=new PieDataSet(yVals,"");
        pieDataSet.setColors(colors);
        PieData pieData=new PieData(pieDataSet);
        String descriptionStr = "正确及错误比例(单位%)";
        Description description = new Description();
        description.setText(descriptionStr);
        pieChart.setDescription(description);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setDragDecelerationEnabled(false);
        pieChart.setData(pieData);

        List<Question> questions=QuestionFactory.getInstance().getQuestionByPractice(practiceId);
        int[] questionReadCount=new int[questions.size()];
        for (int i=0;i<questions.size();i++){
            questionReadCount[i]= UserCookies.getInstance().getReadCount(questions.get(i).getId().toString());
        }

        LineChart lineChart2=find(R.id.fragment_chart_line_ch_2);
        List<Entry> lineVals=new ArrayList<>();
        for (int i=0;i<questions.size();i++){
            lineVals.add(new Entry(i,questionReadCount[i]));
        }
        LineDataSet lineDataSet=new LineDataSet(lineVals,"");
        LineData lineData1=new LineData(lineDataSet);
        lineChart2.setData(lineData1);
        String s2="题目阅读次数";
        Description description1=new Description();
        description1.setText(s2);
        lineChart2.setDescription(description1);
        YAxis yAxisR=lineChart2.getAxisRight();
        YAxis yAxisL=lineChart2.getAxisLeft();
        yAxisL.setEnabled(false);
        yAxisR.setEnabled(false);
        XAxis xAxisLine=lineChart2.getXAxis();
        xAxisLine.setLabelCount(results.size(), true);
        String[] st=new String[results.size()];
        for (int i=0;i<results.size();i++){
            st[i]="Q."+(i+1);
        }
        xAxisLine.setValueFormatter(new IndexAxisValueFormatter(st));
        xAxisLine.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisLine.setGranularity(1);
        xAxisLine.setDrawGridLines(false);
        xAxisLine.setDrawLabels(true);
        xAxisLine.setDrawAxisLine(true);
        lineChart2.setDragEnabled(false);

        BarChart barChart2=find(R.id.fragment_chart_bar_chart2);
        List<BarEntry> barVals=new ArrayList<>();
        for (int i=0;i<DATA.length;i++){
            barVals.add(new BarEntry(i,DATA[i]));
        }
        BarDataSet barDataSet=new BarDataSet(barVals,"");
        BarData barData=new BarData(barDataSet);
        String s3="错误类型数量";
        Description description2=new Description();
        description2.setText(s3);
        XAxis xAxisBar=barChart2.getXAxis();
        xAxisBar.setValueFormatter(new IndexAxisValueFormatter(WRONG_TYPE));
        xAxisBar.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisBar.setDrawGridLines(false);
        xAxisBar.setGranularity(1f);
        barChart2.setDescription(description2);
        barChart2.setData(barData);

        View dot1=find(R.id.fragment_chart_dot_1);
        View dot2=find(R.id.fragment_chart_dot_2);
        View dot3=find(R.id.fragment_chart_dot_3);
        dot = new View[]{dot1,dot2,dot3};

//        List<Chart> charts=new ArrayList<>();
//        charts.add(0,pieChart);
//        charts.add(1,lineChart2);
//        charts.add(2,barChart2);
        charts = new Chart[]{pieChart,lineChart2,barChart2};

        find(R.id.fragment_chart_container).setOnTouchListener(new ViewUtils.AbstractTouchListener() {
            @Override
            public boolean handleTouch(MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    touchX1=event.getX();
                }
                if (event.getAction()==MotionEvent.ACTION_UP) {
                    float touchX2 = event.getX();
                    if (Math.abs(touchX2 - touchX1) > MIN_DISTANCE) {
                        if (touchX2 < touchX1) {
                            if (chartIndex < charts.length - 1) {
                                chartIndex++;
                            } else {
                                chartIndex = 0;
                            }
                        } else {
                            if (chartIndex > 0) {
                                chartIndex--;
                            } else {
                                chartIndex = charts.length - 1;
                            }
                        }
                        switchChart();
                    }

                }
                return true;
            }
        });

    }

    private void switchChart() {
        for (int i=0;i<charts.length;i++){
            if (chartIndex==i){
                charts[i].setVisibility(View.VISIBLE);
                dot[i].setBackgroundResource(R.drawable.dot_fill_style);
            }else {
                charts[i].setVisibility(View.GONE);
                dot[i].setBackgroundResource(R.drawable.dot_style);
            }
        }

    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_chart;
    }

    @Override
    public void search(String kw) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            onChartBackListener=(OnChartBackListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+"必须实现OnChartBackListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onChartBackListener=null;
    }

    public interface OnChartBackListener{
        void onBack();
    }
}
