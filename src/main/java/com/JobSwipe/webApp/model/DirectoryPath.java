package com.JobSwipe.webApp.model;

import com.JobSwipe.webApp.model.enums.CsvContentType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DirectoryPath {

    @Id
    public String path;

    public CsvContentType contentType;
}
