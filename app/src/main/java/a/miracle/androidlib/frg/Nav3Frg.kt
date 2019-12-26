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
class Nav3Frg : BaseFrg() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tv_title.text = "Nav3Fag"
        bt_next.text = "back Nav1Frg"
        bt_next.setOnClickListener{
            // 回到 Nav1Frg
            findNavController().popBackStack(R.id.nav1Frg,false)
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.frg_nav
    }
}