package pansong291.boardgames.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import java.util.Stack;

public class GobangView extends View
{
  int board_row; // 棋盘行数
  int board_column; //棋盘列数
  int board_total; //棋盘总格数
  BoardState[][] board_state; //棋盘状态数据

  int board_background_color; //棋盘背景颜色
  float board_left_x; //棋盘左端x值
  float board_right_x; //棋盘右端x值
  float board_top_y; //棋盘顶端y值
  float board_bottom_y; //棋盘底端y值
  int board_line_color; //棋盘线颜色
  float board_line_stroke; //棋盘线厚度
  float board_spacing; //棋盘线间隔
  Paint board_paint; //棋盘画笔

  float piece_radius; //棋子半径
  Paint piece_black_paint; //黑子画笔
  Paint piece_white_paint; //白子画笔

  boolean calculated; //是否计算过尺寸

  OnPieceDownListener onPieceDownListener; //落子监听

  BoardState current; //当前落子方
  Point touch_point; //触点
  Stack<Point> history; //历史记录
  boolean hasWinner; //是否产生赢家
  Point winner_start_point, winner_end_point; //赢家棋子始末
  int winner_line_color; //赢家连线颜色
  float winner_line_stroke; //赢家连线厚度
  Paint winner_line_paint; //赢家连线画笔

  OnWinnerListener onWinnerListener; // 赢家监听

  public GobangView(Context c)
  {
    super(c);
    defInit();
  }

  public GobangView(Context c, AttributeSet a)
  {
    super(c, a);
    defInit();
  }

  public GobangView(Context c, AttributeSet a, int s)
  {
    super(c, a, s);
    defInit();
  }

  /*
   * 重置
   */
  public void reset()
  {
    if(history.empty()) return;
    hasWinner = false;
    history.clear();
    for(int i = 0; i < board_row; i++)
    {
      for(int j = 0; j < board_column; j++)
      {
        board_state[i][j] = null;
      }
    }
    current = BoardState.BLACK;
    postInvalidate();
  } //reset

  /*
   * 悔棋
   */
  public void retract()
  {
    if(history.empty()) return;
    hasWinner = false;
    touch_point = history.pop();
    board_state[touch_point.x][touch_point.y] = null;
    switchCurrent();
    postInvalidate();
  } //retract

  /*
   * 获取当前落子方
   */
  public BoardState getCurrent()
  {
    return current;
  }

  public void setOnPieceDownListener(OnPieceDownListener listener)
  {
    onPieceDownListener = listener;
  }

  public void setOnWinnerListener(OnWinnerListener listener)
  {
    onWinnerListener = listener;
  }

  private void defInit()
  {
    board_row = 15;
    board_column = 15;
    board_total = board_row * board_column;
    board_state = new BoardState[board_row][board_column];

    board_background_color = Color.GRAY;
    board_line_color = Color.BLACK;
    board_line_stroke = 2;
    board_paint = new Paint();
    board_paint.setColor(board_line_color);
    board_paint.setStrokeWidth(board_line_stroke);
    board_paint.setStrokeCap(Paint.Cap.SQUARE);

    piece_black_paint = new Paint();
    piece_black_paint.setColor(Color.BLACK);

    piece_white_paint = new Paint();
    piece_white_paint.setColor(Color.WHITE);

    current = BoardState.BLACK;
    touch_point = new Point();
    history = new Stack<>();
    winner_start_point = new Point();
    winner_end_point = new Point();
    winner_line_color = Color.RED;
    winner_line_stroke = 10;
    winner_line_paint = new Paint();
    winner_line_paint.setColor(winner_line_color);
    winner_line_paint.setStrokeWidth(winner_line_stroke);
    winner_line_paint.setStrokeCap(Paint.Cap.ROUND);
  } //defInit

