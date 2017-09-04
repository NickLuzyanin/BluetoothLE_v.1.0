package com.luzyanin.adapter;

import java.util.ArrayList;

import android.widget.ImageView;
import com.xinzhongxinbletester.R;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BleDeviceListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private ArrayList<BluetoothDevice> mLeDevices;
	private ArrayList<Integer> RSSIs;
	private ArrayList<String> scanRecords;

	public BleDeviceListAdapter(Context context) {
		mLeDevices = new ArrayList<BluetoothDevice>();
		RSSIs = new ArrayList<Integer>();
		scanRecords = new ArrayList<String>();
		this.mInflater = LayoutInflater.from(context);
	}

	public void addDevice(BluetoothDevice device, int RSSI, String scanRecord) {
		if (!mLeDevices.contains(device)) {
			this.mLeDevices.add(device);
			this.RSSIs.add(RSSI);
			this.scanRecords.add(scanRecord);
		} else {
			for (int i = 0; i < mLeDevices.size(); i++) {
				BluetoothDevice d = mLeDevices.get(i);
				if (device.getAddress().equals(d.getAddress())) {
					RSSIs.set(i, RSSI);
					scanRecords.set(i, scanRecord);
				}
			}
		}
	}

	public BluetoothDevice getDevice(int position) {
		// TODO Auto-generated method stub
		return mLeDevices.get(position);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mLeDevices.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mLeDevices.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View view, ViewGroup arg2) {
		// TODO Auto-generated method stub

		ViewHolder viewholder;
		if (view == null) {
			view = mInflater.inflate(R.layout.item_devicelist, null);
			viewholder = new ViewHolder();
			viewholder.devicename = (TextView) view.findViewById(R.id.tv_devicelist_name1);
			viewholder.deviceAddress = (TextView) view.findViewById(R.id.tv_devicelist_address);
			viewholder.deviceRSSI = (TextView) view.findViewById(R.id.tv_devicelist_rssi1);
			viewholder.ikonka = (ImageView) view.findViewById(R.id.watertoolsView);
			//viewholder.devicerecord = (TextView) view.findViewById(R.id.tv_devicelist_scanRecord);
			//viewholder.devicerecord_name = (TextView) view.findViewById(R.id.tv_devicelist_scanRecord_name);
			view.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) view.getTag();
		}
		String name = mLeDevices.get(position).getName();
		if (name != null && name.toString().equals("Water meter")){
			viewholder.devicename.setText(name);
			viewholder.deviceAddress.setText("Адрес: "+ mLeDevices.get(position).getAddress());
			viewholder.deviceRSSI.setText("Уровень сигнала: " + RSSIs.get(position).toString());
		}

		//else
		//	viewholder.devicename.setText("Unknow Device");
		//viewholder.deviceAddress.setText("Адрес: "+ mLeDevices.get(position).getAddress());
		//viewholder.deviceRSSI.setText("Уровень сигнала: " + RSSIs.get(position).toString());


		String a= 	mLeDevices.get(position).getAddress().toString();
		if(a.equals("24:71:89:08:1B:81")){
			viewholder.ikonka.setImageResource(R.drawable.watermeter_kh);
		} else {
			viewholder.ikonka.setImageResource(R.drawable.light);
		}

		//viewholder.devicerecord.setText("Пакет трансляции:" + "\n"+ scanRecords.get(position));
		//viewholder.devicerecord_name.setText("Имя пакета вещания:"+ Utils.ParseScanRecord(scanRecords.get(position)));
		return view;
	}

	static class ViewHolder {
		TextView devicename;
		TextView deviceAddress;
		TextView deviceRSSI;
		ImageView ikonka;
		//TextView devicerecord;
		//TextView devicerecord_name;
	}

	public void clear() {
		// TODO Auto-generated method stub
		mLeDevices.clear();
		RSSIs.clear();
		scanRecords.clear();
		this.notifyDataSetChanged();
	}

}
