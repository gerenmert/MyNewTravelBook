package com.example.bro.mynewtravelbook;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //cektigimiz isim ve locationlari listlere kaydedecegiz
    static  ArrayList<String> names=new ArrayList<String>(); //son kaydedilenlerinde listede gozukmesi icin mapdede lazım olacak
    static ArrayList<LatLng> locations=new ArrayList<LatLng>(); // static  yapiyoruz,map actividende lazim olacak
    static ArrayAdapter arrayAdapter;
      //menu baglama icin
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //menuyu baglama
        MenuInflater menuInflater=getMenuInflater(); // XML dosyalarını Menu nesnelerine örneklemek için kullanılır.
        menuInflater.inflate(R.menu.add_place,menu);


        return super.onCreateOptionsMenu(menu);

    }



      //menuye tiklandiginda ne olacak
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //menu tiklandiginda yapilacak islemler

        //tiklandigim menu add menusu ise
        if(item.getItemId()==R.id.add_place){
            //intent (kullaniciyi bir activitiden digerine activitiye gecirmek icin kullanidigimiz sinif)(haritalar icin) map gitmek icin
            Intent intent=new Intent(getApplicationContext(),MapsActivity.class);//burdan maps activities gitmek intenti tanimliyoruz
            intent.putExtra("info","new"); //   yeni yer(add place tiklandiginda last location gidecek)
            startActivity(intent); //intenti baslatiyoruz

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView=(ListView) findViewById(R.id.listView); //listeleme işlemleri yapabilmek için kullanilir

        //sql lite islemleri
        try {
            //cursor okuma islemini saglar
           MapsActivity.database=this.openOrCreateDatabase("Places",MODE_PRIVATE,null); //veritabbanimizi aciyoruz
            Cursor cursor =MapsActivity.database.rawQuery("SELECT * FROM places",null); //kaydettigimiz her seyi al
            int nameIx=cursor.getColumnIndex("name"); //kolum indexleri
            int latitudeIx=cursor.getColumnIndex("latitude");
            int longitudeIx=cursor.getColumnIndex("longitude");
                //bir sonraki eleman var sa devam et
           while(cursor.moveToNext()){

              String nameFromDatabase=cursor.getString(nameIx);
              String latitudeFromDatabase=cursor.getString(latitudeIx);
              String longitudeFromDatabase=cursor.getString(longitudeIx);


               names.add(nameFromDatabase);
                 //aldigimz stringleri double cevirdik
               Double l1=Double.parseDouble(latitudeFromDatabase);
               Double l2=Double.parseDouble(longitudeFromDatabase);

               LatLng locationFromDatabase=new LatLng(l1,l2); // donusturdugum doublelar ile  location aldik

               locations.add(locationFromDatabase);
               System.out.println("name:"+nameFromDatabase);
           }
               cursor.close();
        }catch (Exception e){

        }
        //ListView gibi bir dizi veriyi içinde bulunduran yapılara, bu verileri ArrayAdapter aracılığıyla veririz
        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,names);
        listView.setAdapter(arrayAdapter);
 //bir veri kaynağıyla, veriye ihtiyacı olan nesneyi birbirine bağlamaya yarayan yapılardır
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent=new Intent(getApplicationContext(),MapsActivity.class); //(kullaniciyi bir activitiden digerine activitiye gecirmek icin kullanidigimiz sinif)burdan map activitisine gececek
                intent.putExtra("info","old"); //eski secili yer(kayit olanlar)
                intent.putExtra("position",position); //secilen yerin posizyonunu gonderiyoruz
                startActivity(intent);
            }
        });

    }
}