  /*
   * 计算尺寸数据
   */
  private void calculate()
  {
    if(calculated) return;
    float w = getWidth();
    float h = getHeight();
    if(w < h)
    {
      // 四周各留一个间隔
      board_spacing = w / (board_column + 1);
      board_left_x = board_spacing;
      board_top_y = (h - (board_row - 1) * board_spacing) / 2;
    }else
    {
      // 四周各留一个间隔
      board_spacing = h / (board_row + 1);
      board_top_y = board_spacing;
      board_left_x = (w - (board_column - 1) * board_spacing) / 2;
    }
    board_right_x = w - board_left_x;
    board_bottom_y = h - board_top_y;

    // 半径为间隔一半，再乘以0.85
    piece_radius = board_spacing * 85 / 200;

    calculated = true;
  } //calculate

  /*
   * 绘制棋盘
   */
  private void drawBoard(Canvas canvas)
  {
    //绘制背景色
    canvas.drawColor(board_background_color);

    //绘制横线
    for(int i = 0; i < board_row; i++)
      canvas.drawLine(board_left_x, board_top_y + i * board_spacing,
                      board_right_x, board_top_y + i * board_spacing, board_paint);

    //绘制竖线
    for(int i = 0; i < board_column; i++)
      canvas.drawLine(board_left_x + i * board_spacing, board_top_y,
                      board_left_x + i * board_spacing, board_bottom_y, board_paint);

  } //drawBoard

  /*
   * 绘制棋子
   */
  private void drawPiece(Canvas canvas)
  {
    Paint p = null;
    for(int i = 0; i < board_row; i++)
    {
      for(int j = 0; j < board_column; j++)
      {
        switch(board_state[i][j])
        {
          case BLACK:
            p = piece_black_paint;
            break;

          case WHITE:
            p = piece_white_paint;
            break;

          default:
            p = null;
        }
        if(p != null)
          canvas.drawCircle(board_left_x + j * board_spacing,
                            board_top_y + i * board_spacing,
                            piece_radius, p);
      }
    }
  } //drawPiece

  /*
   * 绘制赢家棋子连线
   */
  private void drawWinnerLine(Canvas canvas)
  {
    if(hasWinner)
      canvas.drawLine(board_left_x + winner_start_point.y * board_spacing,
                      board_top_y + winner_start_point.x * board_spacing,
                      board_left_x + winner_end_point.y * board_spacing,
                      board_top_y + winner_end_point.x * board_spacing,
                      winner_line_paint);
  }

  @Override
  protected void onDraw(Canvas canvas)
  {
    calculate();
    drawBoard(canvas);
    drawPiece(canvas);
    drawWinnerLine(canvas);
  } //onDraw

  @Override
  public boolean onTouchEvent(MotionEvent event)
  {
    // 已诞生赢家的情况下不能再落子
    if(hasWinner) return true;
    float x = event.getX(), y = event.getY();
    int rx, ry;
    switch(event.getAction())
    {
      case MotionEvent.ACTION_DOWN:
        // 横坐标转为列数
        ry = point2Row(x - board_left_x);
        // 纵坐标转为行数
        rx = point2Row(y - board_top_y);
        touch_point.set(rx, ry);
        break;

      case MotionEvent.ACTION_UP:
        // 横坐标转为列数
        ry = point2Row(x - board_left_x);
        // 纵坐标转为行数
        rx = point2Row(y - board_top_y);
        // 出界判断
        if(isOutside(rx, ry)) break;
        if(touch_point.equals(rx, ry) && board_state[rx][ry] == null)
        {
          // 在该位置落子
          board_state[rx][ry] = current;
          // 存入历史记录
          history.push(new Point(touch_point));

          // 赢家判定
          hasWinner = isWinner(rx, ry);
          if(hasWinner)
          {
            if(onWinnerListener != null)
              onWinnerListener.onWinner(current);
          }else if(history.size() == board_total)
          {
            // 和局判定
            if(onWinnerListener != null)
              onWinnerListener.onWinner(null);
          }
          postInvalidate();

          if(onPieceDownListener != null)
            onPieceDownListener.onPieceDown(touch_point.x, touch_point.y, current);

          // 切换落子方
          switchCurrent();
          if(onPieceDownListener != null)
            onPieceDownListener.afterPieceDown(current);
        }
        break;
    }
    return true;
  } //onTouchEvent

