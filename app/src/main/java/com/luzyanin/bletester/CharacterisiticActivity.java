package com.luzyanin.bletester;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.luzyanin.adapter.CharacterisiticListAdapter;
import com.luzyanin.service.BleService;
import com.luzyanin.utils.DateUtil;
import com.luzyanin.utils.Utils;
import com.xinzhongxinbletester.R;

public class CharacterisiticActivity extends Activity {
	ListView lv;
	BluetoothAdapter mBluetoothAdapter;
	CharacterisiticListAdapter charListAdapter;
	UUID uuid;
	BleService bleService;
	BluetoothGattService gattService;
	BluetoothGattService gattServicewithFilter;
	BluetoothGattCharacteristic gattChar;
	int rssi;

	
	
	
	private final ServiceConnection conn = new ServiceConnection() {
		@SuppressLint({ "NewApi", "DefaultLocale" })
		@Override
		public void onServiceConnected(ComponentName arg0, IBinder service) {
			// TODO Auto-generated method stub
			bleService = ((BleService.LocalBinder) service).getService();
			gattService = bleService.mBluetoothGatt.getService(uuid);


			//bleService.mBluetoothGatt.readRemoteRssi();
			final ArrayList<HashMap<String, String>> charNames = new ArrayList<HashMap<String, String>>();
			final List<BluetoothGattCharacteristic> gattchars = gattService.getCharacteristics();
			for (BluetoothGattCharacteristic c : gattchars) {
				HashMap<String, String> currentCharData = new HashMap<String, String>();
				String uuidStr = c.getUuid().toString();



				currentCharData.put("Name", Utils.attributes.containsKey(uuidStr) ? Utils.attributes.get(uuidStr): "Unknown Characteristics");
				charNames.add(currentCharData);
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					charListAdapter.addCharNames(charNames);
					charListAdapter.addChars(gattchars);
					charListAdapter.notifyDataSetChanged();
				}
			});
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			bleService = null;
		}
	};
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@SuppressLint("NewApi")
		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (BleService.ACTION_GATT_RSSI.equals(action)) {
				rssi = intent.getExtras().getInt(BleService.EXTRA_DATA_RSSI);
				CharacterisiticActivity.this.invalidateOptionsMenu();
			}
			if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
				Toast.makeText(CharacterisiticActivity.this, "Ресивер дисконект",
						Toast.LENGTH_SHORT).show();
				bleService.connect(DeviceConnect.bleAddress);
			}
			if (BleService.ACTION_CHAR_READED.equals(action)) {
				//displayData(intent.getStringExtra(BleService.EXTRA_DATA));
				//String aa = intent.getExtras().get("HexValue").toString();
				String charUuid = intent.getExtras().get("charUUID").toString();
				if (charUuid.equals("f0001141-0451-4000-b000-000000000000")){
					int decimal = Integer.parseInt(intent.getExtras().get("HexValue").toString(),16);
					//int decimalM3= decimal*coef/1000;
					displayDataCurrentmessure(Integer.toString(decimal)); //mIntent.putExtra("HexValue", hexValue.toString());
				}
                if (charUuid.equals("f0001142-0451-4000-b000-000000000000")){
                    int decimal = Integer.parseInt(intent.getExtras().get("HexValue").toString(),16);
                    //int decimalM3= decimal*coef/1000;
                    displayDataCurrentkof(Integer.toString(decimal)); //mIntent.putExtra("HexValue", hexValue.toString());
                }
                if (charUuid.equals("f0001143-0451-4000-b000-000000000000")){
                    int decimal = Integer.parseInt(intent.getExtras().get("HexValue").toString(),16);
                    //int decimalM3= decimal*coef/1000;
                    displayDataCurrentmessure(Integer.toString(decimal)); //mIntent.putExtra("HexValue", hexValue.toString());
                }
                if (charUuid.equals("f000114f-0451-4000-b000-000000000000")){
					String decimal = (String) intent.getExtras().get("HexValue");
                    //int decimalM3= decimal*coef/1000;
					displayClockCurrentmessure(decimal); //mIntent.putExtra("HexValue", hexValue.toString());
                }
			}
		}
	};

	private void displayDataCurrentmessure(String data) {
		final TextView current_messure = (TextView)findViewById(R.id.current_mes);
		if (data != null) {

			current_messure.setText(data);
		}
	}

	private void displayDataCurrentkof(String data) {
		final TextView current_messure = (TextView)findViewById(R.id.current_kof);
		if (data != null) {

			current_messure.setText(data);
		}
	}

	private void displayClockCurrentmessure(String data) {
		final TextView current_messure = (TextView)findViewById(R.id.current_clock);
		if (data != null) {

			current_messure.setText(DateUtil.getCurrenttime(data));
		}
	}


	public static String str2HexStr(String str) {

		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		return sb.toString().trim();
	}


	public static byte[] str2Byte(String hexStr) {
		int b = hexStr.length() % 2;
		if (b != 0) {
			hexStr = "0" + hexStr;
		}
		String[] a = new String[hexStr.length() / 2];
		byte[] bytes = new byte[hexStr.length() / 2];
		for (int i = 0; i < bytes.length; i++) {
			a[i] = hexStr.substring(2 * i, 2 * i + 2);
		}
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = (byte) Integer.parseInt(a[i], 16);
		}
		return bytes;
	}

    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }




	private static IntentFilter makeGattUpdateIntentFilter() {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BleService.ACTION_CHAR_READED);
		intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
		intentFilter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
		intentFilter.addAction(BleService.BATTERY_LEVEL_AVAILABLE);
		intentFilter.addAction(BleService.ACTION_GATT_RSSI);
		return intentFilter;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chars);
		getActionBar().setTitle("Характеристики");
		uuid = (UUID) getIntent().getExtras().get("serviceUUID");
		init();
		bindService(new Intent(this, BleService.class), conn, BIND_AUTO_CREATE);
		registerReceiver(mBroadcastReceiver, makeGattUpdateIntentFilter());
	}



	@SuppressLint("NewApi")
	private void init() {

		lv = (ListView) findViewById(R.id.lv_charList);
		lv.setEmptyView(findViewById(R.id.pb_empty2));
		charListAdapter = new CharacterisiticListAdapter(this);
		lv.setAdapter(charListAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
									int position, long arg3) {
				// TODO Auto-generated method stub
				switch (position) {
					case 0:
						UUID charUuid = bleService.mBluetoothGatt.getService(uuid).getCharacteristics().get(position).getUuid();
						gattChar = bleService.mBluetoothGatt.getService(uuid).getCharacteristic(charUuid);
						bleService.mBluetoothGatt.readCharacteristic(gattChar);
						break;
					case 1:

						UUID charUuid1 = bleService.mBluetoothGatt.getService(uuid).getCharacteristics().get(position).getUuid();
						gattChar = bleService.mBluetoothGatt.getService(uuid).getCharacteristic(charUuid1);
						bleService.mBluetoothGatt.readCharacteristic(gattChar);
						break;

					case 2:
						break;
					case 3:
						UUID charUuid6 = bleService.mBluetoothGatt.getService(uuid).getCharacteristics().get(position).getUuid();
						gattChar = bleService.mBluetoothGatt.getService(uuid).getCharacteristic(charUuid6);
						bleService.mBluetoothGatt.readCharacteristic(gattChar);

						break;

				}
			}


		});


		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                switch (position){
                    case 0:

                        break;

                    case 1:
						int k1=0x0A;
						String k2=Integer.toHexString(k1);
						UUID charUuid2 = bleService.mBluetoothGatt.getService(uuid).getCharacteristics().get(position).getUuid();
						gattChar = bleService.mBluetoothGatt.getService(uuid).getCharacteristic(charUuid2);
						gattChar.setValue(str2Byte(k2));
						bleService.mBluetoothGatt.writeCharacteristic(gattChar);
						break;
                    case 2:
						break;

                    case 3:

                    	Long time_milssec = new Long(System.currentTimeMillis());
						Long time_sec=time_milssec/1000;
						String time_secinHEX = Long.toHexString(time_sec);

						UUID charUuid4 = bleService.mBluetoothGatt.getService(uuid).getCharacteristics().get(position).getUuid();
						gattChar = bleService.mBluetoothGatt.getService(uuid).getCharacteristic(charUuid4);
						gattChar.setValue(str2Byte(time_secinHEX));

					break;
                }




                return true;
            }
        });


    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub

		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindService(conn);
		unregisterReceiver(mBroadcastReceiver);
	}
}
