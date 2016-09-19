package com.hzy.newsapp;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter implements OnScrollListener{
	public static String[] URLS;
	private List<NewsBean> mList;
	private LayoutInflater mInflater;
	private ImageLoader mImageLoader;
	private int mStart,mEnd;
	private boolean mFirstIn;
	public NewsAdapter(Context context,List<NewsBean> data,ListView listView){
		mList=data;
		mInflater=LayoutInflater.from(context);
		mImageLoader=new ImageLoader(listView);
		URLS=new String[data.size()];
		for(int i=0;i<data.size();i++){
			URLS[i]=data.get(i).newsIconUrl;
			
		}
		mFirstIn=true;
//		注册对应的事件
		listView.setOnScrollListener(this);
	}

	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewholder=null;
		if(convertView==null){
			viewholder=new ViewHolder();
			convertView=mInflater.inflate(R.layout.item_layout, null);
			viewholder.img=(ImageView) convertView.findViewById(R.id.news_img);
			viewholder.title=(TextView) convertView.findViewById(R.id.news_title);
			viewholder.tyle=(TextView) convertView.findViewById(R.id.news_content);
			convertView.setTag(viewholder);
 
		}
		else{
			viewholder=(ViewHolder) convertView.getTag();
		}
		viewholder.img.setImageResource(R.drawable.ic_launcher);
		String url=mList.get(position).newsIconUrl;
		viewholder.img.setTag(url);
//	    new ImageLoader().showImageByThread(viewholder.img,url);
		mImageLoader.showImageByAsyncTask(viewholder.img, url);
		viewholder.title.setText(mList.get(position).newsTitle);
		viewholder.tyle.setText(mList.get(position).newsTyle);
		return convertView;
		
		// TODO Auto-generated method stub
		
	}
	class  ViewHolder{
	public	ImageView img;
	public	TextView title;
	public	TextView tyle;
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if(scrollState==SCROLL_STATE_IDLE){
//			加载可见项
			mImageLoader.loadImages(mStart, mEnd);
		}else{
//			停止任务
			mImageLoader.cancelAllTasks();
		}
	}



	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
		mStart=firstVisibleItem;
		mEnd=firstVisibleItem+visibleItemCount;

	} 

}
