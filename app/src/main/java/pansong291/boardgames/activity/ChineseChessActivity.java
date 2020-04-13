package pansong291.boardgames.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import pansong291.boardgames.R;
import pansong291.boardgames.view.ChineseChessView;

public class ChineseChessActivity extends Zactivity implements ChineseChessView.OnPieceDownListener, ChineseChessView.OnWinnerListener
{
  TextView txt_current, txt_step;
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
    txt_step = findViewById(R.id.txt_step);
    chinese_chess_view = findViewById(R.id.chinese_chess_view);
    setPieceHint(Color.RED);
    chinese_chess_view.setOnPieceDownListener(this);
    chinese_chess_view.setOnWinnerListener(this);
  }

  @Override
  public void onPieceDown(ChineseChessView.Step s, int cur)
  {
    setStepHint(s);
    txt_step.setTextColor(cur);
  }

  @Override
  public void afterPieceDown(int cur)
  {
    setPieceHint(cur);
  }

  @Override
  public void onWinner(int cur)
  {
    toast((cur == Color.BLACK ? "黑方": "红方") + " 胜 !");
  }

  public void onRetractBtnClick(View v)
  {
    txt_step.setTextColor(chinese_chess_view.getCurrent());
    ChineseChessView.Step stp = chinese_chess_view.retract();
    setPieceHint(chinese_chess_view.getCurrent());
    setStepHint(stp);
  }

  public void onResetBtnClick(View v)
  {
    chinese_chess_view.reset();
    setPieceHint(chinese_chess_view.getCurrent());
    setStepHint(null);
  }

  private void setPieceHint(int cur)
  {
    txt_current.setText(cur == Color.BLACK ? "黑方": "红方");
    txt_current.setTextColor(cur);
  }

  private void setStepHint(ChineseChessView.Step stp)
  {
    txt_step.setText(stp == null ? "": stp.stepName);
  }

}
