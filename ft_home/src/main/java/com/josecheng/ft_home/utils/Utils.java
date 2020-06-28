package com.josecheng.ft_home.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;
import java.io.File;

public class Utils {

  /**
   * 获取文件对应uri
   */
  public static Uri getPathUri(Context context, String filePath) {
    Uri uri;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider",
          new File(filePath));
    } else {
      uri = Uri.fromFile(new File(filePath));
    }
    return uri;
  }

  public static Intent getInstallApkIntent(Context context, String filePath) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//增加读写权限
    }
    intent.setDataAndType(getPathUri(context, filePath), "application/vnd.android.package-archive");
    return intent;
  }
}
