package com.jp.oyas.Jinromura;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

//import com.jp.oyas.Jinromura.Globals.murabito;

import android.util.SparseArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.EditText;
import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
import android.widget.LinearLayout;
//import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;


abstract class Screen_super {
	abstract void onCreate(MainActivity boss);
	abstract int onClick(View v, MainActivity boss);
	abstract void onDelete(MainActivity boss);
}

class Screen_title extends Screen_super {
	void onCreate(MainActivity boss){
	}
	int onClick(View v, MainActivity boss){
		return R.layout.regist_name;
	}
	void onDelete(MainActivity boss){
	}
}

class Screen_edit extends Screen_super {
	void onCreate(MainActivity boss){
		String name = boss.getSharedPreferences("default", MainActivity.MODE_PRIVATE).getString("text", null);
		((EditText)boss.findViewById(R.id.editText1)).setText(name);
	}
	int onClick(View v, MainActivity boss){
		switch( v.getId() ){
		case R.id.button:
			int checkedId = ((RadioGroup)boss.findViewById(R.id.RadioGroup_win)).getCheckedRadioButtonId();
			if( checkedId == R.id.radioButton1 )
				Toast.makeText(boss, "チェック１", Toast.LENGTH_SHORT).show();
			
			//保存
			((SharedPreferences.Editor)boss.getSharedPreferences("default", MainActivity.MODE_PRIVATE).edit()).putString("text", 
					((EditText)boss.findViewById(R.id.editText1)).getText().toString() ).commit();
			
			return R.layout.activity_main;
		}
		return 0;
	}
	void onDelete(MainActivity boss){
	}
}

class Screen_regist_name extends Screen_super {
	int num, initnum;
	ArrayList<EditText> edits = new ArrayList<EditText>();
	
	void onCreate(MainActivity boss){
		initnum = boss.getResources().getInteger(R.integer.INITIAL_NUM);
		num = boss.globals.player_num;
		LinearLayout linearLayout = (LinearLayout)boss.findViewById(R.id.linearLayout);
		
		for(int i=0; i<num; i++){
			edits.add( new EditText(boss) );
	       edits.get(i).setMaxLines(1);	//最大行の設定
	       linearLayout.addView( edits.get(i), i, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT) );

	       if( i < boss.globals.players.size() ){
	    	   edits.get(i).setText( boss.globals.players.get(i).name );
	       }else{
	    	   //仮処理。参加者の名前の初期設定
	    	   edits.get(i).setText( "player" + i );
	        }
		}
		((TextView)boss.findViewById(R.id.textView2)).setText( boss.getString(R.string.regist_number) + num );
	}
	int onClick(View v, MainActivity boss){
		switch( v.getId() ){
		case R.id.button1:
			//多すぎないようにする
			if( num >= 99 )
				break;
			//項目を増やす
			edits.add( new EditText(boss) );
	       edits.get(num).setMaxLines(1);	//最大行の設定
	       ((LinearLayout)boss.findViewById(R.id.linearLayout)).addView( edits.get(num), num,
	    		   new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT) );
			num++;
			((TextView)boss.findViewById(R.id.textView2)).setText( boss.getString(R.string.regist_number) + num );
			break;
		case R.id.button2:
			//少なすぎないようにする
			if( num <= initnum )
				break;
			//項目を減らす
			num--;
			((LinearLayout)boss.findViewById(R.id.linearLayout)).removeView( edits.get(num) );
			edits.remove(num);
			((TextView)boss.findViewById(R.id.textView2)).setText( boss.getString(R.string.regist_number) + num );
			break;
		case R.id.button:	//「次へ」
			return R.layout.regist_role;
		}
		return 0;
	}
	void onDelete(MainActivity boss){
		//保存
		boss.globals.player_num = num;
		for(int i=0; i<num; i++){
			if( i >= boss.globals.players.size() ){
				boss.globals.game.addPlayer( edits.get(i).getText().toString() );
			}else{
				boss.globals.players.get(i).name = edits.get(i).getText().toString();
			}
		}
		//後処理
		edits.clear();
	}
}

class Screen_regist_role extends Screen_super {
	
	int roleNum, pNum;
	int nums[];
	
