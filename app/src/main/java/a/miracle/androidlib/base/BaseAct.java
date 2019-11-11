package a.miracle.androidlib.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import a.miracle.lib_utils.StatusBarUtil;

/**
 * Created by c.tao on 2019/11/8
 */
public abstract class BaseAct extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.immersive(this);
    }
}
