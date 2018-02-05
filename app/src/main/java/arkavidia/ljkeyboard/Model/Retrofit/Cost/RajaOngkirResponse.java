package arkavidia.ljkeyboard.Model.Retrofit.Cost;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by axellageraldinc on 28/01/18.
 */

public class RajaOngkirResponse {

    @SerializedName("rajaongkir")
    @Expose
    private RajaOngkir rajaongkir;

    public RajaOngkir getRajaongkir() {
        return rajaongkir;
    }

    public void setRajaongkir(RajaOngkir rajaongkir) {
        this.rajaongkir = rajaongkir;
    }

}
