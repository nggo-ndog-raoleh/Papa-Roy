package arkavidia.ljkeyboard.Model.Retrofit.City;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by axellageraldinc on 24/01/18.
 */

public class RajaOngkirResponse {
    @SerializedName("rajaongkir")
    @Expose
    private RajaOngkir rajaOngkir;

    /**
     * No args constructor for use in serialization
     *
     */
    public RajaOngkirResponse() {
    }

    /**
     *
     * @param rajaOngkir
     */
    public RajaOngkirResponse(RajaOngkir rajaOngkir) {
        super();
        this.rajaOngkir = rajaOngkir;
    }

    public RajaOngkir getRajaOngkir() {
        return rajaOngkir;
    }

    public void setRajaOngkir(RajaOngkir rajaOngkir) {
        this.rajaOngkir = rajaOngkir;
    }
}
