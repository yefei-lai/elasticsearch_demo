package com.example.es_demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieModel {

    private String id;

    private String name;

    private String country;

    private String type;

    private String director;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;


    public MovieModel(String name){
        this.name = name;
    }

}
