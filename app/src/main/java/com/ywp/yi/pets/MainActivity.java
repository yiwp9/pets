package com.ywp.yi.pets;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import Adapter.petsListAdapter;
import data.petContract;
import data.petContract.petEntry;
import data.petSQLite;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    ListView lvPets;
    FloatingActionButton fab;

    private ArrayList<petsList> mPetArrayData;
    private petsListAdapter petAdapter;

    private SQLiteDatabase petData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findPetsView();
        setSupportActionBar(toolbar);
        fab.setOnClickListener(this);//绑定监听器

        mPetArrayData = new ArrayList<>();
        petAdapter = new petsListAdapter(this, mPetArrayData);
        lvPets.setAdapter(petAdapter);
        //载入数据
        upDatePetList();

        lvPets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                petsList p = (petsList) adapterView.getItemAtPosition(i);
                Intent editIntent = new Intent(MainActivity.this, editPet.class);
                editIntent.putExtra("name", p.getPetName());
                editIntent.putExtra("breed", p.getPetBreed());
                startActivity(editIntent);
//              Toast.makeText(getApplicationContext(), p.getPetName() + p.getPetBreed(), Toast.LENGTH_SHORT).show();
            }
        });

        lvPets.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this,"" + i,Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(this, "restart", Toast.LENGTH_SHORT).show();
    }

    /**
     * 查找控件
     */
    private void findPetsView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        lvPets = (ListView) findViewById(R.id.lv_pets);
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }


    /**
     * 菜单创建 , 给定布局
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * 菜单中的选项点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionDelete) {
            //删除所有数据
            deletePetList();
        }
        if (id == R.id.actionFresh) {
            Log.d("main","about");
            //刷新数据
            upDatePetList();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 删除数据
     */
    private void deletePetList() {
        petSQLite mSQLite = new petSQLite(this);
        petData = mSQLite.getReadableDatabase();
        petData.delete(petEntry.TABLE_NAME,null,null);
    }

    /**
     * 更新ListView中的数据
     * @param
     */
    private void upDatePetList() {
        String Id;
        String Name;
        String[] projection = { //需要显示的行
                petEntry._ID,
                petEntry.PET_NAME,
                petEntry.PET_BREED,
                petEntry.PET_GENDER,
                petEntry.PET_WEIGHT
        };
        mPetArrayData.clear();
        petSQLite mSQLite = new petSQLite(this);
        petData = mSQLite.getReadableDatabase();
        Cursor cursor =  petData.query(petEntry.TABLE_NAME,
                projection,null,null,null,null,null);
        try {

            while(cursor.moveToNext()){
                int petId = cursor.getInt(cursor.getColumnIndex(petEntry._ID));
                Id = String.valueOf(petId);
                Name = cursor.getString(cursor.getColumnIndex(petEntry.PET_NAME));
                mPetArrayData.add(new petsList(Id,Name));
            Log.d("add",Id);
            Log.d("add",Name);
            }
            //更新adapter
            petAdapter.notifyDataSetChanged();
        }finally {
            //关闭cursor
            cursor.close();
        }
    }

    @Override
    public void onClick(View view) {

        Intent addIntent = new Intent(this, addPetInformation.class);
        startActivity(addIntent);
//        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
    }
}