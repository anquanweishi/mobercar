package com.xtrd.obdcar.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.view.View;

import com.xtrd.obdcar.config.ApiConfig;
import com.xtrd.obdcar.config.ParamsKey;
import com.xtrd.obdcar.utils.log.LogUtils;

public class ImageUtils {

	protected static final String TAG = "ImageUtils";
	private static final int Width = 216,Hight = 360;
	private static final int Limite_Size = 50*1024;
	
	public static void displayBranchImg(Context context,View img,int branchId) {
		FinalBitmap fb = FinalBitmap.create(context);
		fb.display(img, ApiConfig.getRequestUrl(ApiConfig.Car_Logo_Url)+"?"+ParamsKey.Car_Branch+"="+branchId);
	}
	public static void displayImg(Context context,View img,String url) {
		FinalBitmap fb = FinalBitmap.create(context);
		fb.display(img, url);
	}

	/*public static void uploadImg(String vehicleId,String filepath) {
		FinalHttp fh = new FinalHttp();
		fh.addHeader("Accept-Encoding", "GZIP");
		MultipartEntity entity = null;
		try {
			File file = new File(filepath);
			FileBody fileBody = new FileBody(file);
			FormBodyPart formBodyPart = new FormBodyPart(URLEncoder.encode(filepath), fileBody);
			entity.addPart(formBodyPart);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		try {
			FinalHttp fh = new FinalHttp();
			fh.addHeader("Accept-Encoding", "GZIP");
			AjaxParams params = new AjaxParams();
			params.put(ParamsKey.VEHICLEID, vehicleId);
			params.put(ParamsKey.Picture, new File(filepath));
//			params.put(ParamsKey.Picture, bitmapToByte(filepath));
			//fh.post(ApiConfig.getRequestUrl(ApiConfig.Img_Upload_Url), params, new AjaxCallBack<String>() {
			fh.post("http://192.168.1.101:8085/obd-server/p/picture/save", params, new AjaxCallBack<String>() {

				@Override
				public void onFailure(Throwable t, int errorNo, String strMsg) {
					super.onFailure(t, errorNo, strMsg);
					LogUtils.e(TAG, "errorNo " +strMsg + " "+errorNo);
				}

				@Override
				public void onStart() {
					LogUtils.e(TAG, "start ");
					super.onStart();
				}

				@Override
				public void onSuccess(String t) {
					LogUtils.e(TAG, "t " +t);
					super.onSuccess(t);
				}

			});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
*/
	private static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while ( baos.toByteArray().length> Limite_Size) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩        
			baos.reset();//重置baos即清空baos
			options -= 10;//每次都减少10
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中

		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
		return BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片;
	}

	public static Bitmap getScaleImage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		//现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = Hight;//这里设置高度为800f
		float ww = Width;//这里设置宽度为480f
		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;//be=1表示不缩放
		if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//设置缩放比例
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
	}

	private Bitmap comp(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();        
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出    
			baos.reset();//重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		//开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		//现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;//这里设置高度为800f
		float ww = 480f;//这里设置宽度为480f
		//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;//be=1表示不缩放
		if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;//设置缩放比例
		newOpts.inPreferredConfig = Config.RGB_565;//降低图片从ARGB888到RGB565
		//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
	}

}
