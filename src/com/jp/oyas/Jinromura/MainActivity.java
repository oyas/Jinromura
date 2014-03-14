package com.jp.oyas.Jinromura;

import android.os.Bundle;
import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
//import android.widget.TextView;
import android.widget.ImageView;
//import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
//import android.content.Intent;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.os.Handler;

//import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener, AdapterView.OnItemSelectedListener {

    //private Button button1;
    int requestCode = 1;
     //グローバル変数
    Globals globals;
    //画面ごと用クラス
    Screen screen;
    //メニューの状態
    public int menustate;
    //ハンドラー（タイマー用）
    Handler handle = new Handler();
    
    //ステージの変更とリスナーの登録
    void changeScreen(int id){
    	screen.onDelete(this);	//デストラクタっぽいの呼び出し
		setContentView(id);	//画面切り替え
		switch(id){
		case R.layout.activity_main:
	        ImageView iv = new ImageView(this);
	        iv.setImageResource(R.drawable.jinromura);
	        iv.setScaleType(ImageView.ScaleType.FIT_XY);
	        setContentView(iv);
	        iv.setOnClickListener(this);
	         //メニューの変更
	        menustate = 1;
	        break;
		case R.layout.edit:
	    	 ((Button)findViewById(R.id.button)).setOnClickListener(this);
	    	 ((Spinner)findViewById(R.id.spinner)).setOnItemSelectedListener(this);

	         ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
	         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	         // アイテムを追加します
	         adapter.add("red");
	         adapter.add("green");
	         adapter.add("blue");
	         Spinner spinner = (Spinner) findViewById(R.id.spinner);
	         // アダプターを設定します
	         spinner.setAdapter(adapter);
	         adapter.add("black");
	         adapter.remove("reds");
	         
	         //メニューの変更
	        menustate = 2;
	        break;
		case R.layout.regist_name:
	    	 ((Button)findViewById(R.id.button1)).setOnClickListener(this);
	    	 ((Button)findViewById(R.id.button2)).setOnClickListener(this);
	    	 ((Button)findViewById(R.id.button)).setOnClickListener(this);
	         //メニューの変更
	        menustate = 3;
	        break;
		case R.layout.regist_role:
	    	 ((Button)findViewById(R.id.buttonNext)).setOnClickListener(this);
	         //メニューの変更
	        //menustate = 1;
	        break;
		case R.layout.game_opening:
	    	 ((Button)findViewById(R.id.button)).setOnClickListener(this);
	        break;
		case R.layout.game_evening:
	    	 ((Button)findViewById(R.id.button)).setOnClickListener(this);
	        break;
		case R.layout.game_youare:
	    	 ((Button)findViewById(R.id.button)).setOnClickListener(this);
	        break;
		case R.layout.game_night:
	    	 ((Button)findViewById(R.id.button)).setOnClickListener(this);
	    	 ((Spinner)findViewById(R.id.spinner)).setOnItemSelectedListener(this);
	        break;
		case R.layout.game_morning:
	    	 ((Button)findViewById(R.id.button)).setOnClickListener(this);
	        break;
		case R.layout.game_day:
	    	 ((Button)findViewById(R.id.button)).setOnClickListener(this);
	        break;
		case R.layout.game_vote:
	    	 ((Button)findViewById(R.id.button)).setOnClickListener(this);
	    	 ((Spinner)findViewById(R.id.spinner)).setOnItemSelectedListener(this);
	        break;
		case R.layout.game_vote_result:
	    	 ((Button)findViewById(R.id.button)).setOnClickListener(this);
	        break;
		case R.layout.game_ending:
	    	 ((Button)findViewById(R.id.button)).setOnClickListener(this);
	        break;
			
		}
		screen.change(id, this);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // フルスクリーン表示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 画面ごとの処理切り替え用クラス
        screen = new Screen();
        screen.onCreate();
        changeScreen(R.layout.activity_main);
        
        menustate = 1;
        
//        setContentView(R.layout.activity_main);
         //グローバル変数を取得
        globals = (Globals) this.getApplication();
        globals.CreateGlobals();	//初期化
        globals.player_num = this.getResources().getInteger(R.integer.INITIAL_NUM);	//初期設定
        //globals.boss = this;	//デバッグ用

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	 switch(menustate){
        default:
        	 menustate = 1;
    	 case 1:
    		 ((MenuItem)menu.findItem(R.id.action_settings)).setTitle(R.string.action_settings);
    		 break;
    	 case 2:
    		 ((MenuItem)menu.findItem(R.id.action_settings)).setTitle(R.string.action_settings_back);
    		 break;
    	 case 3:
    		 ((MenuItem)menu.findItem(R.id.action_settings)).setTitle(R.string.action_settings_finish);
    		 break;
    	 }       	
        return super.onPrepareOptionsMenu(menu);
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(menustate){
    	case 1:
           changeScreen(R.layout.edit);
           break;
    	case 2:
    	    changeScreen(R.layout.activity_main);
           break;
    	case 3:
    	    changeScreen(R.layout.activity_main);
           break;
    	}
       return true;
    }

    public void onClick(View v){
    	int next;
    	if( ( next = screen.onClick(v, this) ) != 0 ){
    		changeScreen(next);
    	}
    }
    
    //Spinnerのアイテムが選択された時
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
        // 選択されたアイテムを取得します
       // String item = (String) spinner.getSelectedItem();
        int index = spinner.getSelectedItemPosition();
        globals.spinnerSelected = index;
       // Toast.makeText(this, item + index, Toast.LENGTH_LONG).show();
        //Toast.makeText(SpinnerSampleActivity.this, item, Toast.LENGTH_LONG).show();
    }
    
    public void onNothingSelected(AdapterView<?> arg0) {
    }
    
}
