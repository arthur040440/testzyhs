package com.tlh.android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import static android.content.Context.WIFI_SERVICE;

public class NetworkUtils {

	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo != null) {
			return mNetworkInfo.isConnected();
		}
		return false;
	}

	public static boolean isWifiConnected(Context context) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWiFiNetworkInfo = mConnectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
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
			System.out.println("网络中断");
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
		String netName = getNetworkTypeName(context);
		if("WIFI".equals(netName)){
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
		}else if("Ethernet".equals(netName)){
			if(type == 0){
				return getEthernetIp();
			}
			if(type == 1){
				return getEthernetMask();
			}
			if(type == 2){
				return getEthernetGateway();
			}
		}

		return "unknown";
	}

	private static String intToIp(int paramInt) {
		return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "." + (0xFF & paramInt >> 24);
	}

	// 获取以太网Ip
	private static String getEthernetIp(){
		Process cmdProcess = null;
		BufferedReader reader = null;
		String dnsIP = "";
		try {
			cmdProcess = Runtime.getRuntime().exec("getprop dhcp.eth0.dns1");
			reader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));
			dnsIP = reader.readLine();
			return dnsIP;
		} catch (IOException e) {
			return null;
		} finally{
			try {
				reader.close();
			} catch (IOException e) {
			}
			cmdProcess.destroy();
		}
	}

	// 获取以太网网关
	private static  String getEthernetGateway(){
		Process cmdProcess = null;
		BufferedReader reader = null;
		String dnsIP = "";
		try {
			cmdProcess = Runtime.getRuntime().exec("getprop dhcp.eth0.gateway");
			reader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));
			dnsIP = reader.readLine();
			return dnsIP;
		} catch (IOException e) {
			return null;
		} finally{
			try {
				reader.close();
			} catch (IOException e) {
			}
			cmdProcess.destroy();
		}
	}

	private static  String getEthernetMask(){
		Process cmdProcess = null;
		BufferedReader reader = null;
		String dnsIP = "";
		try {
			cmdProcess = Runtime.getRuntime().exec("getprop dhcp.eth0.mask");
			reader = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));
			dnsIP = reader.readLine();
			return dnsIP;
		} catch (IOException e) {
			return null;
		} finally{
			try {
				reader.close();
			} catch (IOException e) {
			}
			cmdProcess.destroy();
		}
	}

}
