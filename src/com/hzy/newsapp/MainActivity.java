package com.hzy.newsapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends Activity implements OnItemClickListener {
	private ListView newsList;
	private static String URL="http://v.juhe.cn/toutiao/index?type=top&key=9918a2cfab25bd3bce959221be46e1c9";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		newsList=(ListView) findViewById(R.id.news_list);
		new NewsAsynTask().execute(URL);
		newsList.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
//	实现网络的异步访问
	class NewsAsynTask extends AsyncTask<String, Void, List<NewsBean>>{

		@Override
		protected List<NewsBean> doInBackground(String... params) {
			// TODO Auto-generated method stub
			return getJsonData(params[0]);
		}
          @Override
        protected void onPostExecute(List<NewsBean> newsBean) {
        // TODO Auto-generated method stub
        super.onPostExecute(newsBean);
        NewsAdapter adapter=new NewsAdapter(MainActivity.this, newsBean,newsList);
        newsList.setAdapter(adapter);
        }	
	
	
	}
//	将URL对应的JSON格式数据转化为所封装的NEWSBean对象
	private List<NewsBean> getJsonData(String url){
		List<NewsBean> newsBeanList=new ArrayList<NewsBean>();
		try {
			String jsonString=readStream(new java.net.URL(url).openStream());
			Log.d("JsonString", jsonString);
			JSONObject jsonObject;
			NewsBean newsBean;
			jsonObject =new JSONObject(jsonString);
			JSONObject result=jsonObject.getJSONObject("result");
			JSONArray data=result.getJSONArray("data");
			for(int i=0;i<data.length();i++){
				jsonObject=data.getJSONObject(i);
				newsBean=new NewsBean();
				newsBean.newsIconUrl=jsonObject.getString("thumbnail_pic_s");
				newsBean.newsTitle=jsonObject.getString("title");
				newsBean.newsTyle=jsonObject.getString("realtype");
				newsBean.newsUrl=jsonObject.getString("url");
				newsBeanList.add(newsBean);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newsBeanList;
		
	}
//	解析网页返回的数据
	private String readStream(InputStream in){
		InputStreamReader isr;
		String result="";
		try {
			String line="";
			isr=new InputStreamReader(in, "utf_8");
			BufferedReader br=new BufferedReader(isr);
			try {
				while((line=br.readLine())!=null){
					result+=line;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return result;
		
	}

@Override
public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	// TODO Auto-generated method stub
	NewsBean mNewsBean=(NewsBean) parent.getItemAtPosition(position);
	String url=mNewsBean.newsUrl;
	Intent intent=new Intent();
	intent.setAction(Intent.ACTION_VIEW);
	intent.setData(Uri.parse(url));
	startActivity(intent);
}
}
