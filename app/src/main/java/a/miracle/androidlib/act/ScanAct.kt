package a.miracle.androidlib.act

import a.miracle.androidlib.R
import a.miracle.androidlib.utils.ToastUtils
import a.miracle.lib_utils.StatusBarUtil
import a.miracle.lib_zxing.CaptureAct
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.ClipboardManager
import androidx.appcompat.app.AlertDialog
import com.blankj.utilcode.util.StringUtils
import kotlinx.android.synthetic.main.act_scan.*
import kotlinx.android.synthetic.main.v_toolbar.*


/**
 * Created by c.tao on 2019/12/13
 */
class ScanAct : CaptureAct() {
    override fun getLayoutId(): Int {
        return R.layout.act_scan
    }

    override fun initStatusBar() {
        //设置状态栏透明
        StatusBarUtil.immersive(this)
    }

    override fun onAfterSetContentLayout(savedInstanceState: Bundle?) {
        iv_back.setOnClickListener { finish() }
        tv_title.text = "Scan"

        ib_scan.isSelected = true
        ib_hot.setOnClickListener{view->
            allUnSelected()
            view.isSelected = true
            stopScan()

            tv_title.text = "Hot"
        }
        ib_scan.setOnClickListener{view->
            allUnSelected()
            requestMail()
            view.isSelected = true
            startScan()

            tv_title.text = "Scan"
        }
        ib_order.setOnClickListener{view->
            allUnSelected()
            view.isSelected = true
            stopScan()

            tv_title.text = "Order"
        }
    }

    private fun allUnSelected() {
        ib_hot.isSelected = false
        ib_scan.isSelected = false
        ib_order.isSelected = false
    }

    override fun disposeResult(resultString: String) {
        if (StringUtils.isEmpty(resultString)) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("二维码信息错误!")
            builder.setPositiveButton(
                "继续"
            ) { _, _ -> continuePreview() }
            builder.setNegativeButton(
                "取消"
            ) { _, _ -> finish() }
            builder.show()
            return
        }
        if (resultString.startsWith("http://") || resultString.startsWith("https://")) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(resultString)
            builder.setPositiveButton("复制") { _, _ ->
                val cbm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cbm.text = resultString
                ToastUtils.show("复制成功")
                finish()
            }
            builder.setNeutralButton("打开") { _, _ ->
                val uri = Uri.parse(resultString)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
                finish()
            }
            builder.setNegativeButton(
                "取消"
            ) { _, _ -> finish() }
            builder.show()
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(resultString)
            builder.setPositiveButton("复制") { _, _ ->
                val cbm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cbm.text = resultString
                ToastUtils.show("复制成功")
                finish()
            }
            builder.setNegativeButton(
                "取消"
            ) { _, _ -> finish() }
            builder.show()
        }
    }
}