package com.example.mobilesafe.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.mobilesafe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class ContactActivity extends AppCompatActivity {

    private ListView lvList;
    private ContentResolver resolver;
    private ArrayList<HashMap<String, String>> readContact;  //用来存放读取出来的联系人

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        initialize();
    }

    /**
     * 初始化控件
     */
    private void initialize() {
        lvList = (ListView) findViewById(R.id.lvList);
        initData();
    }

    /**
     * 初始化ListView数据
     */
    private void initData() {
        readContact = readContact();   //把读取的联系人放到readContact中

        //在这里我们使用SimpleAdapter，里面传入五个参数

        /**
         ** @param context The context where the View associated with this SimpleAdapter is running
         *                 当前的上下文
         * @param data A List of Maps. Each entry in the List corresponds to one row in the list. The
         *        Maps contain the data for each row, and should include all the entries specified in
         *        "from"
         *             要显示的数据  Maps集合
         * @param resource Resource identifier of a view layout that defines the views for this list
         *        item. The layout file should include at least those named views defined in "to"
         *                 要使用于展示的布局资源
         * @param from A list of column names that will be added to the Map associated with each
         *        item.
         *             每一列的名字，也就data数据中的每一列的数据的名称
         * @param to The views that should display column in the "from" parameter. These should all be
         *        TextViews. The first N views in this list are given the values of the first N columns
         *        in the from parameter.
         *           就是对应的要展示的每一列的控件的id
         */
        lvList.setAdapter(new SimpleAdapter(this, readContact, R.layout.contact_list_item, new String[]{
                "name", "phone"
        }, new int[]{R.id.tvName, R.id.tvPhone}));

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phone = readContact.get(position).get("phone");
                Intent intent = new Intent();
                intent.putExtra("phone", phone);
                setResult(ContactActivity.RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * 读取系统的联系人
     */
    private ArrayList<HashMap<String, String>> readContact() {

        //根据mimetype来区分哪个是联系人哪个是电话号码

        Uri rawContactUri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataContactUri = Uri.parse("content://com.android.contacts/data");

        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        //首先重raw_contact中读取联系人的id("contact_id")
        Cursor rawContactCursor = getContentResolver().query(rawContactUri, new String[]{"contact_id"}, null, null, null);
        if (rawContactCursor != null) {
            while (rawContactCursor.moveToNext()) {
                String contactId = rawContactCursor.getString(0);
                //其次根据contact_id从data中读取联系人的电话号码和姓名  实际上查询的是视图view_data
                Cursor dataCursor = getContentResolver().query(dataContactUri, new String[]{"data1", "mimetype"}, "contact_id = ?", new String[]{contactId}, null);
                if (dataCursor != null) {
                    HashMap<String, String> map = new HashMap<>();
                    //根据mimetype来区分哪个是联系人哪个是电话号码
                    while (dataCursor.moveToNext()) {
                        String data1 = dataCursor.getString(0);
                        String mimetype = dataCursor.getString(1);
                        if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {
                            map.put("phone", data1);
                        } else if ("vnd.android.cursor.item/name".equals(mimetype)) {
                            map.put("name", data1);
                        }
                    }
                    list.add(map);
                    dataCursor.close();
                }
            }
            rawContactCursor.close();
        }
        return list;
    }
}
