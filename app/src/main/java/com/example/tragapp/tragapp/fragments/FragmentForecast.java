package com.example.tragapp.tragapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tragapp.R;
import com.example.tragapp.tragapp.models.DataObject;
import com.example.tragapp.tragapp.models.Forecast;
import com.example.tragapp.tragapp.uiutilities.CardAdapter;
import com.example.tragapp.tragapp.uiutilities.Constants;
import com.example.tragapp.tragapp.uiutilities.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author vishal kumar
 * @version 1.0
 * @since 06.01.2019
 * <p>
 * Forecast Activity is an controller class to bind weather api data with model and show awesome view.
 */
public class FragmentForecast extends Fragment {
    private static final String TAG = FragmentForecast.class.getSimpleName();


    ProgressDialog progressDialog;


    List<DataObject> weatherList;
    List<List<DataObject>> daysList;

    List<String> days;
    Set<String> distinctDays;
    CardAdapter cardAdapter;
    CardAdapter cardAdapter2;
    CardAdapter cardAdapter3;
    RecyclerView recyclerViewToday;
    RecyclerView recyclerViewTomorrow;
    RecyclerView recyclerViewLater;
    TabHost host;
    ImageView imageViewWeatherIcon;

    TextView tvTodayTemperature, tvTodayDescription, tvTodayWind, tvTodayPressure, tvTodayHumidity;
    ConstraintLayout layout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);
        Constants.TEMP_UNIT = " " + getResources().getString(R.string.temp_unit);
        initMember();
        initUi(view);
        StringBuilder addressStringBuilder = new StringBuilder();


        host = (TabHost) view.findViewById(R.id.tabHostT);
        host.setup();

        Date date = new Date();
        int day = date.getDay();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Today");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Today");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tomorrow");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Tomorrow");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Later");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Later");
        host.addTab(spec);
        recyclerViewToday = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        recyclerViewTomorrow = (RecyclerView) view.findViewById(R.id.my_recycler_view2);
        recyclerViewLater = (RecyclerView) view.findViewById(R.id.my_recycler_view3);


        recyclerViewToday.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewToday.setItemAnimator(new DefaultItemAnimator());

        recyclerViewTomorrow.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewTomorrow.setItemAnimator(new DefaultItemAnimator());

        recyclerViewLater.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewLater.setItemAnimator(new DefaultItemAnimator());

        recyclerViewToday.setHasFixedSize(true);
        recyclerViewTomorrow.setHasFixedSize(true);
        recyclerViewLater.setHasFixedSize(true);


        addressStringBuilder.append("Sirolo");
        getWeather(addressStringBuilder);
        return view;
    }


    /**
     * This method get the Date
     *
     * @param milliTime
     * @return
     */
    private String getDate(Long milliTime) {
        Date currentDate = new Date(milliTime);
        SimpleDateFormat df = new SimpleDateFormat("dd");
        String date = df.format(currentDate);
        return date;
    }

    /**
     * This method call the openwheathermap api by city name and onResponseSuccess bind the data
     * with associated model and set the data to show awesome view to user.
     *
     * @param addressStringBuilder
     */
    private void getWeather(StringBuilder addressStringBuilder) {
        progressDialog.show();
        Call<Forecast> call = Utility.getApis().getWeatherForecastData(addressStringBuilder, Constants.API_KEY, Constants.UNITS);
        call.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Log.i(TAG, "onResponse: " + response.isSuccessful());
                    weatherList = response.body().getDataObjectList();
                    distinctDays = new LinkedHashSet<>();
                    for (DataObject obj : weatherList) {
                        distinctDays.add(getDate(obj.getDt() * 1000));
                    }
                    Log.i("DISTINCTSIZE", distinctDays.size() + "");

                    days = new ArrayList<>();
                    days.addAll(distinctDays);

                    for (String day : days) {
                        List<DataObject> temp = new ArrayList<>();
                        Log.i("DAY", day);
                        for (DataObject data : weatherList) {
                            Log.i("ELEMENT", getDate(data.getDt() * 1000));
                            if (getDate(data.getDt() * 1000).equals(day)) {
                                Log.i("ADDEDDD", getDate(data.getDt() * 1000));
                                temp.add(data);
                            }
                        }
                        daysList.add(temp);
                    }

                    daysList.get(0).remove(0);

                    Log.i("DAYSLISTSIZE", daysList.size() + "");

                    cardAdapter = new CardAdapter(getActivity(),daysList.get(0));
                    recyclerViewToday.setAdapter(cardAdapter);

                    cardAdapter2 = new CardAdapter(getActivity(),daysList.get(1));
                    recyclerViewTomorrow.setAdapter(cardAdapter2);

                    cardAdapter3 = new CardAdapter(getActivity(),daysList.get(daysList.size() - 1));
                    recyclerViewLater.setAdapter(cardAdapter3);

                    Log.d("titolo",response.body().getCity().getName() + ", " + response.body().getCity().getCountry());
                    switch (weatherList.get(0).getWeather().get(0).getIcon()) {
                        case "01d":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_clear_sky);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_clear_and_sunny));
                            break;
                        case "01n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_clear_sky);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_clear_and_sunny));
                            break;
                        case "02d":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_few_cloud);
                           // toolbar.setBackgroundResource(R.color.color_partly_cloudy);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_partly_cloudy));
                            break;
                        case "02n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_few_cloud);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_partly_cloudy));
                            break;
                        case "03d":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_scattered_clouds);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_gusty_winds));
                            break;
                        case "03n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_scattered_clouds);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_gusty_winds));
                            break;
                        case "04d":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_broken_clouds);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_cloudy_overnight));
                            break;
                        case "04n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_broken_clouds);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_cloudy_overnight));
                            break;
                        case "09d":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_shower_rain);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_hail_stroms));
                            break;
                        case "09n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_shower_rain);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_hail_stroms));
                            break;
                        case "10d":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_rain);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_heavy_rain));
                            break;
                        case "10n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_rain);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_heavy_rain));
                            break;
                        case "11d":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_thunderstorm);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_thunderstroms));
                            break;
                        case "11n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_thunderstorm);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_thunderstroms));
                            break;
                        case "13d":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_snow);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_snow));
                            break;
                        case "13n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_snow);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_snow));
                            break;
                        case "15d":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_mist);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_mix_snow_and_rain));
                            break;
                        case "15n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_mist);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_mix_snow_and_rain));
                            break;
                    }
                    tvTodayTemperature.setText(weatherList.get(0).getMain().getTemp() + " " + getString(R.string.temp_unit));
                    tvTodayDescription.setText(weatherList.get(0).getWeather().get(0).getDescription());
                    tvTodayWind.setText(getString(R.string.wind_lable) + " " + weatherList.get(0).getWind().getSpeed() + " " + getString(R.string.wind_unit));
                    tvTodayPressure.setText(getString(R.string.pressure_lable) + " " + weatherList.get(0).getMain().getPressure() + " " + getString(R.string.pressure_unit));
                    tvTodayHumidity.setText(getString(R.string.humidity_lable) + " " + weatherList.get(0).getMain().getHumidity() + " " + getString(R.string.humidity_unit));

                }
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                progressDialog.dismiss();
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(getActivity(), getString(R.string.msg_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This method initialize the all ui member variables
     */
    private void initUi(View view) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.progress));

        imageViewWeatherIcon = view.findViewById(R.id.imageViewWeather);
        tvTodayTemperature = view.findViewById(R.id.todayTemperature);
        tvTodayDescription = view.findViewById(R.id.todayDescription);
        tvTodayWind = view.findViewById(R.id.todayWind);
        tvTodayPressure = view.findViewById(R.id.todayPressure);
        tvTodayHumidity = view.findViewById(R.id.todayHumidity);

        layout = view.findViewById(R.id.layoutWeather);
    }



    /**
     * This method initialize the all member variables
     */
    private void initMember() {
        weatherList = new ArrayList<>();
        daysList = new ArrayList<>();
    }
}
