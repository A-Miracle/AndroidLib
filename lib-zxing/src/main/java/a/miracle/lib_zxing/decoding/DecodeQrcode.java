package a.miracle.lib_zxing.decoding;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.Hashtable;
import java.util.Vector;

/**
 * 解析二维码
 * @author c-tao.chen
 *
 */
public class DecodeQrcode {
	private static final String TAG = DecodeQrcode.class.getSimpleName();
	
	public final MultiFormatReader multiFormatReader;
	
	public DecodeQrcode() {
		multiFormatReader = new MultiFormatReader();

		// 解码的参数
		Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(2);
		
		// 可以解析的编码类型
		Vector<BarcodeFormat> decodeFormats = new Vector<BarcodeFormat>();
		if (decodeFormats == null || decodeFormats.isEmpty()) {
			decodeFormats = new Vector<BarcodeFormat>();
			
			// 这里设置可扫描的类型，我这里选择了都支持
			decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
			decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
			decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
		}
		hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

		// 设置继续的字符编码格式为UTF8
		// hints.put(DecodeHintType.CHARACTER_SET, "UTF8");

		// 设置解析配置参数
		multiFormatReader.setHints(hints);
	}
	
	/**
	 * 解码图片二维码
	 * @param bitmap
	 * @return
	 */
	public Result decode(Bitmap bitmap){
		long start = System.currentTimeMillis();

		// 开始对图像资源解码
		Result rawResult = null;
		BitmapLuminanceSource source = new BitmapLuminanceSource(bitmap);
		BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
		try {
			rawResult = multiFormatReader.decodeWithState(binaryBitmap);
		} catch (ReaderException re) {
			// continue
		} finally {
			multiFormatReader.reset();
		}

		if (rawResult != null) {
			long end = System.currentTimeMillis();
			Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
			//Bitmap: source.renderCroppedGreyscaleBitmap();
			return rawResult;
		} else {
			return rawResult;
		}
	}
}
