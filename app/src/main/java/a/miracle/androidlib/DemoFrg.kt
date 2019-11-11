package a.miracle.androidlib

import android.os.Bundle
import android.view.View

import a.miracle.androidlib.base.BaseFrg

/**
 * Created by c.tao on 2019/11/11
 */
class DemoFrg : BaseFrg() {
    companion object {
        val ARG_LAYOUT_ID = "ARG_LAYOUT_ID"
    }

    override fun getLayoutId(): Int {
        arguments?.let {
            return it.getInt(ARG_LAYOUT_ID)
        }
        return 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}
