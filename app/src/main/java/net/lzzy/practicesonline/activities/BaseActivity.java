package net.lzzy.practicesonline.activities;

import android.os.Bundle;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import net.lzzy.practicesonline.R;
import net.lzzy.practicesonline.fragment.SplashFragment;
import net.lzzy.practicesonline.utils.AppUtils;

/**
 * Created by lzzy_gxy on 2019/4/11.
 * Description:
 */
public abstract class BaseActivity extends AppCompatActivity {

    private Fragment fragment;

    public FragmentManager getManager() {
        return manager;
    }

    private FragmentManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutRes());
        AppUtils.addActivity(this);

        //setFragment();
        manager = getSupportFragmentManager();
        fragment = manager.findFragmentById(getContainerId());

        if (fragment ==null){
            fragment =createFragment();
            manager.beginTransaction().add(getContainerId(), fragment).commit();
        }


        populate();

    }

    protected Fragment getFragment(){
        return fragment;
    }
    /**
     * 返回相应布局文件资源
     * @return
     */
    protected abstract int getLayoutRes();


    /**
     * 执行在视图创建后
     */
    protected abstract void populate();

    protected abstract int getContainerId();

    protected abstract Fragment createFragment();

    protected void setFragment(int resId){
        FragmentManager manager=getSupportFragmentManager();
        Fragment fragment=manager.findFragmentById(resId);
        if (fragment==null){

            manager.beginTransaction().add(resId,fragment).commit();
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
        AppUtils.setStopped(getLocalClassName());
    }
}
