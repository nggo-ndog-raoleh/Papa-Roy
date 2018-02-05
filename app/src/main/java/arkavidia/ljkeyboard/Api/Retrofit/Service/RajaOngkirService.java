package arkavidia.ljkeyboard.Api.Retrofit.Service;

import arkavidia.ljkeyboard.Model.Retrofit.City.RajaOngkirResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by axellageraldinc on 24/01/18.
 */

public interface RajaOngkirService {

    @GET("city")
    Call<RajaOngkirResponse> getAllCity(@Header("key") String apiKey);

    @FormUrlEncoded
    @POST("cost")
    Call<arkavidia.ljkeyboard.Model.Retrofit.Cost.RajaOngkirResponse> postCekOngkir(@Header("key") String apiKey,
                                                                                    @Field("origin") String originId,
                                                                                    @Field("destination") String destinationId,
                                                                                    @Field("courier") String courier,
                                                                                    @Field("weight") int weight);

}
