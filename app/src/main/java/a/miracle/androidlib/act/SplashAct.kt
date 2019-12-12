package a.miracle.androidlib.act

import a.miracle.androidlib.R
import android.os.Bundle

import a.miracle.androidlib.base.BaseAct
import android.content.Intent
import kotlinx.android.synthetic.main.act_splash.*

/**
 * Created by c.tao on 2019/11/11
 */
class SplashAct : BaseAct() {
    override fun getLayoutId(): Int {
        return R.layout.act_splash
    }

    override fun initStatusBar() {
    }

    override fun onAfterSetContentLayout(savedInstanceState: Bundle?) {
        iv_icon.postDelayed({
            startActivity(Intent(this, MainAct::class.java))
            finish()
        }, 500)
    }
}
