package com.masashi.tf12_tictactoe;

import android.graphics.Color;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // それぞれのプレイヤーがボードに置く画像
    public static final int[] PLAYER_IMAGES = {R.drawable.icon_batsu, R.drawable.icon_maru};

    // ターン数を数える変数
    // プレイヤーも管理する
    public int turn;

    // ゲームの盤面
    // まだ誰も選択してないときは -1
    public int[] gameBoard;

    // 実際に見えているゲームの盤面; ボタンの配列
    public ImageButton[] boardButtons;

    // プレイヤーとターン表示用のTextView
    public TextView playerTextView;

    //　勝利数表示用のTextView
    public TextView winnerTextView;

    // ボタンにつけたidをまとめておく
    public int[] buttonIDs = {R.id.imageButton1, R.id.imageButton2, R.id.imageButton3,
                            R.id.imageButton4, R.id.imageButton5, R.id.imageButton6,
                            R.id.imageButton7, R.id.imageButton8, R.id.imageButton9};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ひもづけ
        playerTextView = findViewById(R.id.playerText);
        winnerTextView = findViewById(R.id.winnerText);

        // ボタンの配列を用意する
        boardButtons = new ImageButton[9];
        // ImageButtonをidを使って関連付けする
        for (int i = 0; i < boardButtons.length; i++) {
            boardButtons[i] = (ImageButton) findViewById(buttonIDs[i]);
        }

        init();
        setPlayer();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_menu_reset) {
            init();
            setPlayer();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void init() {
        // 編集の初期化
        turn = 1;
        // 現在のゲームボードの初期化
        gameBoard = new int[boardButtons.length];
        for (int i = 0; i< boardButtons.length; i++) {
            // 誰もそのマスを撮ってないときは-1を入れるようにする
            gameBoard[i] = -1;
            // ImageButtonで表示している画像を消す
            boardButtons[i].setImageBitmap(null);
        }

        // 勝敗の表示用のTextViewは見えないようにする
        winnerTextView.setVisibility(View.GONE);
    }

    public void setPlayer() {
        if (turn % 2 == 0) {
            playerTextView.setText("Player: ✖(2)");
        } else {
            playerTextView.setText("Player: ○(1)");
        }
    }

    public void tapImageButton(View view) {
        // 勝敗が画面に出ていない時だけ処理を行うようにする
        if (winnerTextView.getVisibility() == View.VISIBLE) return;

        // どのボタンが押されたのかを取得する
        int tappedButtonPosition;
        int viewId = view.getId();

        if (viewId == R.id.imageButton1) {
            tappedButtonPosition = 0;
        } else if (viewId == R.id.imageButton2) {
            tappedButtonPosition = 1;
        } else if (viewId == R.id.imageButton3) {
            tappedButtonPosition = 2;
        } else if (viewId == R.id.imageButton4) {
            tappedButtonPosition = 3;
        } else if (viewId == R.id.imageButton5) {
            tappedButtonPosition = 4;
        } else if (viewId == R.id.imageButton6) {
            tappedButtonPosition = 5;
        } else if (viewId == R.id.imageButton7) {
            tappedButtonPosition = 6;
        } else if (viewId == R.id.imageButton8) {
            tappedButtonPosition = 7;
        } else { // viewId == R.id.imageButton9
            tappedButtonPosition = 8;
        }


        // 誰もそのマスを取ってないことを確認する
        if (gameBoard[tappedButtonPosition] == -1) {
            // そのターンでプレイヤーの画像を押されたマスにセットする
            boardButtons[tappedButtonPosition].setImageResource(PLAYER_IMAGES[turn % 2]);
            gameBoard[tappedButtonPosition] = turn % 2;

            // 勝数がついたかを判定する
            int judge = judgeGame();

            // judgeの値-1だったら勝敗がついていない
            // judgeの値が1だったら○のプレイヤーの勝利
            // judgeの値が0だったら✖のプレイヤーの勝利
            if (judge != -1) { // 勝負が決まった場合
                if (judge == 0) {
                    winnerTextView.setText("Game End\nPlayer: ×(2)\nWin");
                    winnerTextView.setTextColor(Color.RED);
                } else { // judge == 1 の時を想定
                    winnerTextView.setText("Game End\nPlayer: ◯(1)\nWin");
                    winnerTextView.setTextColor(Color.BLUE);
                }

                winnerTextView.setVisibility(View.VISIBLE);

            } else { // 全部の場所が埋まっても、勝負がつかなかった場合（引き分け）の確認
                if (turn >= gameBoard.length) {
                    winnerTextView.setText("Game End\nDraw");
                    winnerTextView.setTextColor(Color.YELLOW);
                    winnerTextView.setVisibility(View.VISIBLE);
                }
            }

            turn++;

            setPlayer();
        }
    }

    public int judgeGame() {
        for (int i = 0; i < 3; i++) { // 3列
            // 横並びをチェック
            if (isMarkedHorizontal(i)) {
                return gameBoard[i * 3]; // そのマスをとった人の値を返す(0か1)
            }
            // 縦の並びをチェック
            if (isMarkedVertical(i)) {
                return gameBoard[i];
            }
        }

        // 斜めのチェック
        if (isMarkedDiagonal()) {
            return gameBoard[4];
        }

        return -1; // チェックした結果、まだ勝負がついていない場合
    }

    // そのマスが誰かに撮られている（-1ではない）
    // かつ、同じ人が撮っている（i*3と,i*3+1,i*3+2の値が同じ）
    public boolean isMarkedHorizontal(int i) {
        if (gameBoard[i * 3] != -1 && gameBoard[i * 3] == gameBoard[i * 3 + 1] && gameBoard[i * 3] == gameBoard[i * 3 + 2]) {
            return  true;
        } else {
            return false;
        }
    }

    public boolean isMarkedVertical(int i) {
        if (gameBoard[i] != -1 && gameBoard[i] == gameBoard[i + 3] && gameBoard[i] == gameBoard[i + 6]) {
            return  true;
        } else {
            return false;
        }
    }

    public boolean isMarkedDiagonal() {
        if (gameBoard[0] != -1 && gameBoard[0] == gameBoard[4] && gameBoard[0] == gameBoard[8]) { // 左上から右下
            return true;
        } else if (gameBoard[2] != -1 && gameBoard[2] == gameBoard[4] && gameBoard[2] == gameBoard[6]) { // 右上から左下
            return true;
        } else {
            return false;
        }
    }
}
