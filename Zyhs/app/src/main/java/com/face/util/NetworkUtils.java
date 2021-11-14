package com.face.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import static android.content.Context.WIFI_SERVICE;

public class NetworkUtils {

	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null) {
			return mNetworkInfo.isConnected();
		}
		return false;
	}

	public static boolean isWifiConnected(Context context) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mWiFiNetworkInfo != null) {
			return mWiFiNetworkInfo.isConnected();
		}
		return false;
	}

	public static boolean isMobileConnected(Context context) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mMobileNetworkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (mMobileNetworkInfo != null) {
			return mMobileNetworkInfo.isConnected();
		}
		return false;
	}

	public static String getNetworkTypeName(Context context) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null) {
			return mNetworkInfo.getTypeName();
		}
		return "unknown";
	}

	// 检测网络并给出提示
	public static boolean testNetworkStatus(Context context){
		if(!isNetworkConnected(context)){
//			ToastUitls.toastMessage("网络中断");
			return false;
		}
		return true;
	}

	// 获取当前连接wifi名称
	public static String getNetworkName(Context context){
		WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if (wifiInfo != null) {
			return wifiInfo.getSSID();
		}
		return "unknown";
	}

	// 获取当前IP,子网掩码，网关
	public static String getNetworkInfo(Context context,int type){
		WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
		DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
		switch (type){
			case 0:
				if (dhcpInfo != null) {
					return intToIp(dhcpInfo.ipAddress);
				}
				break;
			case 1:
				if (dhcpInfo != null) {
					return intToIp(dhcpInfo.netmask);
				}
				break;
			case 2:
				if (dhcpInfo != null) {
					return intToIp(dhcpInfo.gateway);
				}
				break;
		}

		return "unknown";
	}

	private static String intToIp(int paramInt) {
		return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "." + (0xFF & paramInt >> 24);
	}


}
