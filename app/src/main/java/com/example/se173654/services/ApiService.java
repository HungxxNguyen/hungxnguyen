package com.example.se173654.services;

import com.example.se173654.manager_author.Author;
import com.example.se173654.manager_book.Book;
import com.example.se173654.manager_major.Major;
import com.example.se173654.manager_student.Student;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @GET("Tacgia")
    Call<List<Author>> getTacGiaList();

    @POST("Tacgia")
    Call<Author> addTacGia(@Body Author author);

    @PUT("Tacgia/{id}")
    Call<Author> updateTacGia(@Path("id") String id, @Body Author author);

    @DELETE("Tacgia/{id}")
    Call<Void> deleteTacGia(@Path("id") String id);

    @GET("sach")
    Call<List<Book>> getSachList();

    @DELETE("sach/{id}")
    Call<Void> deleteSach(@Path("id") String id);

    @PUT("sach/{id}")
    Call<Book> updateSach(@Path("id") String id, @Body Book book);

    @POST("sach")
    Call<Book> addSach(@Body Book book);

    @GET("major")
    Call<List<Major>> getMajorList();

    @POST("major")
    Call<Major> addMajor(@Body Major major);

    @PUT("major/{id}")
    Call<Major> updateMajor(@Path("id") String id, @Body Major major);

    @DELETE("major/{id}")
    Call<Void> deleteMajor(@Path("id") String id);

    @GET("student")
    Call<List<Student>> getStudentList();

    @POST("student")
    Call<Student> addStudent(@Body Student student);

    @PUT("student/{id}")
    Call<Student> updateStudent(@Path("id") String id, @Body Student student);

    @DELETE("student/{id}")
    Call<Void> deleteStudent(@Path("id") String id);
}
