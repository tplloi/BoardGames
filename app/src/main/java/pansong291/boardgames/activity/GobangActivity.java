package pansong291.boardgames.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import pansong291.boardgames.R;
import pansong291.boardgames.view.GobangView;
import pansong291.boardgames.view.GobangView.BoardState;

public class GobangActivity extends Zactivity implements GobangView.OnPieceDownListener, GobangView.OnWinnerListener
{
  TextView txt_current;
  GobangView gobang_view;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gobang);

    initView();
  }

  private void initView()
  {
    txt_current = findViewById(R.id.txt_current);
    gobang_view = findViewById(R.id.gobang_view);
    setPieceHint(gobang_view.getCurrent());
    gobang_view.setOnPieceDownListener(this);
    gobang_view.setOnWinnerListener(this);
  }

  @Override
  public void onPieceDown(int x, int y, BoardState bs)
  {
    // TODO: Implement this method
  }

  @Override
  public void afterPieceDown(BoardState bs)
  {
    setPieceHint(bs);
  }

  @Override
  public void onWinner(BoardState bs)
  {
    if(bs == null)
      toast("DRAW !");
    else
      toast(bs.toString() + " WIN !");
  }

  public void onRetractBtnClick(View v)
  {
    gobang_view.retract();
    setPieceHint(gobang_view.getCurrent());
  }

  public void onResetBtnClick(View v)
  {
    gobang_view.reset();
    setPieceHint(gobang_view.getCurrent());
  }

  private void setPieceHint(BoardState bs)
  {
    txt_current.setText(bs.toString());
    txt_current.setTextColor(bs.getColor());
  }

}
