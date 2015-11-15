package net.cloud95.android.lession.mapshape01;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.util.ByteArrayBuffer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


public class MapShape01Activity extends FragmentActivity {

	private GoogleMap map;
	private Switch polyline_switch, polygon_switch, circle_switch;
	private SeekBar ployline_seekbar, ploygon_seekbar, circle_seekbar;

	private Polyline polyline;
	private Polygon polygon;
	private Circle circle;

	// 地圖中心點，繪製圓形用的座標
	private static final LatLng mapCenter = new LatLng(25.051235, 121.538315);

	// 繪製線條用的座標
	private static final LatLng station01 = new LatLng(25.04657, 121.51763);
	private static final LatLng station02 = new LatLng(25.044937, 121.523037);
	private static final LatLng station03 = new LatLng(25.042293, 121.532907);
	private static final LatLng station04 = new LatLng(25.051702, 121.532907);
	private static final LatLng station05 = new LatLng(25.05971, 121.533251);

	// 繪製區塊用的座標
	private static final LatLng station06 = new LatLng(25.062354, 121.541061);
	private static final LatLng station07 = new LatLng(25.041671, 121.5378);
	private static final LatLng station08 = new LatLng(25.04136, 121.557713);
	private static final LatLng station09 = new LatLng(25.063054, 121.552048);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		processViews();
		processControllers();

