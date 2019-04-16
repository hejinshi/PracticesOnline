package net.lzzy.practicesonline.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.constants.ApiConstans;
import net.lzzy.practicesonline.fragment.SplashFragment;
import net.lzzy.practicesonline.utils.AbstractStaticHandler;
import net.lzzy.practicesonline.utils.AppUtils;
import net.lzzy.practicesonline.utils.ViewUtils;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Administrator
 */
public class SplashActivity extends BaseActivity implements SplashFragment.OnSplashFinishedListener{
    private int seconds=10;
    private boolean isCounting=false;
    private static final int WHAT_COUNTING=0;
    private static final int WHAT_COUNT_DONE=2;
    private static final int WHAT_EXCEPTION=1;
    private static final int WHAT_SERVER_OFF=3;
    private TextView tvDisplay;
    private boolean isServerOn=true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AppUtils.isNetworkAvailable()){
            new AlertDialog.Builder(this)
                    .setMessage("网络不可用")
                    .setPositiveButton("exit",(dialog, which) -> AppUtils.exit())
                    .setNegativeButton("ok",(dialog, which) -> gotoMain())
                    .show();
        }else {
            ThreadPoolExecutor executor=AppUtils.getExecutor();
            executor.execute(this::countDown);
            executor.execute(this::detectServerStatus);
        }
        tvDisplay=findViewById(R.id.activity_splash_count_down);
//        asyncCountDown();
//        tvDisplay.setOnClickListener(v -> {
//            cancelCount();
//        });
    }

    private void countDown(){
        while (seconds>=0){
            handler.sendMessage(handler.obtainMessage(WHAT_COUNTING,seconds));
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                handler.sendMessage(handler.obtainMessage(WHAT_EXCEPTION,e.getMessage()));
            }
            seconds--;
        }
        handler.sendEmptyMessage(WHAT_COUNT_DONE);
    }

    private void detectServerStatus(){
        try {
            AppUtils.tryConnectServer(ApiConstans.URL_API);
        } catch (IOException e) {
            isServerOn=false;
            handler.sendMessage(handler.obtainMessage(WHAT_SERVER_OFF,e.getMessage()));
//            e.printStackTrace();
        }
    }

    public void gotoMain() {
        startActivity(new Intent(this,PracticesActivity.class));
        finish();
    }



    private void asyncCountDown(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                isCounting=true;
                while(seconds>=0){
                    try{
                        Thread.sleep(1000);
                        seconds--;
                        Message msg=handler.obtainMessage();
                        msg.what=WHAT_COUNTING;
                        msg.arg1=seconds;
                        handler.sendMessage(msg);
                    }catch (InterruptedException e){
                        handler.sendMessage(handler.obtainMessage(WHAT_EXCEPTION,e.getMessage()));
                    }
                }
                handler.sendEmptyMessage(WHAT_COUNT_DONE);
            }
        }).start();
    }

    private CountHandler handler=new CountHandler(this);
    private static class CountHandler extends AbstractStaticHandler<SplashActivity> {
        CountHandler(SplashActivity context) {
            super(context);
        }

        @Override
        public void handleMessage(Message msg, SplashActivity activity) {

            //此处进行消息的处理

            switch (msg.what){
                case WHAT_COUNTING:
                    String text=msg.obj.toString()+"秒";
                    activity.tvDisplay.setText(text);
                    break;
                case WHAT_COUNT_DONE:
                    if (activity.isServerOn){
                        activity.gotoMain();
                    }

                    break;
                case WHAT_EXCEPTION:

                    new AlertDialog.Builder(activity)
                            .setMessage(msg.obj.toString())
                            .setPositiveButton("继续",(dialog, which) -> activity.gotoMain())
                            .setNegativeButton("退出",(dialog, which) -> AppUtils.exit())
                            .show();
                    break;
                case WHAT_SERVER_OFF:
                    Activity context=AppUtils.getRunningActivity();
                    new AlertDialog.Builder(context)
                            .setMessage("服务器没有响应，是否继续？\n"+msg.obj)
                            .setPositiveButton("确定",(dialog, which) ->{
                                if (context instanceof SplashActivity){
                                    ((SplashActivity) context).gotoMain();
                                }
                            } )
                            .setNegativeButton("退出",(dialog, which) -> AppUtils.exit())
                            .setNeutralButton("设置",(dialog, which) -> ViewUtils.gotoSetting(context))
                            .show();
                    break;
                default:
                    break;
            }
        }
    }
    @Override
    public void cancelCount() {
        seconds=0;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_splash;
    }

    @Override
    protected void populate() {

    }

    @Override
    protected int getContainerId() {
        return R.id.fragment_splash_container;
    }

    @Override
    protected Fragment createFragment() {

        return new SplashFragment();
    }


    //    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        new AlertDialog.Builder(this)
//                .setMessage("")
//                .setPositiveButton("",(dialog, which) -> System.exit(0))
//                .show();
//    }

}
