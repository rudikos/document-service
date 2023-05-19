package com.workwolf.documentservice.nlp;

import gate.Corpus;
import gate.CorpusController;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Annie {

    @Getter
    private Corpus corpus;
    private CorpusController annieController;

    public static Annie createFrom(File file) throws GateException, IOException {
        Annie annie = new Annie();
        annie.initAnnie();
        annie.createCorpus(file);
        annie.execute();
        return annie;
    }

    private void initAnnie() throws GateException, IOException {
        File gateHome = Gate.getGateHome();
        File annieGapp = new File(gateHome, "ANNIEResumeParser.gapp");
        annieController = (CorpusController) PersistenceManager.loadObjectFromFile(annieGapp);
    }

    private void createCorpus(File file) throws ResourceInstantiationException, MalformedURLException {
        corpus = Factory.newCorpus("Annie corpus");
        URL u = file.toURI().toURL();
        FeatureMap params = Factory.newFeatureMap();
        params.put("sourceUrl", u);
        params.put("preserveOriginalContent", true);
        params.put("collectRepositioningInfo", true);
        Document resume = (Document) Factory.createResource("gate.corpora.DocumentImpl", params);
        corpus.add(resume);
    }

    private void execute() throws GateException {
        annieController.setCorpus(corpus);
        annieController.execute();
    }
}