		// 移動地圖
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
				mapCenter, 13);
		map.moveCamera(cameraUpdate);

		LoadingProgress = new ProgressDialog(this);
		LoadingProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		LoadingProgress.setMessage("DOWNLOADING");
		LoadingProgress.setIndeterminate(false);
		LoadingProgress.setCancelable(false);
		LoadingProgress.show();

		String dataUrl = "http://maps.googleapis.com/maps/api/directions/xml?origin=25.04657,121.517638&destination=25.05971,121.533251&sensor=false&units=metric";
		downloadBackgroundTaks thisTask = new downloadBackgroundTaks();
		thisTask.execute(dataUrl);
	}

	@Override
	public void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {
		if (map == null) {
			map = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
		}
	}

	private void processViews() {
		setUpMapIfNeeded();

		polyline_switch = (Switch) findViewById(R.id.polyline_switch);
		ployline_seekbar = (SeekBar) findViewById(R.id.ployline_seekbar);
		polygon_switch = (Switch) findViewById(R.id.polygon_switch);
		ploygon_seekbar = (SeekBar) findViewById(R.id.ploygon_seekbar);
		circle_switch = (Switch) findViewById(R.id.circle_switch);
		circle_seekbar = (SeekBar) findViewById(R.id.circle_seekbar);

		// 線條設定物件
		PolylineOptions polylineOptions = new PolylineOptions();
		// 加入線條座標
		polylineOptions.add(station01, station02, station03, station04,
				station05);
		// 設定線條的寬度、顏色
		polylineOptions.width(10);
		polylineOptions.color(Color.BLUE);
		// 加入線條到地圖
		polyline = map.addPolyline(polylineOptions);
		// 設定區塊中未填滿區域的座標
		List<LatLng> points = new ArrayList<LatLng>();
		points.add(new LatLng(25.05178, 121.545267));
		points.add(new LatLng(25.043148, 121.545353));
		points.add(new LatLng(25.044665, 121.550546));
		points.add(new LatLng(25.051585, 121.550288));

		// 繪製區塊，直接設定座標與未填滿區域
		polygon = map.addPolygon((new PolygonOptions()).add(station06,
				station07, station08, station09).addHole(points));
		// 設定區塊的線條寬度、線條顏色、填滿的顏色與指定繪製順序
		polygon.setStrokeWidth(5);
		polygon.setStrokeColor(Color.rgb(102, 153, 0));
		polygon.setFillColor(Color.argb(200, 255, 255, 0));// 透明度的設定
		polygon.setZIndex(1);// 設定圖層

		// 繪製圓形，設定圓心座標、半徑、繪製順序、線條寬度、線條顏色與填滿的顏色
		circle = map.addCircle((new CircleOptions()).center(mapCenter)
				.radius(2500).zIndex(2).strokeWidth(0)
				.strokeColor(Color.argb(200, 255, 0, 0))
				.fillColor(Color.argb(50, 255, 0, 0)));
	}

	private void processControllers() {
		// 註冊控制圖形是否顯示的事件
		OnCheckedChangeListener switchListener;
		switchListener = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (buttonView == polyline_switch) {
					polyline.setVisible(isChecked);
				} else if (buttonView == polygon_switch) {
					polygon.setVisible(isChecked);
				} else if (buttonView == circle_switch) {
					circle.setVisible(isChecked);
				}
			}
		};
		polyline_switch.setOnCheckedChangeListener(switchListener);
		polygon_switch.setOnCheckedChangeListener(switchListener);
		circle_switch.setOnCheckedChangeListener(switchListener);

		// 註冊改變寬度的事件
		OnSeekBarChangeListener seekBarListener;
		seekBarListener = new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				if (seekBar == ployline_seekbar) {
					polyline.setWidth(progress);
				} else if (seekBar == ploygon_seekbar) {
					polygon.setStrokeWidth(progress);
				} else if (seekBar == circle_seekbar) {
					circle.setStrokeWidth(progress);
				}

			}
		};
		ployline_seekbar.setOnSeekBarChangeListener(seekBarListener);
		ploygon_seekbar.setOnSeekBarChangeListener(seekBarListener);
		circle_seekbar.setOnSeekBarChangeListener(seekBarListener);
	}

	private List<LatLng> getpoints = new ArrayList<LatLng>();
	private ProgressDialog LoadingProgress;

	private class downloadBackgroundTaks extends
			AsyncTask<String, Integer, String> {
		boolean ifSuccess = true;
		String tag[] = { "lat", "lng" };

		@Override
		protected String doInBackground(String... urls) {
			// TODO Auto-generated method stub
			int ifError = 0;
			String resultString = "";
			URL submitURL = null;
			ifError = 0;

			if (urls == null || urls.length != 1) {
				ifError = 1;
				ifSuccess = false;
			}
			if (ifError == 0) {

				try {
					submitURL = new URL(urls[0]);
				} catch (MalformedURLException e) {
					ifSuccess = false;
				}
			}

			if (submitURL != null) {
				try {
					HttpURLConnection urlConn = (HttpURLConnection) submitURL
							.openConnection();
					InputStreamReader httpStream = new InputStreamReader(
							urlConn.getInputStream());
					BufferedReader httpBuffer = new BufferedReader(httpStream);

					String inputLine = null;
					while ((inputLine = httpBuffer.readLine()) != null) {
						resultString += inputLine + "\n";// 加上換行
					}
					httpStream.close();
					urlConn.disconnect();
				} catch (IOException e) {
					ifSuccess = false;
				}
			}
			return resultString; // 傳給 onPostExecute
		}

		protected void onPostExecute(String result) {

			if (LoadingProgress.isShowing() == true) {
				LoadingProgress.hide();
				LoadingProgress.dismiss();
			}
			Log.e("Result", result);// 把讀進來的資料呈現在LogCat
			// 將讀入文件剖析
			if (ifSuccess) {
				// 開立文件剖析工廠
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db;// 工人

				try {
					// 從文件剖析工廠取得建立文件的工人
					db = dbf.newDocumentBuilder();
					ByteArrayInputStream thisStream = null;
					// TODO自動產生的方法Stub
					try {
						// 將網路下載的資料根據UTF-8 轉換為Bytes
						// 將Bytes轉換為ByteArrayInputStream方可以相同格式處理不同編碼的文件
						thisStream = new ByteArrayInputStream(
								result.getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						ifSuccess = false;
					}
					// 文件建立工人對ByteArrayInputStream進行剖析
					Document doc = db.parse(thisStream);
					// 取得TagName為DirectionsResponse的節點列表
					NodeList directionsResponse = doc
							.getElementsByTagName("DirectiondResponse");
					// 判斷TagName為DirectionsResponse的節點數量
					if (directionsResponse.getLength() > 0) {
						// ((Element)directionsResponse.item(n))取得第n個節點
						// 取得第n個節點下TagName為route的節點列表
						NodeList route = ((Element) directionsResponse.item(0))
								.getElementsByTagName("route");
						// 判斷route為DirectionsResponse的節點數量
						if (route.getLength() > 0) {
							NodeList leg = ((Element) route.item(0))
									.getElementsByTagName("leg");
							if (leg.getLength() > 0) {
								NodeList step = ((Element) leg.item(0))
										.getElementsByTagName("step");
								if (step.getLength() > 0) {
									LatLng lastLatLng = null;// 上一個經緯度
									LatLng tempLatLng = null;// 暫時經緯度
									for (int i = 0; i < step.getLength(); i++) {
										NodeList start_location = ((Element) step
												.item(i))
												.getElementsByTagName("start_location");
										NodeList startlats = ((Element) start_location
												.item(0))
												.getElementsByTagName("lat");
										NodeList startlngs = ((Element) start_location
												.item(0))
												.getElementsByTagName("lng");

										if (startlats.item(0).getNodeType() == Node.ELEMENT_NODE
												&& startlngs.item(0)
														.getNodeType() == Node.ELEMENT_NODE) {
											tempLatLng = new LatLng(
													Double.parseDouble(((Element) startlats
															.item(0))
															.getTextContent()),
													Double.parseDouble(((Element) startlats
															.item(0))
															.getTextContent()));

											if (lastLatLng == null) {
												lastLatLng = tempLatLng;
												getpoints.add(lastLatLng);
											} else if (!lastLatLng
													.equals(tempLatLng)) {
												lastLatLng = tempLatLng;
												getpoints.add(lastLatLng);
											}
										}
										NodeList end_location = ((Element) step
												.item(i))
												.getElementsByTagName("end_location");
										NodeList endlats = ((Element) end_location
												.item(0))
												.getElementsByTagName("lat");
										NodeList endlngs = ((Element) end_location
												.item(0))
												.getElementsByTagName("lng");
										if (endlats.item(0).getNodeType() == Node.ELEMENT_NODE
												&& endlngs.item(0)
														.getNodeType() == Node.ELEMENT_NODE) {
											tempLatLng = new LatLng(
													Double.parseDouble(((Element) endlats
															.item(0))
															.getTextContent()),
													Double.parseDouble(((Element) endlats
															.item(0))
															.getTextContent()));

											if (lastLatLng == null) {
												lastLatLng = tempLatLng;
												getpoints.add(lastLatLng);
											} else if (!lastLatLng
													.equals(tempLatLng)) {
												lastLatLng = tempLatLng;
												getpoints.add(lastLatLng);
											}
										}
									}

								}
							}
						}
					}
				} catch (ParserConfigurationException e) {
					ifSuccess = false;
				}catch(SAXException e){
					ifSuccess=false;
				}catch(IOException e){
					ifSuccess=false;
				}
				if(ifSuccess){
					//線條設定物件
					PolylineOptions polylineOptions = new PolylineOptions();
					for(LatLng point2:getpoints){
						polylineOptions.add(point2);
					}
					//設定線條的寬度顏色
					polylineOptions.width(10);
					polylineOptions.color(Color.RED);
					
					//加入線條到地圖
					polyline = map.addPolyline(polylineOptions);
				}
			}
		}
	}
}
