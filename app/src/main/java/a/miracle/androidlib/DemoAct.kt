package a.miracle.androidlib

import a.miracle.androidlib.base.BaseAct
import a.miracle.androidlib.base.BaseFrg
import android.os.Bundle
import kotlinx.android.synthetic.main.v_toolbar.*

/**
 * Created by c.tao on 2019/11/8
 */
class DemoAct : BaseAct() {
    companion object {
        val EXTRA_TITLE = "EXTRA_TITLE"
        val EXTRA_LAYOUT_ID = "EXTRA_LAYOUT_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_demo)

        tv_title.text = intent.getStringExtra(EXTRA_TITLE)

        val layoutId = intent.getIntExtra(EXTRA_LAYOUT_ID, 0)

        val demoFrg = DemoFrg()
        val arguments = Bundle()
        arguments.putInt(DemoFrg.ARG_LAYOUT_ID, layoutId)
        demoFrg.arguments = arguments

        supportFragmentManager.beginTransaction().replace(R.id.fl_content, demoFrg).commit()

        iv_back.setOnClickListener { finish() }
    }
}
