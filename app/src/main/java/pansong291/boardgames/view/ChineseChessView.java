package pansong291.boardgames.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.Stack;

public class ChineseChessView extends View
{
  int board_row; // 棋盘行数
  int board_column; //棋盘列数
  BoardState[][] board_state; //棋盘状态数据

  int board_background_color; //棋盘背景颜色
  float board_left_x; //棋盘左端x值
  float board_right_x; //棋盘右端x值
  float board_top_y; //棋盘顶端y值
  float board_bottom_y; //棋盘底端y值
  float board_center_bottom_y; //棋盘中底端y值
  float board_center_top_y; //棋盘中顶端y值
  int board_line_color; //棋盘线颜色
  float board_line_stroke; //棋盘线厚度
  float board_spacing; //棋盘线间隔
  int[] board_oblique_row; //棋盘斜线行
  int[] board_oblique_column; //棋盘斜线列
  float[] board_oblique_pts; //棋盘斜线坐标
  float board_special_spacing; //炮兵特殊位置间隔
  int[] board_cannon; //炮位置
  int[] board_pawn_row; // 兵位置行
  int[] board_pawn_column; // 兵位置列
  Paint board_paint; //棋盘画笔

  float piece_radius; //棋子半径
  float piece_text_size; //棋子字体大小
  Paint piece_background_paint; //棋子背景画笔
  Paint piece_black_paint; //黑子画笔
  Paint piece_black_circle_paint;
  Paint piece_red_paint; //红子画笔
  Paint piece_red_circle_paint;

  boolean calculated; //是否计算过尺寸

  OnPieceDownListener onPieceDownListener; //落子监听
  int current; //当前落子方
  Point touch_point; //触摸点
  Point select_point; //选中点
  Stack<Step> history; //历史记录
  boolean hasWinner; //是否产生赢家

  public ChineseChessView(Context c)
  {
    super(c);
    defInit();
  }

  public ChineseChessView(Context c, AttributeSet a)
  {
    super(c, a);
    defInit();
  }

