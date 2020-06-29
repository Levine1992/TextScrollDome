package com.example.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blankj.utilcode.util.LogUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.getConfig().globalTag = "123456"
        setContentView(R.layout.activity_main)

        tsv_txt.setTextSizeDp(20)
        tsv_txt.setTextColor("#000000")

        val strings = arrayListOf<String>()
        strings.add("1111111111111111111111111111111111111111111111111111111111111111111")
        strings.add("222222222222222222222222222222222222")
        strings.add("333333333333333333333333333333333")
        strings.add("4444444444444444444444444444444")
        strings.add("55555555555555555555555555555555555555555")
        strings.add("6666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666666")
        strings.add("7777777777777")
        strings.add("888888888888888888888888888888888888888888888888")
        strings.add("99999999999999999999999999999999999999999999999999999")
        strings.add("00000000000000000000000000000000000000000000000000000000")
        tsv_txt.setData(strings)
    }
}
