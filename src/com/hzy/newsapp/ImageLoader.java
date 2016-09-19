package com.hzy.newsapp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

public class ImageLoader {
	private ImageView mimageView;
	private String mUrl;
	private ListView mListView;
	private Set<NewsAsyncTask> mTask;
//	����Caches
	private LruCache<String, Bitmap> mCaches;
	public ImageLoader(ListView listView){
		mListView=listView;
		mTask=new HashSet<ImageLoader.NewsAsyncTask>();
//		��ȡ�������ڴ�
		int maxMemory=(int) Runtime.getRuntime().maxMemory();
		int cacheSize=maxMemory/4;
		mCaches=new LruCache<String, Bitmap>(cacheSize){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// TODO Auto-generated method stub
//				��ÿ�δ��뻺���ʱ�����
				return value.getByteCount();
			}
			
		};
		
	}
// ���ӵ�����
	public void addBitmapToCache(String url,Bitmap bitmap){
		if(getBitmapFromCache(url)==null){
			mCaches.put(url, bitmap);
		}
	}
//	c�ӻ����л�ȡ����
	public Bitmap getBitmapFromCache(String url){
		return mCaches.get(url);
		}
	
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if(mimageView.getTag().equals(mUrl)){
			mimageView.setImageBitmap((Bitmap) msg.obj);
			}
		}

	};
	// ͨ�����̵߳ķ�ʽ����ͼƬ
	

	public void showImageByThread(ImageView img, final String url) {
		mimageView = img;
		mUrl=url;
		Log.d("URL",url);
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				Bitmap bitmap = getBitmapFromUrl(url);
				Message message = Message.obtain();
				message.obj = bitmap;
				handler.sendMessage(message);
			}
		}.start();
	}

	@SuppressLint("DefaultLocale")
	public Bitmap getBitmapFromUrl(String urlString) {
		Bitmap bitmap = null;
		InputStream is = null;
		
			
			try {
				
				URL url = new URL(urlString);
				HttpURLConnection connection= (HttpURLConnection) url.openConnection();
				is = new BufferedInputStream(connection.getInputStream());
				bitmap = BitmapFactory.decodeStream(is);
				connection.disconnect();
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return bitmap;
			
		
			// TODO: handle exception
			

	}
//	ͨ��AsycTask��ʽ����
	
	private class NewsAsyncTask extends AsyncTask<String,Void,Bitmap >{
//		private ImageView mImageView;
		private String mUrl;
		public NewsAsyncTask(String url){
//			mImageView=imageView;
			mUrl=url;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url=params[0];
			Log.d("�����ϻ�ȡͼƬ�ĵ�ַ",url );
//			�������ϻ�ȡͼƬ
			Bitmap bitmap=getBitmapFromUrl(params[0]);
			if(bitmap!=null){
//				�����ڻ����ͼƬ���뻺�棬ʵ�ֻ���Ч��
				addBitmapToCache(url, bitmap);
			}
			return bitmap;
		}
		
	@Override
	protected void onPostExecute(Bitmap result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		ImageView imageView=(ImageView) mListView.findViewWithTag(mUrl);
		if(imageView!=null&&result!=null){
			imageView.setImageBitmap(result);
		}
		mTask.remove(this);

		
	}
		
	}
	public void showImageByAsyncTask(ImageView imageView, String url) {
		// TODO Auto-generated method stub
//		�ӻ���ȡ����Ӧ��ͼƬ
		Bitmap bitmap=getBitmapFromCache(url);
//		���������ֱ���ó���ʹ�ã����û��ȥ����������
		if(bitmap==null){
			imageView.setImageResource(R.drawable.ic_launcher);
			
		}else{
			imageView.setImageBitmap(bitmap);
		}
		
	}

	
	public void cancelAllTasks() {
		// TODO Auto-generated method stub
		if(mTask!=null){
			for(NewsAsyncTask task:mTask){
				task.cancel(false);
			}
		}
	}
//	�������ش�Start��end������ͼƬ
	public void loadImages(int start,int end){
		for(int i=start;i<end;i++){
			String url=NewsAdapter.URLS[i];
			Log.d("aaa", url);
//			�ӻ���ȡ����Ӧ��ͼƬ
			Bitmap bitmap=getBitmapFromCache(url);
//			�������û�У���ȥ����
			if(bitmap==null){
		    NewsAsyncTask task=new NewsAsyncTask(url);
		    task.execute(url);
		    mTask.add(task);
			}else{
				ImageView imageView=(ImageView) mListView.findViewWithTag(url);
				imageView.setImageBitmap(bitmap);
				
			}
		
			
		}
	}

}
