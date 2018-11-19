package dac.zjms.com.compass;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

/**
 * 作者：wanghaowen on 2018/11/19 15:07
 * 邮箱：13409909996@163.com
 * >_<
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
