package net.lzzy.practicesonline.activities;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.fragment.PracticesFragment;
import net.lzzy.practicesonline.models.PracticeFactory;
import net.lzzy.practicesonline.models.Question;
import net.lzzy.practicesonline.network.DetectWebService;
import net.lzzy.practicesonline.utils.AppUtils;
import net.lzzy.practicesonline.utils.ViewUtils;

import java.util.UUID;

/**
 * Created by lzzy_gxy on 2019/4/16.
 * Description:
 */
public class PracticesActivity extends BaseActivity implements PracticesFragment.OnPracticesSelectedListener{
    public static final String EXTRA_API_ID="extraApiId";
    public static final String EXTRA_PRACTICE_ID="extraPracticeId";
    public static final String EXTRA_LOCAL_COUNT="extraLocalCount";
    private ServiceConnection connection;
    private boolean refresh=false;

    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private PendingIntent mResultIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SearchView searchView=findViewById(R.id.bar_title_sv);
        searchView.setQueryHint("请输入关键词搜索");
        searchView.setOnQueryTextListener(new ViewUtils.AbstractQueryListener() {
            @Override
            public void handleQuery(String kw) {
                ((PracticesFragment)getFragment()).search(kw);
            }
        });
        SearchView.SearchAutoComplete auto = searchView.findViewById(R.id.search_src_text);
        auto.setHintTextColor(Color.WHITE);
        auto.setTextColor(Color.WHITE);
        ImageView icon=findViewById(R.id.search_button);
        ImageView icX=searchView.findViewById(R.id.search_close_btn);
        ImageView icG=searchView.findViewById(R.id.search_go_btn);
        icon.setColorFilter(Color.WHITE);
        icG.setColorFilter(Color.WHITE);
        icX.setColorFilter(Color.WHITE);

        if (getIntent()!=null){
            refresh=getIntent().getBooleanExtra(DetectWebService.EXTRA_REFRESH,false);
        }



        connection=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DetectWebService.DetectWebBinder binder=(DetectWebService.DetectWebBinder) service;
                binder.detect();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        int localCount= PracticeFactory.getInstance().get().size();
        Intent intent=new Intent(this, DetectWebService.class);
        intent.putExtra(EXTRA_LOCAL_COUNT,localCount);
        bindService(intent,connection,BIND_AUTO_CREATE);


    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_practices;
    }

    @Override
    protected void populate() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (refresh){
            ((PracticesFragment)getFragment()).startRefresh();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        new AlertDialog.Builder(this)
                .setMessage("退出应用吗？")
                .setPositiveButton("退出",(dialog, which) -> AppUtils.exit())
                .setNegativeButton("取消", (dialog, which) -> {
                })
                .show();
    }

    @Override
    protected int getContainerId() {
        return R.id.activity_practices_container;
    }

    @Override
    protected Fragment createFragment() {
        return new PracticesFragment();
    }



    @Override
    public void onPracticesSelected(String practiceId, int apiId) {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotification = new NotificationCompat.Builder(this)
                // 设置小图标
                .setSmallIcon(R.drawable.ic_my)
                // 设置标题
                .setContentTitle("you have a meeting for"+apiId+"")
                // 设置内容
                .setContentText("you have a meeting at 3:00 this afternoon")
                .build();
        mNotificationManager.notify(0, mNotification);
        Intent intent=new Intent(this,QuestionActivity.class);
        intent.putExtra(EXTRA_API_ID,apiId);
        startActivity(intent);
    }
}
