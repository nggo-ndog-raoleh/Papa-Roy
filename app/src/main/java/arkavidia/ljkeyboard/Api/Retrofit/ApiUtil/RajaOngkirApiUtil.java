package arkavidia.ljkeyboard.Api.Retrofit.ApiUtil;

import arkavidia.ljkeyboard.Api.Retrofit.RetrofitClient;
import arkavidia.ljkeyboard.Api.Retrofit.Service.RajaOngkirService;

/**
 * Created by axellageraldinc on 24/01/18.
 */

public class RajaOngkirApiUtil {

    public static final String BASE_URL = "https://api.rajaongkir.com/starter/";

    public static RajaOngkirService getRajaOngkirService() {
        return RetrofitClient.getClient(BASE_URL).create(RajaOngkirService.class);
    }

}
