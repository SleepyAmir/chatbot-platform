package com.example.platform.modules.ocr.repository;


import com.example.platform.mongo.document.OcrImageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OcrImageRepository extends MongoRepository<OcrImageDocument, String> {

    List<OcrImageDocument> findAllByOrderByCreatedAtDesc();
}