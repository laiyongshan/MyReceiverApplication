package com.lys.androidapp.myreceiverapplication;

import android.util.Log;

import java.io.DataOutputStream;

/**
 * @author lys
 * @time 2018/8/29 19:14
 * @desc:
 */

public class SystemManager {


    /**
     * 11
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     * 12
     *
     * @param command 命令：String apkRoot="chmod 777 "+getPackageCodePath(); RootCommand(apkRoot);
     *                13
     * @return 应用程序是/否获取Root权限
     * 14
     */

    public static boolean RootCommand(String command)

    {

        Process process = null;

        DataOutputStream os = null;

        try

        {

            process = Runtime.getRuntime().exec("su");

            os = new DataOutputStream(process.getOutputStream());

            os.writeBytes(command + "\n");

            os.writeBytes("exit\n");

            os.flush();

            process.waitFor();

        } catch (Exception e)

        {

            Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());

            return false;

        } finally

        {

            try

            {

                if (os != null)

                {

                    os.close();

                }

                process.destroy();

            } catch (Exception e)

            {

            }

        }

        Log.d("*** DEBUG ***", "Root SUC ");

        return true;

    }

}