  public ChineseChessView(Context c, AttributeSet a, int s)
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
    initBoardState();
    select_point.set(0, 0);
    current = Color.RED;
    postInvalidate();
  } //reset

  /*
   * 悔棋
   */
  public void retract()
  {
    if(history.empty()) return;
    hasWinner = false;
    Step stp = history.pop();
    board_state[stp.to.x][stp.to.y] = stp.before;
    board_state[stp.from.x][stp.from.y] = stp.after;
    select_point.set(stp.from.x, stp.from.y);
    switchCurrent();
    postInvalidate();
  } //retract

  /*
   * 获取当前落子方
   */
  public int getCurrent()
  {
    return current;
  }

  public void setOnPieceDownListener(OnPieceDownListener listener)
  {
    onPieceDownListener = listener;
  }

  private void defInit()
  {
    board_row = 10;
    board_column = 9;
    board_state = new BoardState[board_row][board_column];
    board_oblique_row = new int[]{0, 2, 7, 9};
    board_oblique_column = new int[]{3, 5};
    board_cannon = new int[]{2, 7, 1, 7};
    board_pawn_row = new int[]{3, 6};
    board_pawn_column = new int[]{2, 4, 6};
    initBoardState();

    board_background_color = Color.WHITE;
    board_line_color = Color.BLACK;
    board_line_stroke = 2;
    board_paint = new Paint();
    board_paint.setColor(board_line_color);
    board_paint.setStrokeWidth(2);
    board_paint.setStrokeCap(Paint.Cap.ROUND);

    piece_background_paint = new Paint();
    piece_background_paint.setColor(Color.LTGRAY);

    piece_black_paint = new Paint();
    piece_black_paint.setColor(Color.BLACK);
    piece_black_circle_paint = new Paint(piece_black_paint);
    piece_black_circle_paint.setStrokeWidth(4);
    piece_black_circle_paint.setStyle(Paint.Style.STROKE);

    piece_red_paint = new Paint();
    piece_red_paint.setColor(Color.RED);
    piece_red_circle_paint = new Paint(piece_black_circle_paint);
    piece_red_circle_paint.setColor(Color.RED);

    current = Color.RED;
    touch_point = new Point();
    select_point = new Point();
    history = new Stack<>();
  } //defInit

  /*
   * 初始化棋盘状态
   */
  private void initBoardState()
  {
    // 清除棋盘
    for(int i = 0; i < board_row; i++)
      for(int j = 0; j < board_column; j++)
        board_state[i][j] = null;
    // 放置兵
    for(int i = 0; i < board_column; i += 2)
    {
      board_state[board_pawn_row[0]][i] = BoardState.B_P;
      board_state[board_pawn_row[1]][i] = BoardState.R_P;
    }
    // 放置炮
    board_state[board_cannon[0]][board_cannon[2]] = BoardState.B_C;
    board_state[board_cannon[0]][board_cannon[3]] = BoardState.B_C;
    board_state[board_cannon[1]][board_cannon[2]] = BoardState.R_C;
    board_state[board_cannon[1]][board_cannon[3]] = BoardState.R_C;
    // 放置其他棋子
    board_state[0][0] = BoardState.B_R;
    board_state[0][1] = BoardState.B_N;
    board_state[0][2] = BoardState.B_B;
    board_state[0][3] = BoardState.B_Q;
    board_state[0][4] = BoardState.B_K;
    board_state[0][5] = BoardState.B_Q;
    board_state[0][6] = BoardState.B_B;
    board_state[0][7] = BoardState.B_N;
    board_state[0][8] = BoardState.B_R;
    board_state[9][0] = BoardState.R_R;
    board_state[9][1] = BoardState.R_N;
    board_state[9][2] = BoardState.R_B;
    board_state[9][3] = BoardState.R_Q;
    board_state[9][4] = BoardState.R_K;
    board_state[9][5] = BoardState.R_Q;
    board_state[9][6] = BoardState.R_B;
    board_state[9][7] = BoardState.R_N;
    board_state[9][8] = BoardState.R_R;
  } //initBoardState

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
    board_center_bottom_y = board_top_y + (board_row / 2 - 1) * board_spacing;
    board_center_top_y = board_center_bottom_y + board_spacing;

    // 半径为间隔一半，再乘以0.85
    piece_radius = board_spacing * 85 / 200;
    board_special_spacing = board_spacing / 8;

    piece_text_size = piece_radius;
    piece_black_paint.setTextSize(piece_text_size);
    piece_red_paint.setTextSize(piece_text_size);

    board_oblique_pts = new float[16];
    for(int i = 0; i < board_oblique_pts.length; i++)
      if(i % 2 == 0) board_oblique_pts[i] = board_left_x;
      else board_oblique_pts[i] = board_top_y;
    board_oblique_pts[0] += board_oblique_column[0] * board_spacing;
    board_oblique_pts[1] += board_oblique_row[0] * board_spacing;
    board_oblique_pts[2] += board_oblique_column[1] * board_spacing;
    board_oblique_pts[3] += board_oblique_row[1] * board_spacing;

    board_oblique_pts[4] += board_oblique_column[1] * board_spacing;
    board_oblique_pts[5] += board_oblique_row[0] * board_spacing;
    board_oblique_pts[6] += board_oblique_column[0] * board_spacing;
    board_oblique_pts[7] += board_oblique_row[1] * board_spacing;

    board_oblique_pts[8] += board_oblique_column[0] * board_spacing;
    board_oblique_pts[9] += board_oblique_row[2] * board_spacing;
    board_oblique_pts[10] += board_oblique_column[1] * board_spacing;
    board_oblique_pts[11] += board_oblique_row[3] * board_spacing;

    board_oblique_pts[12] += board_oblique_column[1] * board_spacing;
    board_oblique_pts[13] += board_oblique_row[2] * board_spacing;
    board_oblique_pts[14] += board_oblique_column[0] * board_spacing;
    board_oblique_pts[15] += board_oblique_row[3] * board_spacing;

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
    {
      if(i == 0 || i == board_column - 1)
      {
        canvas.drawLine(board_left_x + i * board_spacing, board_top_y,
                        board_left_x + i * board_spacing, board_bottom_y, board_paint);
      }else
      {
        canvas.drawLine(board_left_x + i * board_spacing, board_top_y,
                        board_left_x + i * board_spacing, board_center_bottom_y, board_paint);
        canvas.drawLine(board_left_x + i * board_spacing, board_center_top_y,
                        board_left_x + i * board_spacing, board_bottom_y, board_paint);
      }
    }

    //绘制斜线
    canvas.drawLines(board_oblique_pts, board_paint);

    //绘制炮位置
    drawSpecial(canvas, board_left_x + board_cannon[2] * board_spacing, board_top_y + board_cannon[0] * board_spacing);
    drawSpecial(canvas, board_left_x + board_cannon[3] * board_spacing, board_top_y + board_cannon[0] * board_spacing);
    drawSpecial(canvas, board_left_x + board_cannon[2] * board_spacing, board_top_y + board_cannon[1] * board_spacing);
    drawSpecial(canvas, board_left_x + board_cannon[3] * board_spacing, board_top_y + board_cannon[1] * board_spacing);

    //绘制左兵位置
    drawSpecialRight(canvas, board_left_x, board_top_y + board_pawn_row[0] * board_spacing);
    drawSpecialRight(canvas, board_left_x, board_top_y + board_pawn_row[1] * board_spacing);
    //绘制右兵位置
    drawSpecialLeft(canvas, board_right_x, board_top_y + board_pawn_row[0] * board_spacing);
    drawSpecialLeft(canvas, board_right_x, board_top_y + board_pawn_row[1] * board_spacing);
    //绘制其余兵位置
    for(int i = 0; i < board_pawn_row.length; i++)
      for(int j = 0; j < board_pawn_column.length; j++)
        drawSpecial(canvas, board_left_x + board_pawn_column[j] * board_spacing, board_top_y + board_pawn_row[i] * board_spacing);

  } //drawBoard

  /*
   * 画炮兵特殊位置
   */
  private void drawSpecial(Canvas canvas, float x, float y)
  {
    drawSpecialLeft(canvas, x, y);
    drawSpecialRight(canvas, x, y);
  } //drawSpecial

  /*
   * 画炮兵特殊位置左半部分
   */
  private void drawSpecialLeft(Canvas canvas, float x, float y)
  {
    float z;
    x -= board_special_spacing;
    y -= board_special_spacing;
    z = y - board_special_spacing;
    canvas.drawLine(x, z, x, y, board_paint);
    z = x - board_special_spacing;
    canvas.drawLine(z, y, x, y, board_paint);

    y += 2 * board_special_spacing;
    canvas.drawLine(z, y, x, y, board_paint);
    z = y + board_special_spacing;
    canvas.drawLine(x, z, x, y, board_paint);
  } //drawSpecialLeft

  /*
   * 画炮兵特殊位置右半部分
   */
  private void drawSpecialRight(Canvas canvas, float x, float y)
  {
    float z;
    x += board_special_spacing;
    y -= board_special_spacing;
    z = y - board_special_spacing;
    canvas.drawLine(x, z, x, y, board_paint);
    z = x + board_special_spacing;
    canvas.drawLine(z, y, x, y, board_paint);

    y += 2 * board_special_spacing;
    canvas.drawLine(z, y, x, y, board_paint);
    z = y + board_special_spacing;
    canvas.drawLine(x, z, x, y, board_paint);
  } //drawSpecialRight

  /*
   * 绘制棋子
   */
  public void drawPiece(Canvas canvas)
  {
    Paint p = null, p2 = null;
    float x, y;
    BoardState bs;
    for(int i = 0; i < board_row; i++)
    {
      for(int j = 0; j < board_column; j++)
      {
        bs = board_state[i][j];
        if(bs == null)
          p = null;
        else if(bs.color() == Color.BLACK)
        {
          p = piece_black_paint;
          p2 = piece_black_circle_paint;
        }else
        {
          p = piece_red_paint;
          p2 = piece_red_circle_paint;
        }
        if(p != null)
        {
          x = board_left_x + j * board_spacing;
          y = board_top_y + i * board_spacing;
          canvas.drawCircle(x, y, piece_radius, piece_background_paint);
          canvas.drawCircle(x, y, piece_radius * 4 / 5, p2);
          x -= piece_text_size / 2;
          y += piece_text_size / 3;
          canvas.drawText(bs.nickName(), x, y, p);
        }
      }
    }
  } //drawPiece

  /*
   * 绘制选中位置
   */
  private void drawSelect(Canvas canvas)
  {
    if(isOutside(select_point.x, select_point.y)) return;
    float x, y, z;
    x = board_left_x + select_point.y * board_spacing;
    y = board_top_y + select_point.x * board_spacing;
    x -= piece_radius;
    y -= piece_radius;
    z = y + board_special_spacing;
    canvas.drawLine(x, z, x, y, piece_red_circle_paint);
    z = x + board_special_spacing;
    canvas.drawLine(z, y, x, y, piece_red_circle_paint);

    y += 2 * piece_radius;
    canvas.drawLine(z, y, x, y, piece_red_circle_paint);
    z = y - board_special_spacing;
    canvas.drawLine(x, z, x, y, piece_red_circle_paint);

    x += 2 * piece_radius;
    canvas.drawLine(x, z, x, y, piece_red_circle_paint);
    z = x - board_special_spacing;
    canvas.drawLine(z, y, x, y, piece_red_circle_paint);

    y -= 2 * piece_radius;
    canvas.drawLine(z, y, x, y, piece_red_circle_paint);
    z = y + board_special_spacing;
    canvas.drawLine(x, z, x, y, piece_red_circle_paint);
  } //drawSelect

  @Override
  protected void onDraw(Canvas canvas)
  {
    calculate();
    drawBoard(canvas);
    drawPiece(canvas);
    drawSelect(canvas);
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
        // 单击判定
        if(!touch_point.equals(rx, ry)) break;
        // 多次点击同一个位置
        if(select_point.equals(rx, ry)) break;
        BoardState selBS = board_state[select_point.x][select_point.y];
        // 之前选中的不为空
        if(selBS != null)
        {
          // 回合轮流限制
          if(selBS.color() == current)
          {
            // 到达位要么为空，要么为对方棋子
            if(board_state[rx][ry] == null || board_state[rx][ry].color() != current)
            {
              // 到达位符合规则
              if(isCorrect(rx, ry))
              {
                Step stp = new Step();
                stp.from = new Point(select_point);
                stp.to = new Point(touch_point);
                stp.before = board_state[rx][ry];
                stp.after = board_state[select_point.x][select_point.y];
                board_state[rx][ry] = stp.after;
                board_state[select_point.x][select_point.y] = null;
                history.push(stp);
                switchCurrent();
                if(onPieceDownListener != null)
                  onPieceDownListener.afterPieceDown(current);
              }
            }
          }
        }
        // 设置选中位置
        select_point.set(rx, ry);
        invalidate();
        break;
    }
    return true;
  } //onTouchEvent

  /*
   * 判断到达位是否合规
   */
  private boolean isCorrect(int x, int y)
  {
    if(isOutside(select_point.x, select_point.y)) return false;
    boolean b;
    switch(board_state[select_point.x][select_point.y])
    {
      case R_P:// 兵
        b = x == select_point.x - 1 && y == select_point.y;
        if(x < 5)
        {
          b |= x == select_point.x && 1 == Math.abs(y - select_point.y);
        }
        return b;
      case B_P:// 卒
        b = x == select_point.x + 1 && y == select_point.y;
        if(x > 4)
        {
          b |= x == select_point.x && 1 == Math.abs(y - select_point.y);
        }
        return b;
      case R_C:
      case B_C:// 炮
        int c = -1;
        if(select_point.x == x)
        {
          c = 0;
          b = select_point.y < y;
          for(int i = 1 + (b ? select_point.y : y); i < (b ? y : select_point.y); i++)
          {
            if(board_state[x][i] != null) c++;
          }
        }else if(select_point.y == y)
        {
          c = 0;
          b = select_point.x < x;
          for(int i = 1 + (b ? select_point.x : x); i < (b ? x : select_point.x); i++)
          {
            if(board_state[i][y] != null) c++;
          }
        }
        if(board_state[x][y] == null) return c == 0;
        else return c == 1;
      case R_K:// 帅
        if(6 < x && x < 10 && 2 < y && y < 6)
        {
          return 1 == (Math.abs(select_point.x - x) + Math.abs(select_point.y - y));
        }
        break;
      case B_K:// 将
        if(-1 < x && x < 3 && 2 < y && y < 6)
        {
          return 1 == (Math.abs(select_point.x - x) + Math.abs(select_point.y - y));
        }
        break;
      case R_Q:// 仕
        if(6 < x && x < 10 && 2 < y && y < 6)
        {
          return 1 == Math.abs(select_point.x - x) && 1 == Math.abs(select_point.y - y);
        }
        break;
      case B_Q:// 士
        if(-1 < x && x < 3 && 2 < y && y < 6)
        {
          return 1 == Math.abs(select_point.x - x) && 1 == Math.abs(select_point.y - y);
        }
        break;
      case R_B:// 相
        if(x > 4)
        {
          if(2 == Math.abs(select_point.x - x) && 2 == Math.abs(select_point.y - y))
            return board_state[(select_point.x + x) / 2][(select_point.y + y) / 2] == null;
        }
        break;
      case B_B:// 象
        if(x < 5)
        {
          if(2 == Math.abs(select_point.x - x) && 2 == Math.abs(select_point.y - y))
            return board_state[(select_point.x + x) / 2][(select_point.y + y) / 2] == null;
        }
        break;
      case R_N:
      case B_N:// 马
        if(Math.abs(select_point.x - x) == 1 && Math.abs(select_point.y - y) == 2)
        {
          if(y > select_point.y) y--;
          else y++;
          return board_state[select_point.x][y] == null;
        }else if(Math.abs(select_point.x - x) == 2 && Math.abs(select_point.y - y) == 1)
        {
          if(x > select_point.x) x--;
          else x++;
          return board_state[x][select_point.y] == null;
        }
        break;
      case R_R:
      case B_R:// 车
        if(select_point.x == x)
        {
          b = select_point.y < y;
          for(int i = 1 + (b ? select_point.y : y); i < (b ? y : select_point.y); i++)
          {
            if(board_state[x][i] != null) return false;
          }
          return true;
        }else if(select_point.y == y)
        {
          b = select_point.x < x;
          for(int i = 1 + (b ? select_point.x : x); i < (b ? x : select_point.x); i++)
          {
            if(board_state[i][y] != null) return false;
          }
          return true;
        }
        break;
    }
    return false;
  } //isCorrect

  /*
   * 切换落子方
   */
  private void switchCurrent()
  {
    switch(current)
    {
      case Color.BLACK:
        current = Color.RED;
        break;

      case Color.RED:
        current = Color.BLACK;
        break;
    }
  } //switchCurrent

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
   * 是否出界
   */
  private boolean isOutside(int x, int y)
  {
    if(x < 0 || x >= board_row || y < 0 || y >= board_column)
      return true;
    return false;
  }

  public enum BoardState
  {
    R_P,R_C,R_K,R_Q,R_B,R_N,R_R,
    B_P,B_C,B_K,B_Q,B_B,B_N,B_R;

    public static final String[] nickNames = new String[]
    {"兵","砲","帅","仕","相","馬","車",
      "卒","炮","将","士","象","马","车"};

    public int color()
    {
      if(ordinal() < values().length / 2)
        return Color.RED;
      return Color.BLACK;
    }

    public String nickName()
    {
      return nickNames[ordinal()];
    }
  } //BoardState

  public class Step
  {
    Point from, to;
    BoardState after, before;
  }

  /*
   * 落子监听器
   */
  public interface OnPieceDownListener
  {
    /*
     * 参数x和y表示落子位置，cur为该子颜色
     */
    void onPieceDown(int x, int y, int cur);

    /*
     * cur为下次要落子的颜色
     */
    void afterPieceDown(int cur);
  }

}
