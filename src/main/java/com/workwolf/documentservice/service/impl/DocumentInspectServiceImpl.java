package com.workwolf.documentservice.service.impl;

import com.workwolf.documentservice.nlp.Annie;
import com.workwolf.documentservice.nlp.WorkExperienceField;
import com.workwolf.documentservice.property.DocumentStorageProperties;
import com.workwolf.documentservice.service.DocumentInspectService;
import gate.Document;
import gate.util.GateException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ToXMLContentHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;

import static gate.Utils.stringFor;

@Service
public class DocumentInspectServiceImpl implements DocumentInspectService {

    private final DocumentStorageProperties documentStorageProperties;

    @Autowired
    public DocumentInspectServiceImpl(DocumentStorageProperties documentStorageProperties) {
        this.documentStorageProperties = documentStorageProperties;
    }

    @Override
    public JSONObject inspect(String fileName) {
        Path documentPath = documentStorageProperties.getStorageLocation().resolve(fileName);
        try (InputStream inputStream = Files.newInputStream(documentPath)) {
            byte[] bytes = parseDocument(inputStream);
            File file = writeOutputDocument(fileName, bytes);
            return extractData(file);
        } catch (IOException | GateException e) {
            throw new RuntimeException("something went wrong");
        }
    }

    private byte[] parseDocument(InputStream inputStream) {
        ContentHandler contentHandler = new ToXMLContentHandler();
        AutoDetectParser parser = new AutoDetectParser();
        try {
            parser.parse(inputStream, contentHandler, new Metadata(), new ParseContext());
        } catch (IOException | SAXException | TikaException e) {
            throw new RuntimeException("something went wrong when parsing the document");
        }
        return contentHandler.toString().getBytes();
    }

    private File writeOutputDocument(String fileName, byte[] bytes) {
        String outputFileFormat = getFileFormatForGate(fileName);
        String outputFileName = FilenameUtils.removeExtension(fileName) + outputFileFormat;
        Path outputFilePath = documentStorageProperties.getStorageLocation().resolve(outputFileName);
        try {
            Files.write(outputFilePath, bytes);
        } catch (IOException e) {
            throw new RuntimeException("something went wrong when writing output file");
        }
        return outputFilePath.toFile();
    }

    @SuppressWarnings("unchecked")
    private JSONObject extractData(File file) throws GateException, IOException {
        Annie annie = Annie.createFrom(file);
        Iterator<Document> iter = annie.getCorpus().iterator();
        JSONObject parsedJSON = new JSONObject();
        if (iter.hasNext()) {
            Document doc = iter.next();
            extractEducation(doc, parsedJSON);
            extractWorkExperience(doc, parsedJSON);
        }
        return parsedJSON;
    }

    @SuppressWarnings("unchecked")
    private void extractEducation(Document doc, JSONObject parsedJSON) {
        var educationSection = "education_and_training";
        var defaultAnnotSet = doc.getAnnotations();
        var curAnnSet = defaultAnnotSet.get(educationSection);
        var iterator = curAnnSet.iterator();
        JSONArray subSections = new JSONArray();
        while (iterator.hasNext()) {
            JSONObject subSection = new JSONObject();
            var currAnnot = iterator.next();
            String key = (String) currAnnot.getFeatures().get("sectionHeading");
            String value = stringFor(doc, currAnnot);
            if (!StringUtils.isBlank(key) && !StringUtils.isBlank(value)) {
                subSection.put(key, value);
            }
            if (!subSection.isEmpty()) {
                subSections.add(subSection);
            }
        }
        if (!subSections.isEmpty()) {
            parsedJSON.put(educationSection, subSections);
        }
    }

    @SuppressWarnings("unchecked")
    private void extractWorkExperience(Document doc, JSONObject parsedJSON) {
        var workExperienceSection = "work_experience";
        var defaultAnnotSet = doc.getAnnotations();
        var curAnnSet = defaultAnnotSet.get(workExperienceSection);
        var iterator = curAnnSet.iterator();
        JSONArray workExperiences = new JSONArray();
        while (iterator.hasNext()) {
            JSONObject workExperience = new JSONObject();
            var currAnnot = iterator.next();
            String key = (String) currAnnot.getFeatures().get("sectionHeading");
            if (key.equals("work_experience_marker")) {
                Arrays.stream(WorkExperienceField.values()).forEach(field -> {
                    String v = (String) currAnnot.getFeatures().get(field.getFieldName());
                    if (!StringUtils.isBlank(v)) {
                        workExperience.put(field.getFieldName(), v);
                    }
                });
                key = "text";
            }
            String value = stringFor(doc, currAnnot);
            if (!StringUtils.isBlank(key) && !StringUtils.isBlank(value)) {
                workExperience.put(key, value);
            }
            if (!workExperience.isEmpty()) {
                workExperiences.add(workExperience);
            }

        }
        if (!workExperiences.isEmpty()) {
            parsedJSON.put(workExperienceSection, workExperiences);
        }
    }

    private String getFileFormatForGate(String fileName) {
        String ext = FilenameUtils.getExtension(fileName);
        String outputFileFormat = ".html";
        if (ext.equalsIgnoreCase("txt") || ext.equalsIgnoreCase("rtf")) {
            outputFileFormat = ".txt";
        }
        return outputFileFormat;
    }
}