  /*
   * 触点坐标差转为行列
   */
  private int point2Row(float offset)
  {
    int r;
    float s = offset / board_spacing;
    float y = offset % board_spacing;
    if(y <= board_spacing / 2)
      r = (int)s;
    else
      r = (int)(s + 1);
    return r;
  } //point2Row

  /*
   * 赢家判定
   */
  private boolean isWinner(int x, int y)
  {
    int count = 4; //除了落点以外还有4个棋子
    int sc = 0; //始方向有几个同色棋子
    int ec = 0; //末方向有几个同色棋子
    // 先往左，再往右
    for(sc = 0; !isOutside(x, y - sc - 1) && board_state[x][y - sc - 1] == current; sc ++);
    for(ec = 0; ec < count - sc; ec++)
      if(isOutside(x, y + ec + 1) || board_state[x][y + ec + 1] != current)
        break;
    if(sc + ec == count)
    {
      winner_start_point.set(x, y - sc);
      winner_end_point.set(x, y + ec);
      return true;
    }

    // 先往上，再往下
    for(sc = 0; !isOutside(x - sc - 1, y) && board_state[x - sc - 1][y] == current; sc++);
    for(ec = 0; ec < count - sc; ec++)
      if(isOutside(x + ec + 1, y) || board_state[x + ec + 1][y] != current)
        break;
    if(sc + ec == count)
    {
      winner_start_point.set(x - sc, y);
      winner_end_point.set(x + ec, y);
      return true;
    }

    // 先往左上，再往右下
    for(sc = 0; !isOutside(x - sc - 1, y - sc - 1) && board_state[x - sc - 1][y - sc - 1] == current; sc++);
    for(ec = 0; ec < count - sc; ec++)
      if(isOutside(x + ec + 1, y + ec + 1) || board_state[x + ec + 1][y + ec + 1] != current)
        break;
    if(sc + ec == count)
    {
      winner_start_point.set(x - sc, y - sc);
      winner_end_point.set(x + ec, y + ec);
      return true;
    }

    // 先往左下，再往右上
    for(sc = 0; !isOutside(x + sc + 1, y - sc - 1) && board_state[x + sc + 1][y - sc - 1] == current; sc++);
    for(ec = 0; ec < count - sc; ec++)
      if(isOutside(x - ec - 1, y + ec + 1) || board_state[x - ec - 1][y + ec + 1] != current)
        break;
    if(sc + ec == count)
    {
      winner_start_point.set(x + sc, y - sc);
      winner_end_point.set(x - ec, y + ec);
      return true;
    }
    return false;
  } //isWinner

  /*
   * 是否出界
   */
  private boolean isOutside(int x, int y)
  {
    if(x < 0 || x >= board_row || y < 0 || y >= board_column)
      return true;
    return false;
  }

  /*
   * 切换落子方
   */
  private void switchCurrent()
  {
    switch(current)
    {
      case BLACK:
        current = BoardState.WHITE;
        break;

      case WHITE:
        current = BoardState.BLACK;
        break;
    }
  } //switchCurrent

  /*
   * 棋盘上的状态
   */
  public enum BoardState
  {
    BLACK,WHITE;

    public int getColor()
    {
      int c = 0;
      switch(this)
      {
        case BLACK:
          c = Color.BLACK;
          break;

        case WHITE:
          c = Color.WHITE;
          break;
      }
      return c;
    } //getColor
  }

  /*
   * 落子监听器
   */
  public interface OnPieceDownListener
  {
    /*
     * 参数x和y表示落子位置，bs为该子颜色
     */
    void onPieceDown(int x, int y, BoardState bs);

    /*
     * bs为下次要落子的颜色
     */
    void afterPieceDown(BoardState bs);
  }

  /*
   * 赢家监听器
   */
  public interface OnWinnerListener
  {
    /*
     * 产生赢家
     */
    void onWinner(BoardState bs);
  }

  void toast(Object o)
  {
    Toast.makeText(getContext(), o.toString(), 0).show();
  }
}
