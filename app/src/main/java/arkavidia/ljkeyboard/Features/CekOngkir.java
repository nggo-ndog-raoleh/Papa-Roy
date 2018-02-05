package arkavidia.ljkeyboard.Features;

import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputConnection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arkavidia.ljkeyboard.Api.Retrofit.ApiUtil.RajaOngkirApiUtil;
import arkavidia.ljkeyboard.Api.Retrofit.Service.RajaOngkirService;
import arkavidia.ljkeyboard.Database.SqliteDbHelper;
import arkavidia.ljkeyboard.Model.Retrofit.City.RajaOngkirResponse;
import arkavidia.ljkeyboard.Model.Retrofit.City.Result;
import arkavidia.ljkeyboard.Model.Retrofit.Cost.Cost;
import arkavidia.ljkeyboard.Model.Retrofit.Cost.Costs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by axellageraldinc on 29/01/18.
 */

public class CekOngkir {

    private static final String TAG = "CekOngkir.class";

    private static final String API_KEY = "f669819773a9f6b26a3ae4188a3f1ab9";

    private String originId, destinationId;

    SqliteDbHelper sqliteDbHelper;
    RajaOngkirService rajaOngkirService;

    public CekOngkir(Context context) {
        sqliteDbHelper = new SqliteDbHelper(context);
        rajaOngkirService = RajaOngkirApiUtil.getRajaOngkirService();
    }

    public void execute(final String origin, final String destination, final String courier, final int weightInGrams, final InputConnection inputConnection){
        if(getRecordsCountFromSqlite()==0){
            rajaOngkirService.getAllCity(API_KEY).enqueue(new Callback<RajaOngkirResponse>() {
                @Override
                public void onResponse(Call<RajaOngkirResponse> call, Response<RajaOngkirResponse> response) {
                    List<Result> resultList = response.body().getRajaOngkir().getResults();
                    for (Result result:resultList
                            ) {
                        if(result.getCityName().toUpperCase().equals(origin.toUpperCase())){
                            originId = result.getCityId();
                        } else if(result.getCityName().toUpperCase().equals(destination.toUpperCase())){
                            destinationId = result.getCityId();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RajaOngkirResponse> call, Throwable t) {

                }
            });
        } else{
            HashMap<String, String> originAndDestinationIdMap = getCityIdFromSqlite(origin, destination);
            for(Map.Entry m:originAndDestinationIdMap.entrySet()){
                if(m.getKey().toString().toUpperCase().equals("ORIGIN")){
                    originId = m.getValue().toString();
                } if(m.getKey().toString().toUpperCase().equals("DESTINATION")){
                    destinationId = m.getValue().toString();
                }
            }
        }
        Log.i(TAG, "originId : " + originId + "\n" +
                "destionationId : " + destinationId + "\n" +
                "courier : " + courier + "\n" +
                "weight in grams : " + weightInGrams);
        rajaOngkirService.postCekOngkir(API_KEY, originId, destinationId, courier.toLowerCase(), weightInGrams).enqueue(new Callback<arkavidia.ljkeyboard.Model.Retrofit.Cost.RajaOngkirResponse>() {
            @Override
            public void onResponse(Call<arkavidia.ljkeyboard.Model.Retrofit.Cost.RajaOngkirResponse> call, Response<arkavidia.ljkeyboard.Model.Retrofit.Cost.RajaOngkirResponse> response) {
                if(response.isSuccessful()){
                    StringBuilder messageOngkir = new StringBuilder();
                    List<arkavidia.ljkeyboard.Model.Retrofit.Cost.Result> resultList = response.body().getRajaongkir().getResults();
                    for (arkavidia.ljkeyboard.Model.Retrofit.Cost.Result result:resultList
                            ) {
                        List<Costs> costsList = result.getCosts();
                        messageOngkir.append(origin + " - " + destination + "\n");
                        messageOngkir.append(weightInGrams + " gram\n");
                        for (Costs costs:costsList
                                ) {
                            List<Cost> costList = costs.getCost();
                            for (Cost cost:costList
                                    ) {
                                messageOngkir.append(courier + " " + costs.getService() + " | Rp " + cost.getValue() + "\n");
                            }
                        }
                    }
                    messageOngkir.setLength(messageOngkir.length()-1);
                    inputConnection.commitText(messageOngkir.toString(), 1);
                } else {
                    Log.e(TAG, "Error post cek ongkir : " + response.code() + "," + response.message());
                    Log.e(TAG, response.body().getRajaongkir().getStatus().getDescription());
                }
            }

            @Override
            public void onFailure(Call<arkavidia.ljkeyboard.Model.Retrofit.Cost.RajaOngkirResponse> call, Throwable t) {
                Log.e(TAG, "Failed cek ongkir : " + t.toString());
            }
        });
    }

    public int getRecordsCountFromSqlite(){
        return sqliteDbHelper.countDataTableCity();
    }

    private HashMap<String, String> getCityIdFromSqlite(String origin, String destination){
        HashMap<String, String> param = sqliteDbHelper.getOriginIdAndDestinationId(origin, destination);
        Log.i(TAG, "Get city id via sqlite");
        return param;
    }

}
