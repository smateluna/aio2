package cl.cbrs.aio.parametron;

import cl.parametron.binding.implement.Parametron;

public class ParamsFirmaElectronica extends Parametron {
    private String serviciosFirmaElectronicaUrl;

    public ParamsFirmaElectronica() {
    }

    public String getServiciosFirmaElectronicaUrl() {
        return serviciosFirmaElectronicaUrl;
    }

    public void setServiciosFirmaElectronicaUrl(String serviciosFirmaElectronicaUrl) {
        this.serviciosFirmaElectronicaUrl = serviciosFirmaElectronicaUrl;
    }
}
