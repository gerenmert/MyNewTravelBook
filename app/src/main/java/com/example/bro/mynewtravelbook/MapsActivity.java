package com.example.bro.mynewtravelbook;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMapLongClickListener {
    LocationManager locationManager; //Bu sınıf, sistem konum servislerine erişim sağlar.
    LocationListener locationListener; //Konum değiştiğinde, location manager bildirim almak için kullanılır
    private GoogleMap mMap; //haritamiz
    static SQLiteDatabase database; //mainactivideden erisecegimiz icin static yaptik databasemizi

   // GoogleMap.OnMapLongClickListener ile onMapLongClick methodunda uzun tikladinda ne yapacak methodu yazacaz
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }




        //harita hazir oldugunda yapilacak islemler(OnmapreadyCallback den ezilmis)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap; //map e google map atadik
        mMap.setOnMapLongClickListener(this); // adress bilgisini haritadaki marker tasidik(uzun tikladiginde)
        Intent intent=getIntent(); // Intent, başka bir Activity'yi başlatma (MainActivity)
        String info=intent.getStringExtra("info"); //veriyi aliyoruz
        if(info.matches("new")){ //aldigimiz veri yeni locasyon ise(yeni yer eklemem bekleniyorsa)



       locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE); //location tanimliyoruz
       locationListener= new LocationListener() {
           @Override
           public void onLocationChanged(Location location) { //kullanicinin yeri degistiginde calisacak method
               //onLocation change her seferinde cagirildigi icin harita uzreinde gezemioyruz yenileniyor(guncelleniyor) onun icin kontrol koymaliyiz

                // LatLng bize enlem ve boylamlari verir(yer belirten obje)
               SharedPreferences sharedPreferences=MapsActivity.this.getSharedPreferences("com.example.bro.mynewtravelbook",MODE_PRIVATE); //kaydemek istedigimiz (true/false bit) bunun icinde tutabiliyoruz
               boolean firstTimeCheck=sharedPreferences.getBoolean("notFirstTime",false);//ilk deger false olsun
                    //amacimiz  uygulama ilk acildiginda
               if(!firstTimeCheck){ //uygulama ilk acildiginda  kullaniciyi guncelle
                   LatLng userLocation=new LatLng(location.getLatitude(),location.getLongitude());

                   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15)); //newLatLngZoom ilk parameteresi nereye zoom yapacam,ikinci nekadar zoom yapacam

                   sharedPreferences.edit().putBoolean("notFirstTime",true).apply();
               }
               //kaydemek istedigimiz (true/false bit) bunun icinde tutabiliyoruz(sharedpreferences) (bayrak mantigi)


               //firstCheck time biz daha once app hic acmadigizsak varsayilan false olarak gelecek

                //daha onceden boyle bir kayit yapilmadiysa









           }

           @Override
           public void onStatusChanged(String provider, int status, Bundle extras) {
               //konum alinamiyorsa
           }

           @Override
           public void onProviderEnabled(String provider) {
               //konum servisini  kullnicinin elle acmasi
           }

           @Override
           public void onProviderDisabled(String provider) {
                //ilgili servis kullanici tarafindan kapatildiysa
           }
       };



            //locationManger kullanmak icin kullanicidan bazi izinler almak gerekiyor
           if(Build.VERSION.SDK_INT>=23){ //bunu yapmamiz sebebi 23 ve sonrasi  location icin  izin gerekeiyor kullanicidan eski versiyonlarda izin almadanda olabiliyor
             if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){ //kullanicinin lokasyonuns(yeri)  ulasma iznimiz  var mi
                 requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1); // iznimiz yoksa izin istiyoruz kullsnicidan.(request ise bu izinin id si gibi dusun biz atadik)

             }else{// Eger izin varsa
                 locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);//kullanicinin konumunu almaya basla(mintime kac milisaniye sonra yenilensin,misdistance kac metre gittiginde yenilensin)
                 mMap.clear(); //daha once marker(kirmizi unlem isareti) varsa kaldir
                 Location lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //lastLocation(kullanicinin son konumu) eger  bilmezsek onLocationChanged  calismaz
                 if(lastLocation!=null){ // telefon ilk acildiginda lokasyon olmyacagindan hata almayalim
                     LatLng lastUserLocation=new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                     mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15)); //son locasyonu goster  zoom boyutu 15 yap
                 }

             }

           }else{ //eski vversiyon direk location al
               locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);//kullanicinin konumunu almaya basla(mintime kac milisaniye sonra yenilensin,kac)

               Location lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //son bilinen location(getlastknowlocation)
               if(lastLocation!=null){ // telefon ilk acildiginda lokasyon olmyacagindan hata almayalim
                   LatLng lastUserLocation=new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());//son location nun konumunu al (latlng)
                   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15)); //ilk parameter yer ,ikinci zoom
               }
           }

    }else{  //eger yeni loasyon degilse ozaman kaydettimiz lokasyondur onun tikladigi positioni aliyoruz
            //biz bu locasyonlari arraylistten aliyoruz
            mMap.clear();
            int position=intent.getIntExtra("position",0);
            LatLng location= new LatLng(MainActivity.locations.get(position).latitude,MainActivity.locations.get(position).longitude);//position(tikladigi idsine gore  enlem ve boylamini aliyoruz)
            String placeName=MainActivity.names.get(position);
            mMap.addMarker(new MarkerOptions().title(placeName).position(location));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
        }
    }
              //asadaki  onRequest method izin aldiktan sonra yapilacak islemler
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { //kullanicinin izni yoksa izizn verdiginde yapilacak islemler
        //kullanicinin izinleri verdiginde yapilacak islemler
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0){ //bir sonuc geldiyse, 0 dan buyuk olacak
            if(requestCode==1){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){ //burdada izin aliyoruz (daha kisa hali)
                    //yukardaki izin istemeyle ayni daha kisa olani     (eger izin verilmisse)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);//kullanicinin konumunu almaya basla(mintime kac milisaniye sonra yenilensin,kac)


                      Intent intent =getIntent();
                      String info=intent.getStringExtra("info");
                      if(info.matches("new")){ //aldigimiz veri yeni locasyon ise(yeni yer eklemem bekleniyorsa)
                          Location lastLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                          if(lastLocation!=null){ // daha once lokasyon alinmadiysa(telefon ilk acildiginda bu uygulama hata vermemek icin  kontrol ediyoruz)
                              LatLng lastUserLocation=new LatLng(lastLocation.getLatitude(),lastLocation.getLongitude());
                              mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation,15));
                          }
                      }else{
                          mMap.clear(); //amac onceki marker temizlemek
                          int position=intent.getIntExtra("position",0);
                          LatLng location= new LatLng(MainActivity.locations.get(position).latitude,MainActivity.locations.get(position).longitude);
                          String placeName=MainActivity.names.get(position);
                          mMap.addMarker(new MarkerOptions().title(placeName).position(location));
                          mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15));
                      }
                }
            }
        }

    }


          //kullanicin uzun tikladiginda yapilacak islemler
    @Override
    public void onMapLongClick(LatLng latLng) {
           mMap.clear();
           //adres biligilerini almak icin kullandigimiz sinif
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault()); //geocoder, adres bilgilerini(sokak,cadde ..) islemleri enlem boylama donustutur. locale.default  (yerel) dilde o uygun gosterim(adres gosterimi)
        String address="";
        try { // tam adresi alamaz  en boylama donusturemesse app cokmesinde try catch aldik
             List<Address>addressList=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1); //tiklandigina maxsimum 1 adres dondursun
           if(addressList!=null && addressList.size()>0){


                   if(addressList.get(0).getThoroughfare()!=null){
                       address+=addressList.get(0).getThoroughfare(); //caddde ismi
                       if(addressList.get(0).getSubThoroughfare()!=null){
                           address+=addressList.get(0).getSubThoroughfare();//sokak adi
                       }
                   }else{ //sokak cadde bulamiyorsa ulkeyi dondur
                       if(addressList.get(0).getCountryName()!=null) {
                           address += addressList.get(0).getCountryName();
                       }
                       //yoka no adresi dondur

                   }





           }else{
               address="";
           }


        } catch (IOException e) {
            e.printStackTrace();
        }
        if(address.matches("")){
            address=""; //adres bulamadigimizda
        }
        mMap.addMarker(new MarkerOptions().title(address).position(latLng)); //marker (kirimizi isareti ekledik)sectigimiz yeri
        Toast.makeText(getApplicationContext(),"new Place",Toast.LENGTH_SHORT).show(); //yeni yer yaratildiginda geriye gitmesi gerekecek

         //kaydedeceklerimiz (amac verileri main activitye dondugumuzde kaydedilen verilerin main activitede gozukmesi(tekrar uygulamandan gir cik gerek kalamasin))
          MainActivity.names.add(address); //yeri
          MainActivity.locations.add(latLng);// konumu
          MainActivity.arrayAdapter.notifyDataSetChanged(); //yeni datalar eklendiginde guncelleme yap

        //veritabani islemleri sqlite database (tikladigimiz yerleri veritabanimiza ekleyecegiz)
          try { //veritabanina baglamadiginda  bir sıkıntı olursa app cokmesin diye try catch aldik
            Double l1=latLng.latitude;
            Double l2= latLng.longitude;
               //langitude degerleri string cevirelim
            String coord1=l1.toString();
            String coord2=l2.toString();

                  database=this.openOrCreateDatabase("Places",MODE_PRIVATE,null); //errorhandler ihtiyac yok null yaptik
             database.execSQL("CREATE TABLE IF NOT EXISTS places (name VARCHAR,latitude VARCHAR,longitude VARCHAR)"); //tablo yok ise tablo olustur
              String compile="INSERT INTO places (name, latitude, longitude)VALUES (?,?,?)"; //degerleri bilmedigimiz icin soru isareti koyuyoruz
              SQLiteStatement sqLiteStatement=database.compileStatement(compile);


              sqLiteStatement.bindString(1,address); //adresi
              sqLiteStatement.bindString(2,coord1); //enlemi
              sqLiteStatement.bindString(3,coord2); //boylami

              sqLiteStatement.execute(); //kaydediyoruz



          }catch (Exception e){

          }
    }
}
