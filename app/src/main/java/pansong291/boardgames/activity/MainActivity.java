package pansong291.boardgames.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import pansong291.boardgames.R;

public class MainActivity extends Zactivity 
{
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  public void onBtnClick(View v)
  {
    Intent it;
    switch(v.getId())
    {
      case R.id.btn_gobang:
        it = new Intent(this, GobangActivity.class);
        startActivity(it);
        break;

      case R.id.btn_chinese_chess:
        it = new Intent(this, ChineseChessActivity.class);
        startActivity(it);
        break;
    }
  }

}
