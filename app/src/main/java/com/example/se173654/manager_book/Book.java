package com.example.se173654.manager_book;

public class Book {
    private String id; // Đổi từ Id thành id
    private String TenSach;
    private String NgayXb;
    private String Theloai;
    private String IdTacgia;

    // Constructor mặc định (yêu cầu bởi Retrofit)
    public Book() {}

    // Getter và Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenSach() {
        return TenSach;
    }

    public void setTenSach(String tenSach) {
        TenSach = tenSach;
    }

    public String getNgayXb() {
        return NgayXb;
    }

    public void setNgayXb(String ngayXb) {
        NgayXb = ngayXb;
    }

    public String getTheloai() {
        return Theloai;
    }

    public void setTheloai(String theloai) {
        Theloai = theloai;
    }

    public String getIdTacgia() {
        return IdTacgia;
    }

    public void setIdTacgia(String idTacgia) {
        IdTacgia = idTacgia;
    }
}