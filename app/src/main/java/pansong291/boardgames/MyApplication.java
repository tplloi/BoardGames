package pansong291.boardgames;

import pansong291.boardgames.activity.CrashActivity;
import pansong291.crash.CrashApplication;

public class MyApplication extends CrashApplication
{
  @Override
  public Class<?> getPackageActivity()
  {
    setShouldShowDeviceInfo(false);
    return CrashActivity.class;
  }

}
