package com.neoguri.nchart

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.neoguri.nchart.chart.NChart
import com.neoguri.nchart.databinding.ActivityMainBinding
import java.util.ArrayList

class MainActivity : AppCompatActivity(), NChart.ChartEventListener {

    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val value = "6,4,13,20,15,30,21,45,5,20,6,4,13,20,15,30,21,45,5,20,21,45,5,20"

        // 값,원이 있는지 없는지 ,원 움직일수있게 ,배경 깍기

        // circle = true, move = false -> setEnable로 빼기

        binding.NChart1.chartSet(value)

        binding.NChart2.setCircleEnabled(true)
        binding.NChart2.drawableBackgroundSet(ContextCompat.getDrawable(this, R.drawable.blue_gradation))
        binding.NChart2.chartSet(value, NChart.CUTMODE.TOP)

        binding.NChart3.setCircleEnabled(true)
        binding.NChart3.setCircleMoveEnabled(true)
        binding.NChart3.setOnChartEvent(this)
        binding.NChart3.drawableBackgroundSet(ContextCompat.getDrawable(this, R.drawable.blue_gradation))
        binding.NChart3.chartSet(value, NChart.CUTMODE.ALL)

        binding.NChart4.drawableBackgroundSet(ContextCompat.getDrawable(this, R.drawable.blue_gradation))
        binding.NChart4.chartSet(value, NChart.CUTMODE.BOTTOM)

        binding.NChart5.chartSet(value)

        binding.NChart6.drawableBackgroundSet(ContextCompat.getDrawable(this, R.drawable.blue_gradation))
        binding.NChart6.chartSet(value, NChart.CUTMODE.TOP)

        binding.NChart7.setCircleEnabled(true)
        binding.NChart7.setCircleMoveEnabled(true)
        binding.NChart7.setOnChartEvent(this)
        binding.scrollView7.setDrawView(binding.NChart7)
        binding.NChart7.drawableBackgroundSet(ContextCompat.getDrawable(this, R.drawable.blue_gradation))
        binding.NChart7.chartSet(value, 5, NChart.CUTMODE.ALL)

        binding.NChart8.drawableBackgroundSet(ContextCompat.getDrawable(this, R.drawable.blue_gradation))
        binding.NChart8.chartSet(value, 5, NChart.CUTMODE.BOTTOM)

        binding.NChart9.setCircleEnabled(true)
        binding.NChart9.setCircleMoveEnabled(true)
        binding.NChart9.setOnChartEvent(this)
        binding.scrollView9.setDrawView(binding.NChart9)
        binding.NChart9.chartSet(
            ContextCompat.getColor(this, R.color.transparency),
            ContextCompat.getColor(this, R.color.white),
            ContextCompat.getColor(this, R.color.purple_200),
            ContextCompat.getDrawable(this, R.drawable.setting_image),
            resources.getDimensionPixelSize(R.dimen.chart_line_stroke_width),
            resources.getDimensionPixelSize(R.dimen.chart_circle_width),
            value,
            7,
            NChart.CUTMODE.TOP,
            ContextCompat.getDrawable(this, R.drawable.blue_gradation)
        )

        binding.NChart10.setCircleEnabled(true)
        binding.NChart10.setCircleMoveEnabled(true)
        binding.NChart10.setOnChartEvent(this)
        binding.scrollView10.setDrawView(binding.NChart10)
        binding.NChart10.chartSet(value)

    }

    override fun onChartDownUpEvent(view: View, boolean: Boolean) {
        binding.scrollView.setScrollingEnabled(boolean)
        if(view.id == binding.NChart7.id){
            binding.scrollView7.setScrollingEnabled(boolean)
        } else if(view.id == binding.NChart9.id){
            binding.scrollView9.setScrollingEnabled(boolean)
        } else if(view.id == binding.NChart10.id){
            binding.scrollView10.setScrollingEnabled(boolean)
        }
    }

    override fun onScrollSetEvent(view: View, arrayList: ArrayList<Float>, width: Float) {
        if(view.id == binding.NChart7.id){
            binding.scrollView7.scrollTo((arrayList[0] - width).toInt(), 0)
        } else if(view.id == binding.NChart9.id){
            binding.scrollView9.scrollTo((arrayList[0] - width).toInt(), 0)
        } else if(view.id == binding.NChart10.id){
            binding.scrollView10.scrollTo((arrayList[0] - width).toInt(), 0)
        }
    }

    override fun onChartValueEvent(view: View, value: Int) {
    }

    override fun onDestroy() {
        mBinding = null
        super.onDestroy()
    }
}