package com.example.demo.service.ai;

import com.example.demo.constant.FileType;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class DocumentTextExtractionService {

    public String extractText(MultipartFile file, FileType fileType) {
        try {
            return extractText(file.getBytes(), fileType);
        } catch (IOException e) {
            throw new AppException(ErrorCode.DOCUMENT_TEXT_EXTRACTION_FAILED);
        }
    }

    public String extractText(byte[] fileBytes, FileType fileType) {
        try {
            return switch (fileType) {
                case PDF -> extractPdfText(fileBytes);
                case DOCX -> extractDocxText(fileBytes);
            };
        } catch (IOException e) {
            throw new AppException(ErrorCode.DOCUMENT_TEXT_EXTRACTION_FAILED);
        }
    }

    private String extractPdfText(byte[] fileBytes) throws IOException {
        try (PDDocument document = PDDocument.load(fileBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractDocxText(byte[] fileBytes) throws IOException {
        try (XWPFDocument document = new XWPFDocument(new java.io.ByteArrayInputStream(fileBytes));
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }
}