	void onCreate(MainActivity boss){
		roleNum = boss.globals.roles.size();
		pNum = boss.globals.player_num;	//のこり役職数
		nums = new int[roleNum];		//役職ごとの人数
		
		LinearLayout vi;
		LinearLayout linearLayout = (LinearLayout)boss.findViewById(R.id.scrollView1).findViewById(R.id.linearLayout);
		
		for(int i=0; i < roleNum; i++){
			vi = (LinearLayout)boss.getLayoutInflater().inflate(R.layout.regist_role_one, null);
			linearLayout.addView(vi, i, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT) );
			((TextView)vi.findViewById(R.id.textViewName)).setText( boss.globals.roles.get(i).name );
			((Button)vi.findViewById(R.id.buttonUp)).setOnClickListener(boss);
			((Button)vi.findViewById(R.id.buttonDown)).setOnClickListener(boss);
			nums[i] = 0;
		}
		
		((TextView)boss.findViewById(R.id.textViewNinzuu)).setText( boss.getString(R.string.regist_waitnum) + pNum );
	}
	int onClick(View v, MainActivity boss){
		LinearLayout ll;
		int index;
		switch( v.getId() ){
		case R.id.buttonNext:
			if( pNum == 0 ){
				return R.layout.game_opening;
			}else{
				return 0;	//何もしない
			}
		case R.id.buttonUp:
			ll = ((LinearLayout)v.getParent());
			index = ((LinearLayout)ll.getParent()).indexOfChild(ll);
			if( pNum > 0 ){
				nums[index]++;
				((TextView)ll.findViewById(R.id.textViewNum)).setText( "" + nums[index] );
				pNum--;
				((TextView)boss.findViewById(R.id.textViewNinzuu)).setText( boss.getString(R.string.regist_waitnum) + pNum );
			}
			break;
		case R.id.buttonDown:
			ll = ((LinearLayout)v.getParent());
			index = ((LinearLayout)ll.getParent()).indexOfChild(ll);
			if( nums[index] > 0 ){
				nums[index]--;
				((TextView)ll.findViewById(R.id.textViewNum)).setText( "" + nums[index] );
				pNum++;
				((TextView)boss.findViewById(R.id.textViewNinzuu)).setText( boss.getString(R.string.regist_waitnum) + pNum );
			}
			break;
		default:
			
		}
		return 0;
	}
	void onDelete(MainActivity boss){
		//人数保存
		for(int i=0; i < roleNum; i++){
			boss.globals.roles.get(i).num = nums[i];
		}
	}
}

class Screen_game_opening extends Screen_super {
	void onCreate(MainActivity boss){
	}
	int onClick(View v, MainActivity boss){
		return R.layout.game_evening;
	}
	void onDelete(MainActivity boss){
		//ゲーム開始
		//ランダムに参加者の役職を決める
		Random rand = new Random();
		int n, pNum = boss.globals.player_num;
		int[] pRole = new int[pNum];
		Arrays.fill(pRole, -1);	//初期化
		
		for(int i=0; i<pNum; i++){
			n = rand.nextInt( pNum );
			if( pRole[n] < 0 ){
				pRole[n] = i;		//pRole[役職の仮番号] = 人;
			}else{
				i--;
			}
		}
		
		int role=-1;	//今決めている役職
		int role_n=0;	//その役職の選ばなければならないのこり人数
		for(int i=0; i<pNum; i++){
			if( role_n <= 0 ){
				//次の役職
				for(int j=role+1; j<boss.globals.roles.size(); j++){
					if( boss.globals.roles.get(j).num > 0 ){
						role = j;
						role_n = boss.globals.roles.get(j).num;
						break;
					}
				}
			}
			//役職決定
			boss.globals.players.get( pRole[i] ).role = role;
			role_n--;
		}
		
		//gameクラス初期化
		boss.globals.game.start();
	}
}

class Screen_game_evening extends Screen_super {
	void onCreate(MainActivity boss){
	}
	int onClick(View v, MainActivity boss){
		return R.layout.game_youare;
	}
	void onDelete(MainActivity boss){
	}
}

