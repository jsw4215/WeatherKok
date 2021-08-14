package com.example.weatherkok.where;

import android.util.Log;

import com.example.weatherkok.where.interfaces.WhereContract;
import com.example.weatherkok.where.interfaces.WhereRetrofitInterface;
import com.example.weatherkok.where.models.ResponseParams;
import com.example.weatherkok.where.models.SidoResponse;
import com.example.weatherkok.where.models.WhereRequestBody;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Query;

import static com.example.weatherkok.src.ApplicationClass.getRetrofit;

public class WhereService {

    private WhereContract.ActivityView mWhereContractActivityView;

    public WhereService(WhereContract.ActivityView mWhereContractActivityView) {
        this.mWhereContractActivityView = mWhereContractActivityView;
    }

    public void getSidoList(String key,
                            String domain,
                            String request,
                            String format,
                            int size,
                            int page,
                            boolean geometry,
                            boolean attribute,
                            String crs,
                            String geomfilter,
                            String data) {
        final WhereRetrofitInterface whereRetrofitInterface = getRetrofit().create(WhereRetrofitInterface.class);
        //new WhereRequestBody(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data)
        whereRetrofitInterface.getSidoList(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data).enqueue(new Callback<ResponseParams>() {
            @Override
            public void onResponse(Call<ResponseParams> call, Response<ResponseParams> response) {
                if(response == null) {
                    Log.e("aaaaaa","error ~~~!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }
                String r = response.body().toString();
                final ResponseParams responseParams = response.body();
                if (responseParams == null) {
                    mWhereContractActivityView.validateFailure(null);
                    return;
                }
                Log.d("isSuccess : ", String.valueOf(response.isSuccessful()));
                Log.d("Status : ", response.body().getResponse().getStatus());
                Log.d("Status : ", responseParams.getResponse().getStatus());
                Log.d("Record : ", String.valueOf(responseParams.getResponse().getRecord().getTotal()));
                Log.d("result : ", responseParams.getResponse().getResult().getFeatureCollection().getFeatures().get(1).getProperties().getCtp_kor_nm());
                mWhereContractActivityView.validateSuccess(response.isSuccessful(), responseParams.getResponse().getRecord(), responseParams.getResponse().getResult());
            }

            @Override
            public void onFailure(Call<ResponseParams> call, Throwable t) {
                mWhereContractActivityView.validateFailure(null);
            }
        });
    }

    void getSigunguList(String key,
                     String domain,
                     String request,
                     String format,
                     int size,
                     int page,
                     boolean geometry,
                     boolean attribute,
                     String crs,
                     String geomfilter,
                     String data) {
        final WhereRetrofitInterface whereRetrofitInterface = getRetrofit().create(WhereRetrofitInterface.class);
        //new WhereRequestBody(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data)
        whereRetrofitInterface.getSigunguList(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data).enqueue(new Callback<ResponseParams>() {
            @Override
            public void onResponse(Call<ResponseParams> call, Response<ResponseParams> response) {
                if(response == null) {
                    Log.e("aaaaaa","error ~~~!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }
                String r = response.body().toString();
                final ResponseParams responseParams = response.body();
                if (responseParams == null) {
                    mWhereContractActivityView.validateFailure(null);
                    return;
                }
                Log.d("isSuccess : ", String.valueOf(response.isSuccessful()));
                Log.d("Status : ", response.body().getResponse().getStatus());
                Log.d("Status : ", responseParams.getResponse().getStatus());
                Log.d("Record : ", String.valueOf(responseParams.getResponse().getRecord().getTotal()));
                Log.d("result : ", responseParams.getResponse().getResult().getFeatureCollection().getFeatures().get(1).getProperties().getFull_nm());
                mWhereContractActivityView.validateSggSuccess(response.isSuccessful(), responseParams.getResponse().getRecord(), responseParams.getResponse().getResult());
            }

            @Override
            public void onFailure(Call<ResponseParams> call, Throwable t) {
                mWhereContractActivityView.validateFailure(null);
            }
        });
    }

    void getEmdList(String key,
                        String domain,
                        String request,
                        String format,
                        int size,
                        int page,
                        boolean geometry,
                        boolean attribute,
                        String crs,
                        String geomfilter,
                        String data) {
        final WhereRetrofitInterface whereRetrofitInterface = getRetrofit().create(WhereRetrofitInterface.class);
        //new WhereRequestBody(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data)
        whereRetrofitInterface.getEmdList(key, domain, request, format, size, page, geometry, attribute, crs, geomfilter, data).enqueue(new Callback<ResponseParams>() {
            @Override
            public void onResponse(Call<ResponseParams> call, Response<ResponseParams> response) {
                if(response == null) {
                    Log.e("aaaaaa","error ~~~!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                }
                String r = response.body().toString();
                final ResponseParams responseParams = response.body();
                if (responseParams == null) {
                    mWhereContractActivityView.validateFailure(null);
                    return;
                }
                Log.d("isSuccess : ", String.valueOf(response.isSuccessful()));
                Log.d("Status : ", response.body().getResponse().getStatus());
                Log.d("Status : ", responseParams.getResponse().getStatus());
                Log.d("Record : ", String.valueOf(responseParams.getResponse().getRecord().getTotal()));
                Log.d("result : ", responseParams.getResponse().getResult().getFeatureCollection().getFeatures().get(1).getProperties().getFull_nm());
                mWhereContractActivityView.validateEmdSuccess(response.isSuccessful(), responseParams.getResponse().getRecord(), responseParams.getResponse().getResult());
            }

            @Override
            public void onFailure(Call<ResponseParams> call, Throwable t) {
                mWhereContractActivityView.validateFailure(null);
            }
        });
    }

}
