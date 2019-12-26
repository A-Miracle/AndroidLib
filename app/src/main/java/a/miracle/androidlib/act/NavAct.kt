package a.miracle.androidlib.act

import a.miracle.androidlib.R
import a.miracle.androidlib.base.BaseAct
import a.miracle.androidlib.widget.nav.FragmentNavigator
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.act_nav.*
import kotlinx.android.synthetic.main.v_toolbar.*

/**
 * Created by c.tao on 2019/12/26
 */
class NavAct : BaseAct() {

    lateinit var fragmentNavigator: FragmentNavigator
    lateinit var navController: NavController

    override fun getLayoutId(): Int {
        return R.layout.act_nav
    }

    override fun onAfterSetContentLayout(savedInstanceState: Bundle?) {
        iv_back.setOnClickListener { finish() }
        tv_title.text = "NavAct"
        navController = findNavController(R.id.my_nav_fragment)

        if(navController is FragmentNavigator){
            Log.i("Nav", "FragmentNavigator")
        }

        ib_hot.setOnClickListener {
            navController.navigate(R.id.nav1Frg)
        }
        ib_scan.setOnClickListener {
            navController.navigate(R.id.nav2Frg)
        }
        ib_order.setOnClickListener {
            navController.navigate(R.id.nav3Frg)
        }

        fragmentNavigator = navController.navigatorProvider.getNavigator(FragmentNavigator::class.java)
    }

    override fun onBackPressed() {
        Log.i("Nav", fragmentNavigator.currentFragment.tag)
        if (!navController.navigateUp()) super.onBackPressed()
    }
}