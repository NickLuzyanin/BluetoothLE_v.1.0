package com.luzyanin.bletester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.luzyanin.adapter.BleSevicesListAdapter;
import com.luzyanin.service.BleService;
import com.luzyanin.utils.Utils;
import com.xinzhongxinbletester.R;

public class DeviceConnect extends Activity {

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	public static final String FIND_DEVICE_ALARM_ON = "find.device.alarm.on";
	public static final String DISCONNECT_DEVICE = "find.device.disconnect";
	public static final String CANCEL_DEVICE_ALARM = "find.device.cancel.alarm";
	public static final String DEVICE_BATTERY = "device.battery.level";




	ListView serviceList;
	SwipeRefreshLayout swagLayout;
	BleSevicesListAdapter servicesListAdapter;
	private BluetoothAdapter mBluetoothAdapter;
	private static final String TAG="mylogs";



	Intent intent;
	SharedPreferences sharedPreferences;
	Editor editor;
	public static String bleAddress;
	boolean isConnecting = false;
	boolean isAlarm = false;
	List<BluetoothGattService> gattServices = new ArrayList<BluetoothGattService>();
	List<BluetoothGattService> gattServiceswithFilter = new ArrayList<BluetoothGattService>();
	BleService bleService;
	int rssi;
	private final ServiceConnection conn = new ServiceConnection() {
		@SuppressLint("NewApi")
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			bleService = ((BleService.LocalBinder) service).getService();
			if (!bleService.init()) {
				finish();
			}
			bleService.connect(bleAddress);

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			bleService = null;
		}
	};

	BroadcastReceiver mbtBroadcastReceiver = new BroadcastReceiver() {

		@SuppressLint({ "NewApi", "DefaultLocale" })
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (BleService.ACTION_GATT_CONNECTED.equals(action)) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(DeviceConnect.this, "Устройство подключено успешно",
								Toast.LENGTH_LONG).show();
					}
				});
			}
			if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
				Toast.makeText(DeviceConnect.this, "Соединение с устройством разорвано", Toast.LENGTH_LONG)
						.show();

			}
			if (BleService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
				String uuid = null;
				//UUID uuid1 = new UUID(0xf000114004514000,0xb000000000000000);
				bleService.mBluetoothGatt.readRemoteRssi();
			    gattServices = bleService.mBluetoothGatt.getServices();
				for (int i=0;i<gattServices.size();i++){
					String aa = gattServices.get(i).getUuid().toString();
					if(aa.equals("f0001140-0451-4000-b000-000000000000")){
						//добавить в gattServiceswithFilter объект с uuid "f0001140-0451-4000-b000-000000000000"
						gattServiceswithFilter.add(gattServices.get(i));

					}
				}
				final ArrayList<HashMap<String, String>> serviceNames = new ArrayList<HashMap<String, String>>();
				final ArrayList<HashMap<String, String>> serviceNameswithFilter = new ArrayList<HashMap<String, String>>(); //Сделал свой ArrayList  который хочу вывести

				for (BluetoothGattService ser : gattServices) {
					HashMap<String, String> currentServiceData = new HashMap<String, String>();
					HashMap<String, String> currentServiceDataFilter = new HashMap<String, String>();
					uuid = ser.getUuid().toString();
					if(uuid.equals("f0001140-0451-4000-b000-000000000000")){
						currentServiceDataFilter.put("Name", Utils.attributes.containsKey(uuid) ? Utils.attributes.get(uuid) : "Unknown Service");
						serviceNameswithFilter.add(currentServiceDataFilter);

					}
					currentServiceData.put("Name", Utils.attributes.containsKey(uuid) ? Utils.attributes.get(uuid) : "Unknown Service");
					serviceNames.add(currentServiceData);
				}

				runOnUiThread(new Runnable() {
							@SuppressLint("NewApi")
							public void run() {
								// TODO Auto-generated method stub
								servicesListAdapter.addServiceNames(serviceNameswithFilter); //было serviceNames changed serviceNameswithFilter
								servicesListAdapter.addService(gattServiceswithFilter); // было gattServices changed gattServiceswithFilter
								servicesListAdapter.notifyDataSetChanged();
								Toast.makeText(DeviceConnect.this, "Выведен список сервисов", Toast.LENGTH_LONG).show();
							}
				});
			}
			if (BleService.ACTION_GATT_RSSI.equals(action)) {
				rssi = intent.getExtras().getInt(BleService.EXTRA_DATA_RSSI);
				DeviceConnect.this.invalidateOptionsMenu();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_servies);
		getActionBar().setTitle("Список сервисов");
		//sharedPreferences = getPreferences(0);
		//editor = sharedPreferences.edit();
		init();
		bindBleSevice();
		registerReceiver(mbtBroadcastReceiver, makeGattUpdateIntentFilter());

	}

	private void bindBleSevice() {
		Intent serviceIntent = new Intent(this, BleService.class);
		bindService(serviceIntent, conn, BIND_AUTO_CREATE);
	}

	@Override
	protected void onStart() {
		super.onResume();
		Log.d(TAG, "Класс DeviceScanActivity метод OnStart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "Класс DeviceScanActivity метод OnResume");


	}

	private void init() {
		serviceList = (ListView) findViewById(R.id.lv_deviceList);
		serviceList.setEmptyView(findViewById(R.id.pb_empty));
		servicesListAdapter = new BleSevicesListAdapter(this);
		swagLayout = (SwipeRefreshLayout) findViewById(R.id.swagLayout);
		swagLayout.setOnRefreshListener(new OnRefreshListener() {

			@SuppressLint("NewApi")
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				servicesListAdapter.clear();
				bleService.mBluetoothGatt.discoverServices();
				servicesListAdapter.notifyDataSetChanged();
				swagLayout.setRefreshing(false);
			}
		});
		serviceList.setAdapter(servicesListAdapter);
		bleAddress = getIntent().getExtras().getString((EXTRAS_DEVICE_ADDRESS));

		serviceList.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				Intent servicesIntent = new Intent(DeviceConnect.this,CharacterisiticActivity.class);
				servicesIntent.putExtra("serviceUUID",gattServiceswithFilter.get(position).getUuid());  // быдл bleService.mBluetoothGatt.getServices() //поменял на gattServiceswithFilter
				startActivity(servicesIntent);
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		bleService.close(bleService.mBluetoothGatt);
		unbindService(conn);
		unregisterReceiver(mbtBroadcastReceiver);
	}

	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(BleService.BATTERY_LEVEL_AVAILABLE);
		intentFilter.addAction(BleService.ACTION_GATT_RSSI);
		return intentFilter;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.services, menu);
		menu.getItem(1).setTitle("Уровень сигнала: "+rssi + " dBm");
		menu.getItem(0).setVisible(false);
		return true;
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.menu_rssi)
			item.setTitle("Уровень сигнала: "+rssi + " dBm");
		return super.onOptionsItemSelected(item);
	}





}


