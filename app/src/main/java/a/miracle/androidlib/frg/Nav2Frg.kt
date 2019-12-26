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
class Nav2Frg : BaseFrg() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tv_title.text = "Nav2Fag"
        bt_next.text = "goto Nav3Frg"
        bt_next.setOnClickListener{
            findNavController().navigate(Nav2FrgDirections.actionNav2FrgToNav3Frg())
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.frg_nav
    }
}