class Screen_game_youare extends Screen_super {
	int now;	//現在の操作プレイヤー
	String name;
	MainActivity Boss;
	void onCreate(MainActivity boss){
		now = boss.globals.game.nextplayer();
		if( now != -1 ){
			name = boss.globals.players.get(now).name;
		}else{
			name = boss.getString(R.string.game_youare_gamemaster);
		}
		((TextView)boss.findViewById(R.id.PlayerName)).setText( name );
		Boss = boss;
	}
	int onClick(View v, MainActivity boss){
		new AlertDialog.Builder( boss )
			.setTitle( boss.getString(R.string.game_youare_1) + 
					name + 
					boss.getString(R.string.game_youare_2) )
        	.setPositiveButton(
        			"はい", 
        			new DialogInterface.OnClickListener() {
        				@Override
        				public void onClick(DialogInterface dialog, int which) {  
        					switch(now){
        					case -1:
        						Boss.changeScreen( R.layout.game_morning );
        						break;
        					default:
        						Boss.changeScreen( R.layout.game_night );
        					}
        				}
        			})
        	.setNegativeButton(
        			"いいえ", null ) 
        			//new DialogInterface.OnClickListener() {
        			//	@Override
        			//	public void onClick(DialogInterface dialog, int which) {  
        			//	}
        			//})
        	.show();
		return 0;	//何もしない
	}
	void onDelete(MainActivity boss){
	}
}

