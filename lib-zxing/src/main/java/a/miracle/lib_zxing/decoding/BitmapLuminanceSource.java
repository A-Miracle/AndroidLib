package a.miracle.lib_zxing.decoding;

import android.graphics.Bitmap;

import com.google.zxing.LuminanceSource;

/**
 * BitmapLuminanceSource
 * @author c-tao.chen
 *
 */
public class BitmapLuminanceSource extends LuminanceSource {

	private byte mBitmapPixels[];

	protected BitmapLuminanceSource(Bitmap bitmap) {
		super(bitmap.getWidth(), bitmap.getHeight());

		// 首先，要取得该图片的像素数组内容
		int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
		mBitmapPixels = new byte[bitmap.getWidth() * bitmap.getHeight()];
		bitmap.getPixels(data, 0, getWidth(), 0, 0, getWidth(), getHeight());

		// 将int数组转换为byte数组，也就是取像素值中蓝色值部分作为辨析内容
		for (int i = 0; i < data.length; i++) {
			mBitmapPixels[i] = (byte) data[i];
		}
	}

	@Override
	public byte[] getMatrix() {
		// 返回我们生成好的像素数据
		return mBitmapPixels;
	}

	@Override
	public byte[] getRow(int y, byte[] row) {
		// 这里要得到制定行的像素数据
		System.arraycopy(mBitmapPixels, y * getWidth(), row, 0, getWidth());
		return row;
	}
}
