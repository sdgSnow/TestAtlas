package com.weidu.testatlas;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.taobao.atlas.dex.util.FileUtils;
import com.taobao.atlas.update.AtlasUpdater;
import com.taobao.atlas.update.model.UpdateInfo;

import java.io.File;

public class MainActivity extends AppCompatActivity{

    private Button bt_remote;
    private Button bt_local;
    private Button bt_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_remote = findViewById(R.id.bt_remote);
        bt_local = findViewById(R.id.bt_local);
        bt_update = findViewById(R.id.bt_update);

        bt_remote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("testatlas","bt_remote");
                Intent intent = new Intent();
                intent.setClassName(MainActivity.this, "com.weidu.remotebundle.RemoteActivity");
                startActivity(intent);
            }
        });
        bt_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("testatlas","bt_local");
                Intent intent1 = new Intent();
                intent1.setClassName(MainActivity.this, "com.weidu.localbundle.LocalActivity");
                startActivity(intent1);
            }
        });
        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("testatlas","bt_update");
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        update();
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        Toast.makeText(MainActivity.this, "更新完成,请重启", Toast.LENGTH_LONG).show();
                    }
                }.execute();
            }
        });

    }

    private void update(){
        File updateInfo = new File(getExternalCacheDir(), "update.json");
        if (!updateInfo.exists()) {
            Toast.makeText(MainActivity.this, "更新信息不存在,请先 执行 buildTpatch.sh", Toast.LENGTH_LONG).show();
            return;
        }
        String jsonStr = new String(FileUtils.readFile(updateInfo));
        UpdateInfo info = JSON.parseObject(jsonStr, UpdateInfo.class);
        File patchFile = new File(getExternalCacheDir(), "patch-" + info.updateVersion + "@" + info.baseVersion + ".tpatch");
        try {
            AtlasUpdater.update(info, patchFile);
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "更新失败," + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
