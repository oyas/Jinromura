package com.jp.oyas.Jinromura;

import android.app.Application;
//import android.widget.Toast;

import java.util.ArrayList;

// グローバル変数定義用
public class Globals extends Application {
	
	// 定数定義
	static final int ROLE_MURA = 0;
	static final int ROLE_JINRO = 1;
	static final int ROLE_URANAI = 2;
	static final int ROLE_KARIUDO = 3;
	static final int ROLE_REIBAI = 4;
	
	//参加者
	class murabito {
		String name;
		int role;	//役職
		boolean alive;	//生死 true:生 false:死
		int vote;	//投票先、または夜時間の選択先
		murabito(String Name){
			name = Name;
			role = ROLE_MURA;
			alive = true;
			vote = 0;
		}
	}
		
	//役職
	class role {
		String name, example;	//役職名、その説明
		int win;	//勝利条件。0:村人陣営 1:人狼陣営
		int skill;	//夜に使える能力。 0:なし 1:人狼 2:占い師 3:狩人 4:霊媒師
		int uranai;	//占い結果。 0:not人狼 1:人狼 2:占い師	

		int num;	//割り当てられた人数
	
		// 説明文を返す
		String string_win(MainActivity boss) {
			switch( win ){
			case ROLE_MURA:
				return boss.getString(R.string.edit_win_mura);
			case ROLE_JINRO:
				return boss.getString(R.string.edit_win_jinro);
			}
			return null;
		}
		String string_skill(MainActivity boss) {
			switch( skill ){
			case ROLE_MURA:
				return boss.getString(R.string.edit_skill_mura);
			case ROLE_JINRO:
				return boss.getString(R.string.edit_skill_jinro);
			case ROLE_URANAI:
				return boss.getString(R.string.edit_skill_uranai);
			case ROLE_KARIUDO:
				return boss.getString(R.string.edit_skill_kariudo);
			case ROLE_REIBAI:
				return boss.getString(R.string.edit_skill_reibai);
			}
			return null;
		}
		String string_uranai(MainActivity boss) {
			switch( uranai ){
			case ROLE_MURA:
				return boss.getString(R.string.edit_uranai_mura);
			case ROLE_JINRO:
				return boss.getString(R.string.edit_uranai_jinro);
			case ROLE_URANAI:
				return boss.getString(R.string.edit_uranai_uranai);
			}
			return null;
		}
		String string_action(MainActivity boss){
			switch( skill ){
			case ROLE_MURA:
				return boss.getString(R.string.game_night_utagau);
			case ROLE_JINRO:
				return boss.getString(R.string.game_night_osou);
			case ROLE_URANAI:
				return boss.getString(R.string.game_night_uranau);
			case ROLE_KARIUDO:
				return boss.getString(R.string.game_night_mamoru);
			case ROLE_REIBAI:
				return boss.getString(R.string.game_night_uranau);
			}
			return null;
		}
		
		//初期設定
		role(){
			win = ROLE_MURA;
			skill = ROLE_MURA;
			uranai = ROLE_MURA;
		}
	}
	
	// ゲームの進行状況
	class gameClass{
		int day;	//何日目か
		int nowplayer;	//今操作しているプレイヤー
		int nowplayer_spi;	//スピナー用
		int winner;	//勝利陣営 -1:まだ勝敗がついていない
		
		//参加者追加
		void addPlayer(String name){
			players.add( new murabito(name) );
		}
		
		//ゲーム開始時の初期化
		void start(){
			day = 0;
			nowplayer = -1;
			nowplayer_spi = -1;
			winner = -1;
			for(int i=0; i<player_num; i++){
				players.get(i).alive = true;
			}
		}
		
		//次の人を返す。-1のときは１周したということ
		int nextplayer(){
			while(true){
				nowplayer++;
				if( nowplayer >= player_num ){
					nowplayer = -1;	//1周した
					break;
				}
				if( players.get(nowplayer).alive ){
					break;
				}
			}
			return nowplayer;
		}
		
		//nowplayer()のスピナー版
		int nextplayer_spi(){
			while(true){
				nowplayer_spi++;
				if( nowplayer_spi >= player_num ){
					nowplayer_spi = -1;	//1周した
					break;
				}
				if( nowplayer_spi == nowplayer ){
					continue;
				}
				if( players.get(nowplayer_spi).alive ){
					break;
				}
			}
			return nowplayer_spi;
		}
		
		//スピナーの選択を確定（投票など）
		int vote(){
			for(int i = 0; i <= spinnerSelected; i++){
				nextplayer_spi();
			}
			players.get(nowplayer).vote = nowplayer_spi;
			nowplayer_spi = -1;
			return players.get(nowplayer).vote;
		}
		
		//勝敗判定 勝敗がついていればtrueを返す
		boolean judge(){
			int mura=0, jinro=0;
			while( nextplayer() > -1 ){
				if( roles.get(players.get(nowplayer).role).skill == ROLE_JINRO ){
					jinro++;
				}else{
					mura++;
				}
			}
			//Toast.makeText(boss, "mura=" + mura + "\njinro=" + jinro, Toast.LENGTH_SHORT).show();
			if( jinro == 0 ){
				winner = ROLE_MURA;
			}else if( mura <= jinro ){
				winner = ROLE_JINRO;
			}else{
				return false;
			}
			return true;
		}
		
		gameClass(){
			start();
		}
	}
	
	// グローバル変数
	int player_num;	//参加者の人数
	ArrayList<role> roles;
	ArrayList<murabito> players;
	gameClass game;
	int spinnerSelected;	//スピナーで現在選択されている項目

	//MainActivity boss;	//デバッグ用
	
	
	//コンストラクタもどき(親のコンストラクタをオーバーライドすると親のコンストラクタが呼ばれず面倒なため)
	void CreateGlobals(){
		player_num = 0;
		roles = new ArrayList<role>();
		players = new ArrayList<murabito>();
		game = new gameClass();
		
		////////////////////////////////////
		// 応急処置(役職決め打ち)
		// 村民 ROLE_MURA=0
		roles.add( new role() );
		roles.get(0).name = "村民";
		roles.get(0).example = "ただの村民です。";

		//人狼 ROLE_JINRO=1
		roles.add( new role() );
		roles.get(1).name = "人狼";
		roles.get(1).example = "普段は村民のふりをしていますが、夜中に人を食べます。";
		roles.get(1).win = ROLE_JINRO;
		roles.get(1).skill = ROLE_JINRO;
		roles.get(1).uranai = ROLE_JINRO;

		//占い師 ROLE_URANAI=2
		roles.add( new role() );
		roles.get(2).name = "占い師";
		roles.get(2).example = "夜時間に一人占うことができ、その人が人狼かどうか知ることができます。";
		roles.get(2).skill = ROLE_URANAI;
		roles.get(2).uranai = ROLE_URANAI;

		//狂人
		roles.add( new role() );
		roles.get(3).name = "狂人";
		roles.get(3).example = "人狼の味方の村民です。";
		roles.get(3).win = ROLE_JINRO;

		//狩人
/*
		roles.add( new role() );
		roles.get(4).name = "狩人";
		roles.get(4).example = "夜時間に人狼から村人を一人守ることができます。";
		roles.get(4).skill = ROLE_KARIUDO;
*/
		//霊媒師
/*
		roles.add( new role() );
		roles.get(5).name = "霊媒師";
		roles.get(5).example = "夜時間にその日の昼に処刑された人が、人狼だったかどうか占えます。";
		roles.get(5).skill = ROLE_REIBAI;
*/		
	}
}
