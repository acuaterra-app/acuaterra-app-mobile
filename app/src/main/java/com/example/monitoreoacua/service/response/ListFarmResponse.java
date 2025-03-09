package com.example.monitoreoacua.service.response;

import com.example.monitoreoacua.business.models.Farm;

import java.util.List;

public class ListFarmResponse extends ApiResponse<ListFarmResponse.Data>{

    //private String message;
    //private List<String> errors;
    //private Map<String, Object> meta;

    //public String getMessage() { return message; }
    //public List<String> getErrors() { return errors; }
    //public Map<String, Object> getMeta() { return meta; }

    /**
     * Obtiene la primera granja en la lista de datos.
     * @return Un objeto Farm o null si la lista está vacía.
     */
    public Farm getFirstFarm() {
        Data data = getFirstDataItem();
        return data != null ? data.getFarm() : null;
    }

    /**
     * Obtiene la lista de todas las granjas en la respuesta.
     * @return Lista de granjas.
     */
    public List<Farm> getAllFarms() {
        Data data = getFirstDataItem();
        return data != null ? data.getFarms() : null;
    }

    /**
     * Clase interna que representa la estructura del campo "data" en la respuesta.
     */
    public static class Data {
        private List<Farm> farms;

        public List<Farm> getFarms() {
            return farms;
        }

        public Farm getFarm() {
            return (farms != null && !farms.isEmpty()) ? farms.get(0) : null;
        }
    }
}
