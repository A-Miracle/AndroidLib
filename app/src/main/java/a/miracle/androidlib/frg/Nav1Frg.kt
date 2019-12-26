package a.miracle.androidlib.frg

import a.miracle.androidlib.R
import a.miracle.androidlib.base.BaseFrg
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.frg_nav.*

/**
 * Created by c.tao on 2019/12/26
 */
class Nav1Frg : BaseFrg() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tv_title.text = "Nav1Fag"
        bt_next.text = "goto Nav2Frg"
        bt_next.setOnClickListener{
            findNavController().navigate(Nav1FrgDirections.actionNav1FrgToNav2Frg())
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.frg_nav
    }
}