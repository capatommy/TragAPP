package com.example.tragapp.tragapp.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tragapp.R;
import com.example.tragapp.tragapp.models.DataObject;
import com.example.tragapp.tragapp.models.Forecast;
import com.example.tragapp.tragapp.uiutilities.Constants;
import com.example.tragapp.tragapp.uiutilities.Utility;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentForecastNew extends Fragment {
    private static final String TAG = FragmentForecast.class.getSimpleName();

    ProgressDialog progressDialog;

    List<DataObject> weatherList;
    List<List<DataObject>> daysList;

    List<String> days;
    Set<String> distinctDays;
    ImageView ImageToday, ImageTomorrow, ImageLater;
    TextView TodayDay, TomorrowDay, LaterDay;
    TextView TodayWeather;
    TextView TodayTemp, TomorrowTemp, LaterTemp;
    LinearLayout llToday, llTomorrow, llLater,ll;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forecast_new, container,false);
        Constants.TEMP_UNIT = " " + getResources().getString(R.string.temp_unit);
        initMember();
        initUi(view);
        StringBuilder addressStringBuilder = new StringBuilder();

        Date date = new Date();
        int day = date.getDay();

        addressStringBuilder.append("Sirolo");
        getWeather(addressStringBuilder);
        return view;
    }

    private String getDate(Long milliTime) {
        Date currentDate = new Date(milliTime);
        SimpleDateFormat df = new SimpleDateFormat("dd");
        String date = df.format(currentDate);
        return date;
    }
    private void initUi (View view) {

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.progress));

        ImageToday = view.findViewById(R.id.ImageWeatherToday);
        ImageTomorrow = view.findViewById(R.id.ImageWeatherTomorrow);
        ImageLater = view.findViewById(R.id.ImageWeatherLater);
        TodayDay = view.findViewById(R.id.TodayDay);
        TomorrowDay = view.findViewById(R.id.TomorrowDay);
        LaterDay = view.findViewById(R.id.LaterDay);
        TodayTemp = view.findViewById(R.id.TodayTemp);
        TomorrowTemp = view.findViewById(R.id.TomorrowTemp);
        LaterTemp = view.findViewById(R.id.LaterTemp);

        TodayWeather = view.findViewById(R.id.TodayWeather);

        llToday = view.findViewById(R.id.llToday);
        llTomorrow = view.findViewById(R.id.llTomorrow);
        llLater = view.findViewById(R.id.llLater);
        ll = view.findViewById(R.id.ll);

    }

    private void initMember () {
        weatherList = new ArrayList<>();
        daysList = new ArrayList<>();
    }


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
                    Log.d("titolo", response.body().getCity().getName() + ", " + response.body().getCity().getCountry());

                    TodayWeather.setText(daysList.get(0).get(0).getWeather().get(0).getDescription());

                    UpdateTab(daysList.get(0).get(0), TodayDay, TodayTemp, ImageToday, llToday);
                    UpdateTab(daysList.get(1).get(0), TomorrowDay, TomorrowTemp, ImageTomorrow, llTomorrow);
                    UpdateTab(daysList.get(2).get(0), LaterDay, LaterTemp, ImageLater, llLater);

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

    private void UpdateTab(DataObject object, TextView day, TextView temp, ImageView logo,  LinearLayout layout){

        switch (object.getWeather().get(0).getIcon()) {
            case "01d":
                logo.setImageResource(R.drawable.ic_weather_clear_sky);
                layout.setBackgroundColor(getResources().getColor(R.color.color_clear_and_sunny));
                break;
            case "01n":
                logo.setImageResource(R.drawable.ic_weather_clear_sky);
                layout.setBackgroundColor(getResources().getColor(R.color.color_clear_and_sunny));
                break;
            case "02d":
                logo.setImageResource(R.drawable.ic_weather_few_cloud);
                layout.setBackgroundColor(getResources().getColor(R.color.color_partly_cloudy));
                break;
            case "02n":
                logo.setImageResource(R.drawable.ic_weather_few_cloud);
                layout.setBackgroundColor(getResources().getColor(R.color.color_partly_cloudy));
                break;
            case "03d":
                logo.setImageResource(R.drawable.ic_weather_scattered_clouds);
                layout.setBackgroundColor(getResources().getColor(R.color.color_gusty_winds));
                break;
            case "03n":
                logo.setImageResource(R.drawable.ic_weather_scattered_clouds);
                layout.setBackgroundColor(getResources().getColor(R.color.color_gusty_winds));
                break;
            case "04d":
                logo.setImageResource(R.drawable.ic_weather_broken_clouds);
                layout.setBackgroundColor(getResources().getColor(R.color.color_cloudy_overnight));
                break;
            case "04n":
                logo.setImageResource(R.drawable.ic_weather_broken_clouds);
                layout.setBackgroundColor(getResources().getColor(R.color.color_cloudy_overnight));
                break;
            case "09d":
                logo.setImageResource(R.drawable.ic_weather_shower_rain);
                layout.setBackgroundColor(getResources().getColor(R.color.color_hail_stroms));
                break;
            case "09n":
                logo.setImageResource(R.drawable.ic_weather_shower_rain);
                layout.setBackgroundColor(getResources().getColor(R.color.color_hail_stroms));
                break;
            case "10d":
                logo.setImageResource(R.drawable.ic_weather_rain);
                layout.setBackgroundColor(getResources().getColor(R.color.color_heavy_rain));
                break;
            case "10n":
                logo.setImageResource(R.drawable.ic_weather_rain);
                layout.setBackgroundColor(getResources().getColor(R.color.color_heavy_rain));
                break;
            case "11d":
                logo.setImageResource(R.drawable.ic_weather_thunderstorm);
                layout.setBackgroundColor(getResources().getColor(R.color.color_thunderstroms));
                break;
            case "11n":
                logo.setImageResource(R.drawable.ic_weather_thunderstorm);
                layout.setBackgroundColor(getResources().getColor(R.color.color_thunderstroms));
                break;
            case "13d":
                logo.setImageResource(R.drawable.ic_weather_snow);
                layout.setBackgroundColor(getResources().getColor(R.color.color_snow));
                break;
            case "13n":
                logo.setImageResource(R.drawable.ic_weather_snow);
                layout.setBackgroundColor(getResources().getColor(R.color.color_snow));
                break;
            case "15d":
                logo.setImageResource(R.drawable.ic_weather_mist);
                layout.setBackgroundColor(getResources().getColor(R.color.color_mix_snow_and_rain));
                break;
            case "15n":
                logo.setImageResource(R.drawable.ic_weather_mist);
                layout.setBackgroundColor(getResources().getColor(R.color.color_mix_snow_and_rain));
                break;
        }
        temp.setText(object.getMain().getTemp()+ " " + getString(R.string.temp_unit));
        day.setText(getTime(object.getDt() * 1000));

    }

    private String getTime(Long milliTime) {
        Date currentDate = new Date(milliTime);
        SimpleDateFormat df = new SimpleDateFormat("E, dd.MM.yyyy");
        String date = df.format(currentDate);
        return date;
    }

}


