package pansong291.boardgames.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import pansong291.boardgames.R;
import pansong291.boardgames.view.ChineseChessView;

public class ChineseChessActivity extends Zactivity implements ChineseChessView.OnPieceDownListener
{
  TextView txt_current;
  ChineseChessView chinese_chess_view;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_chinese_chess);

    initView();
  }

  private void initView()
  {
    txt_current = findViewById(R.id.txt_current);
    chinese_chess_view = findViewById(R.id.chinese_chess_view);
    setPieceHint(Color.RED);
    chinese_chess_view.setOnPieceDownListener(this);
    //gobang_view.setOnWinnerListener(this);
  }

  @Override
  public void onPieceDown(int x, int y, int cur)
  {
    // TODO: Implement this method
  }

  @Override
  public void afterPieceDown(int cur)
  {
    setPieceHint(cur);
  }

//  @Override
//  public void onWinner(BoardState bs)
//  {
//    if(bs == null || bs == BoardState.EMPTY)
//      toast("DRAW !");
//    else
//      toast(bs.toString() + " WIN !");
//  }

  public void onRetractBtnClick(View v)
  {
    chinese_chess_view.retract();
    setPieceHint(chinese_chess_view.getCurrent());
  }

  public void onResetBtnClick(View v)
  {
    chinese_chess_view.reset();
    setPieceHint(chinese_chess_view.getCurrent());
  }

  private void setPieceHint(int cur)
  {
    String s = cur == Color.BLACK ? "黑方": "红方";
    txt_current.setText(s);
    txt_current.setTextColor(cur);
  }

}
