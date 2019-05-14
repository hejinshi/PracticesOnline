package net.lzzy.practicesonline.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.fragment.QuestionFragment;
import net.lzzy.practicesonline.models.Question;
import net.lzzy.practicesonline.models.QuestionFactory;
import net.lzzy.practicesonline.models.UserCookies;
import net.lzzy.practicesonline.models.view.PracticeResult;
import net.lzzy.practicesonline.models.view.QuestionResult;
import net.lzzy.practicesonline.network.PracticeService;
import net.lzzy.practicesonline.utils.AbstractStaticHandler;
import net.lzzy.practicesonline.utils.AppUtils;
import net.lzzy.practicesonline.utils.ViewUtils;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class QuestionActivity extends AppCompatActivity {
    public static final int SUCCEED = 0;
    public static final int FAILED = 1;
    public static final int RE = 2;
    public static final String EXTRA_RESULT = "extraResult";
    public static final String EXTRA_PRACTICE_ID = "extraPracticeId";
    public static final int REQUEST_CODE_RESULT = 0;
    private String practiceId;
    private int apiId;
    private List<Question> questions;
    private TextView textView;
    private TextView tvCommit;
    private TextView tvHint;
    private ViewPager pager;
    private boolean isCommitted=false;
    private FragmentStatePagerAdapter adapter;
    private int pos;
    private View[] dots;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_question);
        AppUtils.addActivity(this);
        retrieveData();
        initViews();
        initDots();
        setListeners();
        pos = UserCookies.getInstance().getCurrentQuestion(practiceId);
        //setButton(practiceId,pos);
        pager.setCurrentItem(pos);
        refreshDots(pos);
        UserCookies.getInstance().updateReadCount(questions.get(pos).getId().toString());

    }

    private void setListeners(){
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                refreshDots(position);
                UserCookies.getInstance().updateCurrentQuestion(practiceId,position);
                UserCookies.getInstance().updateReadCount(questions.get(position).getId().toString());
                //setButton(practiceId,position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tvCommit.setOnClickListener(v -> commitPractice());
        textView.setOnClickListener(v -> redirect());
    }



    private void redirect() {
        List<QuestionResult> results=UserCookies.getInstance().getResultFromCookies(questions);
        Intent intent=new Intent(this,ResultActivity.class);
        intent.putExtra(EXTRA_PRACTICE_ID,practiceId);
        intent.putParcelableArrayListExtra(EXTRA_RESULT,(ArrayList<? extends Parcelable>) results);
        startActivityForResult(intent, REQUEST_CODE_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initDots(){
        int count=questions.size();
        dots = new View[count];
        LinearLayout container=findViewById(R.id.activity_question_dots);
        container.removeAllViews();
        int px= ViewUtils.dp2px(16);
        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(px,px);
        px=ViewUtils.dp2px(5);
        params.setMargins(px,px,px,px);
        for (int i=0;i<count;i++){
            TextView tvDot=new TextView(this);
            tvDot.setLayoutParams(params);
            tvDot.setBackgroundResource(R.drawable.dot_style);
            tvDot.setTag(i);
            //
            tvDot.setOnClickListener(v -> pager.setCurrentItem((Integer) v.getTag()));
            container.addView(tvDot);
            dots[i]=tvDot;
        }

    }
    private void refreshDots(int pos){
        for (int i=0;i<dots.length;i++){
            int drawable=i==pos?R.drawable.dot_fill_style:R.drawable.dot_style;
            dots[i].setBackgroundResource(drawable);
        }

    }
    String info;
    private void commitPractice() {
        List<QuestionResult> results=UserCookies.getInstance().getResultFromCookies(questions);
        List<String> macs=AppUtils.getMacAddress();
        String[] items=new String[macs.size()];
        macs.toArray(items);
        info=items[0];
        new AlertDialog.Builder(this)
                .setTitle("")
                .setSingleChoiceItems(items,0,(dialog, which) -> info=items[which])
                .setNegativeButton("取消",null)
                .setPositiveButton("提交",(dialog, which) -> {
                    PracticeResult result=new PracticeResult(results,apiId,"何金石, "+info);
                    postResult(result);
                }).show();
    }
    private static class ResultHandler extends AbstractStaticHandler<QuestionActivity>{
        static final int RESPONSE_OK_MIN=200;
        static final int RESPONSE_OK_MAX=220;

        ResultHandler(QuestionActivity context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, QuestionActivity questionActivity) {
            ViewUtils.dismissProgress();
            if (msg.what==SUCCEED){
                int code=(int) msg.obj;
                if (code>=RESPONSE_OK_MIN&&code<=RESPONSE_OK_MAX){
                    questionActivity.isCommitted=true;
                    Toast.makeText(questionActivity, "succeed", Toast.LENGTH_SHORT).show();
                    UserCookies.getInstance().commitPractice(questionActivity.practiceId);
                    questionActivity.redirect();
                }else {
                    Toast.makeText(questionActivity, "failed", Toast.LENGTH_SHORT).show();
                }
            }else if (msg.what==FAILED){
                Toast.makeText(questionActivity, "failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ResultHandler resultHandler=new ResultHandler(this);

    private void postResult(PracticeResult result) {
        ViewUtils.showProgress(this,"正在提交成绩...");
        AppUtils.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try{
                    int code=PracticeService.postResult(result);
                    resultHandler.sendMessage(resultHandler.obtainMessage(SUCCEED,code));
                }catch (JSONException|IOException e){
                    e.printStackTrace();
                    resultHandler.sendMessage(resultHandler.obtainMessage(FAILED,e));
                }
            }
        });
    }




//    private CountHandler handler=new CountHandler(this);
//    @SuppressLint("HandlerLeak")
//    private class CountHandler extends AbstractStaticHandler<QuestionActivity>{
//
//        private CountHandler(QuestionActivity context) {
//            super(context);
//        }
//
//        @Override
//        public void handleMessage(Message msg, QuestionActivity questionActivity) {
//            switch (msg.what){
//                case SUCCEED:
//                    isCommitted=true;
//                    Toast.makeText(questionActivity, "succeed", Toast.LENGTH_SHORT).show();
//                    break;
//                case FAILED:
//                    Toast.makeText(questionActivity, "failed", Toast.LENGTH_SHORT).show();
//                    break;
//                case RE:
//                    Toast.makeText(questionActivity, "re", Toast.LENGTH_SHORT).show();
//                    break;
//                default:
//                    break;
//            }
//        }
//    }

//    static class ResultPost extends AsyncTask<PracticeResult,Void,Integer>{
//        PracticeResult result;
//        ResultPost(PracticeResult result){
//            this.result=result;
//        }
//
//        @Override
//        protected Integer doInBackground(PracticeResult... practiceResults) {
//
//            try {
//                return PracticeService.postResult(result);
//            } catch (JSONException|IOException e) {
//                e.printStackTrace();
//                return 0;
//
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Integer integer) {
//            super.onPostExecute(integer);
//            List<QuestionResult> questionResults=result.getResults();
//            QuestionFactory factory=QuestionFactory.getInstance();
//            if (integer>=200&&integer<=220){
//
//
//            }else {
//                return;
//            }
//        }
//    }

    private void initViews() {
        textView = findViewById(R.id.activity_question_tv_view);
        tvCommit = findViewById(R.id.activity_question_tv_commit);
        tvHint = findViewById(R.id.activity_question_tv_hint);
        pager = findViewById(R.id.activity_question_pager);
        if (isCommitted){
            tvCommit.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.VISIBLE);
        }else {
            textView.setVisibility(View.GONE);
            tvCommit.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.GONE);
        }
        adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Question question=questions.get(position);
                question.getId();
                return QuestionFragment.newInstance(question.getId().toString(),position,isCommitted);
            }

            @Override
            public int getCount() {
                return questions.size();
            }
        };
        pager.setAdapter(adapter);
    }

    private void retrieveData() {
        practiceId=getIntent().getStringExtra(PracticesActivity.EXTRA_PRACTICE_ID);
        apiId=getIntent().getIntExtra(PracticesActivity.EXTRA_API_ID,-1);
        questions= QuestionFactory.getInstance().getQuestionByPractice(practiceId);
        isCommitted=UserCookies.getInstance().isPracticeCommitted(practiceId);
        if (apiId<0||questions==null||questions.size()==0){
            Toast.makeText(this, "no questions", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppUtils.removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUtils.setRunning(getLocalClassName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppUtils.setRunning(getLocalClassName());
    }
}
