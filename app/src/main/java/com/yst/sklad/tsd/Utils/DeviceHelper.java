package com.yst.sklad.tsd.Utils;

/**
 * Created by lapenkov on 01.12.2017.
 */

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import android.os.Bundle;

public class DeviceHelper {

    public static int getTypeOfHardware()
    {
      if (android.os.Build.MANUFACTURER.toLowerCase().equals("pitech")) return 1;

      else
        return 0;
    }
}
