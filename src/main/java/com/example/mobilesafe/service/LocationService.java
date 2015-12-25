package com.example.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;


public class LocationService extends Service {

    private LocationManager lm;
    private MyLocationListener listener;
    private SharedPreferences mPres;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPres = getSharedPreferences("config", MODE_PRIVATE);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
       /* List<String> allProviders = lm.getAllProviders();// 获取所有位置提供者
        System.out.println(allProviders);*/

        /**
         * * @param criteria the criteria that need to be matched
         * @param enabledOnly if true then only a provider that is currently enabled is returned
         */
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);  //是否允许付费 ,比如使用3G网络
        criteria.setAccuracy(Criteria.ACCURACY_FINE);   //设置精确度，精确度越高，耗电量越大
        String bestProvider = lm.getBestProvider(criteria, true);  //获取最佳位置提供者
        listener = new MyLocationListener();
        lm.requestLocationUpdates(bestProvider, 0, 0, listener);// 参1表示位置提供者,参2表示最短更新时间,参3表示最短更新距离

    }

    class MyLocationListener implements LocationListener {

        // 位置发生变化
        @Override
        public void onLocationChanged(Location location) {
           //将获取的经纬度保存到sp中
            mPres.edit().putString("location", "j : " + location.getLongitude() + " w :" +
                    location.getLatitude());

            stopSelf();   //拿到短信后就停掉service，这样不那么耗电了，然后每次发送读取的地理位置就是最近一次保存的数据
        }

        // 位置提供者状态发生变化
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        // 用户打开gps
        @Override
        public void onProviderEnabled(String provider) {
        }

        // 用户关闭gps
        @Override
        public void onProviderDisabled(String provider) {
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lm.removeUpdates(listener);// 当activity销毁时,停止更新位置, 节省电量
    }
}
