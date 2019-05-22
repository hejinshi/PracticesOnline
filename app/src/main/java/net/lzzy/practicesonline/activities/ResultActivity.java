package net.lzzy.practicesonline.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.fragment.ChartFragment;
import net.lzzy.practicesonline.fragment.GridFragment;
import net.lzzy.practicesonline.models.view.QuestionResult;

import java.util.List;

import javax.xml.transform.Result;

/**
 * Created by lzzy_gxy on 2019/5/13.
 * Description:
 */
public class ResultActivity extends BaseActivity implements GridFragment.OnGridListener,ChartFragment.OnChartBackListener{


    public static final String EXTRA_QUESTION_POS = "extra_question_pos";
    public static final String EXTRA_FAVORITE_FLAG = "favorite_flag";
    private List<QuestionResult> results;
    private String practiceId;
    private Button btn;
    private int questionPos;
    private FragmentPagerAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        practiceId = getIntent().getStringExtra(QuestionActivity.EXTRA_PRACTICE_ID);
        questionPos=getIntent().getIntExtra(QuestionActivity.EXTRA_QUESTION_POS,0);



    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_result;
    }

    @Override
    protected void populate() {

        btn = findViewById(R.id.activity_result_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment nowFragment=getManager().findFragmentById(R.id.activity_result_container);
                if (nowFragment instanceof GridFragment){
                    results = getIntent().getParcelableArrayListExtra(QuestionActivity.EXTRA_RESULT);
                    Fragment fragment=ChartFragment.newInstance(results,practiceId);
                    getManager().beginTransaction().replace(R.id.activity_result_container,fragment).commit();
                    btn.setText("图");
                }
                if (nowFragment instanceof ChartFragment){
                    Fragment fragment2=createFragment();
//                    getManager().beginTransaction().hi
                    getManager().beginTransaction().replace(R.id.activity_result_container,fragment2).commit();
                    btn.setText("表");
                }

            }
        });
        adapter=new FragmentPagerAdapter(getManager()) {
            @Override
            public Fragment getItem(int position) {
                return null;
            }

            @Override
            public int getCount() {
                return 0;
            }
        };
    }

    @Override
    protected int getContainerId() {
        return R.id.activity_result_container;
    }

    @Override
    protected Fragment createFragment() {
        results = getIntent().getParcelableArrayListExtra(QuestionActivity.EXTRA_RESULT);
        return GridFragment.newInstance(practiceId,results);
    }

    @Override
    public void returnPosition(int position) {
        Intent intent=new Intent();
        intent.putExtra(EXTRA_QUESTION_POS,position);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void onBack() {
        //onBackPressed();
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("返回到哪里")
                .setNeutralButton("返回题目",(dialog, which) -> {
                    Intent intent=new Intent();
                    intent.putExtra(EXTRA_QUESTION_POS,questionPos);
                    setResult(RESULT_OK,intent);
                    finish();
                    super.onBackPressed();
                })
                .setPositiveButton("查看收藏",(dialog, which) -> {
                    Intent intent=new Intent();
                    intent.putExtra(EXTRA_FAVORITE_FLAG,true);
                    setResult(RESULT_OK,intent);
                    finish();
                    super.onBackPressed();
                })
                .setNegativeButton("章节列表",(dialog, which) -> {
                    startActivity(new Intent(this,PracticesActivity.class));
                    finish();
                    super.onBackPressed();
                })
                .show();


    }
}
