package com.example.monitoreoacua.service.request;

public class UserRequest extends BaseRequest {

   private String name;
   private String email;
   private String dni;
   private int id_module;
   private String address;
   private String contact;

   public UserRequest(String name, String email, String dni, int idModule, String address, String contact) {
       this.name = name;
       this.email = email;
       this.dni = dni;
       this.id_module = idModule;
       this.address = address;
       this.contact = contact;
   }

   // Getters and setters for each field
   public String getName() {
       return name;
   }

   public void setName(String name) {
       this.name = name;
   }

   public String getEmail() {
       return email;
   }

   public void setEmail(String email) {
       this.email = email;
   }

   public String getDni() {
       return dni;
   }

   public void setDni(String dni) {
       this.dni = dni;
   }

   public int getId_module() {
       return id_module;
   }

   public void setId_module(int id_module) {
       this.id_module = id_module;
   }

   public String getAddress() {
       return address;
   }

   public void setAddress(String address) {
       this.address = address;
   }

   public String getContact() {
       return contact;
   }

   public void setContact(String contact) {
       this.contact = contact;
   }

}