class Screen_game_night extends Screen_super {
	boolean action;	//行動したかどうか
	int now, role;
	void onCreate(MainActivity boss){
		now = boss.globals.game.nowplayer;
		role = boss.globals.players.get(now).role;
		//説明文
		((TextView)boss.findViewById(R.id.RoleName)).setText( boss.globals.roles.get(role).name );
		((TextView)boss.findViewById(R.id.textViewExplain)).setText( 
				boss.globals.roles.get(role).example + "\n\n" +
				boss.getString(R.string.edit_win) + "\n　　" +
				boss.globals.roles.get(role).string_win(boss) + "\n" +
				boss.getString(R.string.edit_skill) + "\n　　" +
				boss.globals.roles.get(role).string_skill(boss) + "\n" +
				boss.getString(R.string.edit_uranai) + "\n　　" +
				boss.globals.roles.get(role).string_uranai(boss) + "\n" +
				"\n"
				);
		((TextView)boss.findViewById(R.id.textViewSelect)).setText( 
				boss.globals.roles.get(role).string_action(boss) + boss.getString(R.string.game_night_select) );
		((TextView)boss.findViewById(R.id.button)).setText( boss.globals.roles.get(role).string_action(boss) );
		//スピナー設定
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(boss, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		while( boss.globals.game.nextplayer_spi() != -1 ){
			//アイテム追加
			adapter.add( boss.globals.players.get( boss.globals.game.nowplayer_spi ).name );
		}
		((Spinner)boss.findViewById(R.id.spinner)).setAdapter(adapter);	//アダプターを設定
        
		//変数初期化
		action = false;
	}
	int onClick(View v, MainActivity boss){
		// もう行動していたら次の人へ
		if( action ){
			return R.layout.game_youare;
		}
		
		//行動
		int vote = boss.globals.game.vote();
		switch( role ){
		case Globals.ROLE_MURA:
		case Globals.ROLE_JINRO:
			break;
		case Globals.ROLE_URANAI:
			int role_ura = boss.globals.players.get(vote).role;
			((TextView)boss.findViewById(R.id.textViewResult)).setText( 
					boss.globals.players.get(vote).name + " は、" +
					boss.globals.roles.get(role_ura).string_uranai(boss) );
			break;
		}
		
		((TextView)boss.findViewById(R.id.textViewResult)).setPadding(0, 30, 0, 60);
		((TextView)boss.findViewById(R.id.button)).setText( boss.getString(R.string.game_night_next) );
		action = true;
		
		return 0;
	}
	void onDelete(MainActivity boss){
	}
}

class Screen_game_morning extends Screen_super {
	int jinro_Target=-1, mura_Target=-1;
	void onCreate(MainActivity boss){
		int now, pNum = boss.globals.player_num;
		int jinro_num=0, jinro_max=0;
		int mura_num=0, mura_max=0;
		int[] target_jinro = new int[pNum];
		int[] target_mura = new int[pNum];
		Arrays.fill(target_jinro, 0);	//初期化
		Arrays.fill(target_mura, 0);	//初期化
		Random rand = new Random();	//乱数発生用
		while( (now = boss.globals.game.nextplayer()) != -1 ){
			int v = boss.globals.players.get(now).vote;
			switch( boss.globals.roles.get(boss.globals.players.get(now).role).skill ){
			case Globals.ROLE_MURA:
				target_mura[ v ]++;
				if( mura_max < target_mura[v] ){
					mura_max = target_mura[v];
					mura_num = 1;
				}else if( mura_max == target_mura[v] ){
					mura_num++;
				}
				break;
			case Globals.ROLE_JINRO:
				target_jinro[ v ]++;
				if( jinro_max < target_jinro[v] ){
					jinro_max = target_jinro[v];
					jinro_num = 1;
				}else if( jinro_max == target_jinro[v] ){
					jinro_num++;
				}
				break;
			}
		}
		//襲撃結果
		if( jinro_max > 0 ){
			for(int i=0; i<pNum; i++){
				if( target_jinro[i] == jinro_max &&
						rand.nextInt(jinro_num) == 0 ){
					jinro_Target = i;
					break;
				}
			}
		}
		if( jinro_Target != -1 ){
			boss.globals.players.get(jinro_Target).alive = false;
			((TextView)boss.findViewById(R.id.target_jinro)).setText( boss.globals.players.get(jinro_Target).name );
		}else{
			((TextView)boss.findViewById(R.id.target_jinro)).setText( "" );
			((TextView)boss.findViewById(R.id.textView_J)).setText( boss.getString(R.string.game_morning_zero) );
		}
		//疑われた人物
		if( mura_max > 0 ){
			for(int i=0; i<pNum; i++){
				if( target_mura[i] == mura_max &&
						rand.nextInt(mura_num) == 0 ){
					mura_Target = i;
					break;
				}
			}
		}
		if( mura_Target != -1 ){
			((TextView)boss.findViewById(R.id.target_mura)).setText( boss.globals.players.get(mura_Target).name );
		}else{
			((TextView)boss.findViewById(R.id.target_mura)).setText( "" );
			((TextView)boss.findViewById(R.id.textView_M)).setText( boss.getString(R.string.game_morning_zero) );
		}
		
		
		//日付更新
		boss.globals.game.day++;
	}
	int onClick(View v, MainActivity boss){
		// 勝利判定
		if( boss.globals.game.judge() ){
			return R.layout.game_ending;
		}
		return R.layout.game_day;
	}
	void onDelete(MainActivity boss){
	}
}

class Screen_game_day extends Screen_super {
	Timer timer = null;
	int limittime, time_sec, time_min, old_sec=-1;
	long starttime, nowtime;
	MainActivity Boss;
	void onCreate(MainActivity boss){
		//starttime = System.currentTimeMillis();	//開始時刻保存
		limittime = boss.getResources().getInteger(R.integer.INITIAL_TIMER);
		Boss = boss;	//TimerTaskで使うため保存
		//タイマー
		timer = new Timer();
		timer.schedule( new TimerTask(){
	        @Override
	        public void run() {
            	//nowtime = System.currentTimeMillis();
            	//time_sec = limittime - (int)(nowtime - starttime) / 1000;
	        	time_sec = limittime--;
            	if( time_sec <= 0 ){
            		timer.cancel();
            		timer = null;
            		time_sec = 0;
            	}
            	time_min = time_sec / 60;
            	time_sec -= time_min * 60;
            	//if( time_sec != old_sec ){
            		//old_sec = time_sec;
            		// Activityに関わる処理はhandlerに投げる
            		Boss.handle.post( new Runnable() {
            			public void run() {
            				((TextView)Boss.findViewById(R.id.textViewTimer)).setText( time_min + ":" + String.format( "%02d", time_sec ) );
            			}
            		});
            	//}
	        }
	    }, 0, 1000 );
	}
	int onClick(View v, MainActivity boss){
		if( timer != null ){
			timer.cancel();
			timer = null;
		}
		return R.layout.game_vote;
	}
	void onDelete(MainActivity boss){
		//投票へ移る
		boss.globals.game.nextplayer();
	}
}

class Screen_game_vote extends Screen_super {
	boolean action;	//投票したかどうか
	void onCreate(MainActivity boss){
		//投票者名
		((TextView)boss.findViewById(R.id.PlayerName)).setText( boss.globals.players.get(boss.globals.game.nowplayer).name );
		//スピナー設定
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(boss, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		while( boss.globals.game.nextplayer_spi() != -1 ){
			//アイテム追加
			adapter.add( boss.globals.players.get( boss.globals.game.nowplayer_spi ).name );
		}
		((Spinner)boss.findViewById(R.id.spinner)).setAdapter(adapter);	//アダプターを設定      

		//初期padding
		((TextView)boss.findViewById(R.id.textViewResult)).setPadding(0, 0, 0, 0);
		((TextView)boss.findViewById(R.id.textViewWhite)).setPadding(0, 60, 0, 60);
		//初期化
		action=false;
	}
	int onClick(View v, MainActivity boss){
		//投票し終わっていたら次へ
		if( action ){
			int now = boss.globals.game.nextplayer();
			if( now == -1 ){
				return R.layout.game_vote_result;
			}else{
				return R.layout.game_vote;
			}
		}
		//投票
		boss.globals.game.vote();
		((TextView)boss.findViewById(R.id.textViewResult)).setText("投票しました。");
		((TextView)boss.findViewById(R.id.textViewResult)).setPadding(0, 60, 0, 60);
		((TextView)boss.findViewById(R.id.textViewWhite)).setPadding(0, 0, 0, 0);
		((TextView)boss.findViewById(R.id.button)).setText( boss.getString(R.string.game_night_next) );
		action = true;
		return 0;
	}
	void onDelete(MainActivity boss){
	}
}

class Screen_game_vote_result extends Screen_super {
	int Target=-1;
	void onCreate(MainActivity boss){
		int now, pNum = boss.globals.player_num;
		int mura_num=0, mura_max=0;
		int[] target_mura = new int[pNum];
		Arrays.fill(target_mura, 0);	//初期化
		Random rand = new Random();	//乱数発生用
		while( (now = boss.globals.game.nextplayer()) != -1 ){
			int v = boss.globals.players.get(now).vote;
			target_mura[ v ]++;
			if( mura_max < target_mura[v] ){
				mura_max = target_mura[v];
				mura_num = 1;
			}else if( mura_max == target_mura[v] ){
				mura_num++;
			}
		}
		//結果
		for(int i=0; i<pNum; i++){
			if( target_mura[i] == mura_max &&
					rand.nextInt(mura_num) == 0 ){
				Target = i;
				break;
			}
		}
		boss.globals.players.get(Target).alive = false;
		//表示
		((TextView)boss.findViewById(R.id.PlayerName)).setText( boss.globals.players.get(Target).name );
	}
	int onClick(View v, MainActivity boss){
		// 勝利判定
		if( boss.globals.game.judge() ){
			return R.layout.game_ending;
		}
		return R.layout.game_evening;
	}
	void onDelete(MainActivity boss){
	}
}

class Screen_game_ending extends Screen_super {
	void onCreate(MainActivity boss){
		//結果を表示する
		String allRole = new String();
		allRole += "\n";
		//勝利陣営の表示
		switch( boss.globals.game.winner ){
		case Globals.ROLE_MURA:
			allRole += boss.getString(R.string.edit_win_mura);
			break;
		case Globals.ROLE_JINRO:
			allRole += boss.getString(R.string.edit_win_jinro);
			break;
		}
		allRole += "\n\n";
		//参加者の役職表示
		for(int i=0; i<boss.globals.player_num; i++){
			allRole += boss.globals.players.get(i).name + "\t" +
						boss.globals.roles.get(boss.globals.players.get(i).role).name + "\n";
		}
		((TextView)boss.findViewById(R.id.textView)).setText( allRole );
	}
	int onClick(View v, MainActivity boss){
		return R.layout.activity_main;
	}
	void onDelete(MainActivity boss){
	}
}


//画面切り替えをスムーズにするクラス
public class Screen {
	Screen_super S;
	SparseArray<Screen_super> sHash = new SparseArray<Screen_super>();

	void onCreate(){
		sHash.put(R.layout.activity_main, new Screen_title());
		sHash.put(R.layout.edit, new Screen_edit());
		sHash.put(R.layout.regist_name, new Screen_regist_name());
		sHash.put(R.layout.regist_role, new Screen_regist_role());
		sHash.put(R.layout.game_opening, new Screen_game_opening());
		sHash.put(R.layout.game_evening, new Screen_game_evening());
		sHash.put(R.layout.game_youare, new Screen_game_youare());
		sHash.put(R.layout.game_night, new Screen_game_night());
		sHash.put(R.layout.game_morning, new Screen_game_morning());
		sHash.put(R.layout.game_day, new Screen_game_day());
		sHash.put(R.layout.game_vote, new Screen_game_vote());
		sHash.put(R.layout.game_vote_result, new Screen_game_vote_result());
		sHash.put(R.layout.game_ending, new Screen_game_ending());
	}
	
	int onClick(View v, MainActivity boss){
		return S.onClick(v, boss);
	}
	
	void change(int id, MainActivity boss){
		S = sHash.get(id);
		S.onCreate(boss);
	}
	
	void onDelete(MainActivity boss){
		if( S != null )
			S.onDelete(boss);
	}
}
